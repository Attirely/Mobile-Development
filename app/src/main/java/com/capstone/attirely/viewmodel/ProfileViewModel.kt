package com.capstone.attirely.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.attirely.data.Content
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class ProfileViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _favoritesList = MutableLiveData<List<Pair<String, Content>>>()
    val favoritesList: LiveData<List<Pair<String, Content>>> = _favoritesList

    private val _closetList = MutableLiveData<List<Content>>()
    val closetList: LiveData<List<Content>> = _closetList

    private val _favorites = MutableLiveData<Set<String>>()
    val favorites: LiveData<Set<String>> = _favorites

    private val _isLoadingFavorites = MutableLiveData<Boolean>()
    val isLoadingFavorites: LiveData<Boolean> = _isLoadingFavorites

    init {
        fetchFavorites()
        fetchCloset()
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

    private fun fetchCloset() {
        // Simulate fetching closet items from a data source
        _closetList.value = listOf(
            // Add your closet content data here
        )
    }

    fun toggleFavorite(contentId: String, content: Content) {
        val user = auth.currentUser ?: return
        val favoritesCollection = db.collection("users").document(user.uid).collection("favorite")
        val docRef = favoritesCollection.document(contentId)

        if (_favorites.value?.contains(contentId) == true) {
            docRef.delete().addOnSuccessListener {
                _favorites.value = _favorites.value?.minus(contentId)
                fetchFavorites() // Refresh the favorites list
            }
        } else {
            docRef.set(content).addOnSuccessListener {
                _favorites.value = _favorites.value?.plus(contentId)
                fetchFavorites() // Refresh the favorites list
            }
        }
    }
}
