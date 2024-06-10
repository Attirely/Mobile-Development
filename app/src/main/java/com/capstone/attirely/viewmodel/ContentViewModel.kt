package com.capstone.attirely.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.attirely.data.Content
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class ContentViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _contentList = MutableLiveData<List<Content>>()
    val contentList: LiveData<List<Content>> = _contentList

    private val _newestContentList = MutableLiveData<List<Content>>()
    val newestContentList: LiveData<List<Content>> = _newestContentList

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isLoadingNewest = MutableLiveData(false)
    val isLoadingNewest: LiveData<Boolean> = _isLoadingNewest

    private var lastDocument: DocumentSnapshot? = null
    private var isFetching = false

    init {
        fetchContent()
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
                    document.toObject(Content::class.java)
                }.shuffled()

                lastDocument = result.documents.lastOrNull()

                val currentList = _contentList.value.orEmpty().toMutableList()

                contents.forEach { content ->
                    if (!currentList.contains(content)) {
                        currentList.add(content)
                    }
                }

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
                    document.toObject(Content::class.java)
                }
                _newestContentList.value = contents
                _isLoadingNewest.value = false
            }
            .addOnFailureListener {
                _isLoadingNewest.value = false
            }
    }
}
