package com.capstone.attirely.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AddResultViewModel : ViewModel() {
    private val _categoryState = mutableStateOf(mapOf<String, String>())
    val categoryState: State<Map<String, String>> get() = _categoryState

    fun updateCategory(imageUri: String, category: String) {
        _categoryState.value = _categoryState.value.toMutableMap().apply {
            this[imageUri] = category
        }
    }
}