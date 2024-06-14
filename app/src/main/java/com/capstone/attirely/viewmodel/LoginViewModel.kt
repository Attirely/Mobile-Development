package com.capstone.attirely.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val loginResult = MutableLiveData<Result<GoogleSignInAccount>>()
    val firebaseAuthResult = MutableLiveData<Result<FirebaseUser>>()
    private val oneTapClient: SignInClient = Identity.getSignInClient(application)

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

    fun getOneTapClient(): SignInClient = oneTapClient
}