package com.capstone.attirely.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.attirely.data.Content
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

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

        val query = db.collection("content")
            .limit(8)

        val actualQuery = lastDocument?.let {
            query.startAfter(it)
        } ?: query

        actualQuery.get()
            .addOnSuccessListener { result ->
                val contents = result.map { document ->
                    document.toObject(Content::class.java)
                }
                lastDocument = if (result.documents.isNotEmpty()) {
                    result.documents.last()
                } else {
                    null
                }

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