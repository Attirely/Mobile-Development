package com.capstone.attirely.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.attirely.data.Outfit
import com.capstone.attirely.data.Content
import com.capstone.attirely.retrofit.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class SearchViewModel : ViewModel() {
    private val _outfits = MutableStateFlow<List<Outfit>>(emptyList())
    val outfits: StateFlow<List<Outfit>> = _outfits.asStateFlow()

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    init {
        fetchOutfits()
        fetchFavorites()
    }

    private fun fetchOutfits() {
        viewModelScope.launch {
            val cowokOutfits = RetrofitInstance.api.getCowokOutfits().map { it.toContent() }
            val cewekOutfits = RetrofitInstance.api.getCewekOutfits().map { it.toContent() }
            _outfits.value = (cowokOutfits + cewekOutfits).map { it.toOutfit() }
        }
    }

    fun fetchFavorites() {
        val user = auth.currentUser ?: return
        db.collection("users").document(user.uid).collection("favorite")
            .get()
            .addOnSuccessListener { result ->
                val favorites = result.map { document ->
                    document.id // Assuming the document ID is the unique identifier
                }.toSet()
                _favorites.value = favorites
            }
    }

    fun toggleFavorite(outfit: Outfit) {
        val user = auth.currentUser ?: return
        val favoritesCollection = db.collection("users").document(user.uid).collection("favorite")
        val docRef = favoritesCollection.document(generateDocumentId(outfit.imageurl))

        if (_favorites.value.contains(outfit.filename)) {
            docRef.delete().addOnSuccessListener {
                _favorites.value = _favorites.value - outfit.filename
            }
        } else {
            val content = outfit.toContent()
            docRef.set(content).addOnSuccessListener {
                _favorites.value = _favorites.value + outfit.filename
            }
        }
    }

    private fun generateDocumentId(input: String): String {
        return input.hashCode().toString() // Generate a valid document ID
    }
}

fun Outfit.toContent(): Content {
    return Content(
        title = this.classes.joinToString(", "),
        imageUrl = this.imageurl
    )
}

fun Content.toOutfit(): Outfit {
    return Outfit(
        filename = this.imageUrl.hashCode().toString(), // Generate a unique filename based on the hash of the imageUrl
        imageurl = this.imageUrl,
        classes = this.title.split(", ")
    )
}