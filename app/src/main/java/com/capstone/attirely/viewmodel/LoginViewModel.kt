package com.capstone.attirely.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.attirely.R
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.stevdzasan.onetap.OneTapSignInState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val loginResult = MutableLiveData<Result<GoogleSignInAccount>>()
    val firebaseAuthResult = MutableLiveData<Result<FirebaseUser>>()

    fun handleSignInResult(tokenId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                firebaseAuthWithGoogle(tokenId)
            } catch (e: Exception) {
                firebaseAuthResult.postValue(Result.failure(e))
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        firebaseAuthResult.postValue(Result.success(user))
                    } else {
                        firebaseAuthResult.postValue(Result.failure(Exception("User is null")))
                    }
                } else {
                    firebaseAuthResult.postValue(Result.failure(task.exception ?: Exception("Unknown error")))
                }
            }
    }
}