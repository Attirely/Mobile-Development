package com.capstone.attirely.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.WindowManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.capstone.attirely.MainActivity
import com.capstone.attirely.data.ClosetItem
import com.capstone.attirely.data.Content
import com.capstone.attirely.data.User
import com.capstone.attirely.datastore.DataStoreManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStoreManager = DataStoreManager(application)
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val loginResult = MutableLiveData<Result<GoogleSignInAccount>>()
    val firebaseAuthResult = MutableLiveData<Result<FirebaseUser>>()

    private val _favoritesList = MutableStateFlow<List<Pair<String, Content>>>(emptyList())
    val favoritesList: StateFlow<List<Pair<String, Content>>> = _favoritesList

    private val _closetItems = MutableStateFlow<List<ClosetItem>>(emptyList())
    val closetItems: StateFlow<List<ClosetItem>> = _closetItems

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites

    private val _isLoadingFavorites = MutableStateFlow(false)
    val isLoadingFavorites: StateFlow<Boolean> = _isLoadingFavorites

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode: StateFlow<Boolean> = _isSelectionMode

    private val _selectedClosetItems = MutableStateFlow<List<ClosetItem>>(emptyList())
    val selectedClosetItems: StateFlow<List<ClosetItem>> = _selectedClosetItems

    init {
        fetchUserDetails()
        fetchFavorites()
        fetchCloset()
    }

    fun signOut(context: Context) {
        firebaseAuth.signOut()
        val activity = context as? MainActivity
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        activity?.window?.decorView?.animate()?.alpha(0f)?.setDuration(500)?.withEndAction {
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            activity.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            activity.finish()
        }?.start()
    }


    private fun fetchUserDetails() {
        val currentUser = auth.currentUser ?: return
        val user = User(
            avatarUrl = currentUser.photoUrl?.toString() ?: "",
            username = currentUser.displayName ?: "",
            email = currentUser.email ?: ""
        )
        _user.value = user
    }

    fun fetchFavorites() {
        _isLoadingFavorites.value = true
        val user = auth.currentUser ?: return
        db.collection("users").document(user.uid).collection("favorite")
            .get()
            .addOnSuccessListener { result ->
                val favorites = result.map { document ->
                    document.id to document.toObject(Content::class.java)
                }
                _favoritesList.value = favorites
                _favorites.value = favorites.map { it.first }.toSet()
                _isLoadingFavorites.value = false
            }
            .addOnFailureListener {
                _isLoadingFavorites.value = false
            }
    }

    fun fetchCloset() {
        val user = auth.currentUser ?: return
        db.collection("users").document(user.uid).collection("closet")
            .get()
            .addOnSuccessListener { result ->
                val closetItems = result.map { document ->
                    document.toObject(ClosetItem::class.java).copy(id = document.id)
                }
                _closetItems.value = closetItems
            }
    }

    fun toggleFavorite(contentId: String, content: Content) {
        val user = auth.currentUser ?: return
        val favoritesCollection = db.collection("users").document(user.uid).collection("favorite")
        val docRef = favoritesCollection.document(contentId)

        if (_favorites.value.contains(contentId)) {
            docRef.delete().addOnSuccessListener {
                _favorites.value = _favorites.value - contentId
                fetchFavorites()
            }
        } else {
            docRef.set(content).addOnSuccessListener {
                _favorites.value = _favorites.value + contentId
                fetchFavorites()
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun activateSelectionMode() {
        _isSelectionMode.value = true
    }

    fun toggleSelectClosetItem(item: ClosetItem) {
        _selectedClosetItems.value = if (_selectedClosetItems.value.contains(item)) {
            _selectedClosetItems.value - item
        } else {
            _selectedClosetItems.value + item
        }
    }

    fun deleteClosetItem(item: ClosetItem) {
        val user = auth.currentUser ?: return
        val storageRef = storage.getReferenceFromUrl(item.imageUrl)

        storageRef.delete().addOnSuccessListener {
            db.collection("users").document(user.uid).collection("closet")
                .document(item.id)
                .delete()
                .addOnSuccessListener {
                    fetchCloset()
                    _selectedClosetItems.value = _selectedClosetItems.value - item
                }
        }.addOnFailureListener {
            Log.e("DeleteClosetItem", "Failed to delete item image: ${it.message}")
        }
    }

    fun updateClosetItemText(itemId: String, newText: String) {
        val user = auth.currentUser ?: return
        db.collection("users").document(user.uid).collection("closet")
            .document(itemId)
            .update("text", newText)
            .addOnSuccessListener {
                fetchCloset()
            }
            .addOnFailureListener {
                Log.e("UpdateClosetItemText", "Failed to update item text: ${it.message}")
            }
    }

    fun navigateToSearch(navController: NavController) {
        navController.navigate("search") {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun saveImageUrls(urls: List<String>) {
        viewModelScope.launch {
            dataStoreManager.saveImageUrls(urls)
        }
    }

    fun saveCategories(categories: List<String>) {
        viewModelScope.launch {
            dataStoreManager.saveCategories(categories)
        }
    }

    val filteredClosetItems: StateFlow<List<ClosetItem>> =
        combine(_closetItems, _searchQuery) { items, query ->
            if (query.isBlank()) {
                items
            } else {
                items.filter {
                    it.text.contains(query, ignoreCase = true) || it.category.contains(
                        query,
                        ignoreCase = true
                    )
                }
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
