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

    private var lastDocument: DocumentSnapshot? = null
    private var isLoading = false

    init {
        fetchContent()
    }

    fun fetchContent() {
        if (isLoading) return
        isLoading = true

        var query = db.collection("content")
            .limit(8)

        lastDocument?.let {
            query = query.startAfter(it)
        }

        query.get()
            .addOnSuccessListener { result ->
                val contents = result.map { document ->
                    document.toObject(Content::class.java)
                }
                lastDocument = result.documents.lastOrNull()

                val currentList = _contentList.value.orEmpty().toMutableList()
                currentList.addAll(contents)
                _contentList.value = currentList

                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }
}
