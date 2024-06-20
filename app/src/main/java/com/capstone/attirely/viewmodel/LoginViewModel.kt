package com.capstone.attirely.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val firebaseAuthResult = MutableLiveData<Result<FirebaseUser>>()
    private val oneTapClient: SignInClient = Identity.getSignInClient(application)
    val isLoggedIn = MutableLiveData<Boolean>()

    fun handleSignInResult(tokenId: String) {
        viewModelScope.launch {
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
                        isLoggedIn.postValue(true)
                    } else {
                        firebaseAuthResult.postValue(Result.failure(Exception("User is null")))
                        isLoggedIn.postValue(false)
                    }
                } else {
                    firebaseAuthResult.postValue(Result.failure(task.exception ?: Exception("Unknown error")))
                    isLoggedIn.postValue(false)
                }
            }
    }

    fun getOneTapClient(): SignInClient = oneTapClient

    fun updateLoginState(isLoggedIn: Boolean) {
        this.isLoggedIn.postValue(isLoggedIn)
    }
}