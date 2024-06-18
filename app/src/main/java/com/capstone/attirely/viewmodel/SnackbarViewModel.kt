package com.capstone.attirely.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.attirely.data.SnackbarState

class SnackbarViewModel : ViewModel() {
    private val _snackbarState = MutableLiveData<SnackbarState>()
    val snackbarState: LiveData<SnackbarState> = _snackbarState

    fun showSnackbar(title: String, message: String, color: Color) {
        _snackbarState.value = SnackbarState(title, message, color)
    }

    fun clearSnackbar() {
        _snackbarState.value = null
    }
}