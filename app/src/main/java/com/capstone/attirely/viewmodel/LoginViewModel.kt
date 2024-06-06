package com.capstone.attirely.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val oneTapClient: SignInClient = Identity.getSignInClient(application)
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val signInResult = MutableLiveData<Result<BeginSignInResult>>()
    val loginResult = MutableLiveData<Result<SignInCredential>>()

    private val signInRequest: BeginSignInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId("358775350563-cll50vefba4eeged3bf8j5en4svb5plq.apps.googleusercontent.com")  // Ensure this matches your Firebase project settings
                .setFilterByAuthorizedAccounts(false)
                .build()
        )
        .build()

    fun beginSignIn() {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                signInResult.postValue(Result.success(result))
            }
            .addOnFailureListener { e ->
                Log.e("SignInError", "Error starting sign-in: ${e.message}")
                signInResult.postValue(Result.failure(e))
            }
    }

    fun handleSignInResult(credential: SignInCredential) {
        val idToken = credential.googleIdToken
        if (idToken != null) {
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        loginResult.postValue(Result.success(credential))
                    } else {
                        Log.e("SignInError", "Error during Firebase sign-in: ${task.exception?.message}")
                        loginResult.postValue(Result.failure(task.exception ?: Exception("Sign-in failed")))
                    }
                }
        } else {
            Log.e("SignInError", "No ID token!")
            loginResult.postValue(Result.failure(Exception("No ID token!")))
        }
    }
}