package com.capstone.attirely.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.attirely.data.Content
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class ContentViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _contentList = MutableLiveData<List<Pair<String, Content>>>()
    val contentList: LiveData<List<Pair<String, Content>>> = _contentList

    private val _newestContentList = MutableLiveData<List<Pair<String, Content>>>()
    val newestContentList: LiveData<List<Pair<String, Content>>> = _newestContentList

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isLoadingNewest = MutableLiveData(false)
    val isLoadingNewest: LiveData<Boolean> = _isLoadingNewest

    private val _favorites = MutableLiveData<Set<String>>()
    val favorites: LiveData<Set<String>> = _favorites

    private var lastDocument: DocumentSnapshot? = null
    private var isFetching = false

    init {
        fetchContent()
        fetchFavorites()
    }

    fun fetchContent() {
        if (isFetching || lastDocument == null && _contentList.value?.isNotEmpty() == true) return
        isFetching = true
        _isLoading.value = true

        var query = db.collection("content")
            .limit(8)

        lastDocument?.let {
            query = query.startAfter(it)
        }

        query.get()
            .addOnSuccessListener { result ->
                val contents = result.map { document ->
                    document.id to document.toObject(Content::class.java)
                }.shuffled()

                lastDocument = result.documents.lastOrNull()

                val currentList = _contentList.value.orEmpty().toMutableList()
                currentList.addAll(contents)

                _contentList.value = currentList
                _isLoading.value = false
                isFetching = false
            }
            .addOnFailureListener {
                _isLoading.value = false
                isFetching = false
            }
    }

    fun fetchNewestContent() {
        _isLoadingNewest.value = true

        db.collection("content")
            .limit(10)
            .get()
            .addOnSuccessListener { result ->
                val contents = result.map { document ->
                    document.id to document.toObject(Content::class.java)
                }
                _newestContentList.value = contents
                _isLoadingNewest.value = false
            }
            .addOnFailureListener {
                _isLoadingNewest.value = false
            }
    }

    fun fetchFavorites() {
        val user = auth.currentUser ?: return
        db.collection("users").document(user.uid).collection("favorite")
            .get()
            .addOnSuccessListener { result ->
                val favorites = result.map { it.id }.toSet()
                _favorites.value = favorites
            }
    }

    fun toggleFavorite(contentId: String, content: Content) {
        val user = auth.currentUser ?: return
        val favoritesCollection = db.collection("users").document(user.uid).collection("favorite")
        val docRef = favoritesCollection.document(contentId)

        if (_favorites.value?.contains(contentId) == true) {
            docRef.delete().addOnSuccessListener {
                _favorites.value = _favorites.value?.minus(contentId)
            }
        } else {
            docRef.set(content).addOnSuccessListener {
                _favorites.value = _favorites.value?.plus(contentId)
            }
        }
    }
}
