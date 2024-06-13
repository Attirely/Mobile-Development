package com.capstone.attirely.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.attirely.data.Outfit
import com.capstone.attirely.data.Content
import com.capstone.attirely.retrofit.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val _outfits = MutableStateFlow<List<Outfit>>(emptyList())
    val outfits: StateFlow<List<Outfit>> = _outfits.asStateFlow()

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _genderFilter = MutableStateFlow("all")
    val genderFilter: StateFlow<String> = _genderFilter.asStateFlow()

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    init {
        fetchFilteredOutfits("all")
        fetchFavorites()
    }

    private fun fetchFilteredOutfits(gender: String) {
        viewModelScope.launch {
            val outfits = when (gender) {
                "male" -> RetrofitInstance.api.getCowokOutfits()
                "female" -> RetrofitInstance.api.getCewekOutfits()
                else -> {
                    val cowokOutfits = RetrofitInstance.api.getCowokOutfits()
                    val cewekOutfits = RetrofitInstance.api.getCewekOutfits()
                    cowokOutfits + cewekOutfits
                }
            }
            _outfits.value = outfits
        }
    }

    fun fetchFavorites() {
        val user = auth.currentUser ?: return
        db.collection("users").document(user.uid).collection("favorite")
            .get()
            .addOnSuccessListener { result ->
                val favorites = result.map { document ->
                    document["imageUrl"].toString() // Use imageUrl as the unique identifier
                }.toSet()
                _favorites.value = favorites
            }
    }

    fun toggleFavorite(outfit: Outfit) {
        val user = auth.currentUser ?: return
        val favoritesCollection = db.collection("users").document(user.uid).collection("favorite")
        val docRef = favoritesCollection.document(generateDocumentId(outfit.imageurl))

        if (_favorites.value.contains(outfit.imageurl)) { // Use imageurl as unique identifier
            docRef.delete().addOnSuccessListener {
                _favorites.value = _favorites.value - outfit.imageurl
            }
        } else {
            val content = outfit.toContent()
            docRef.set(content).addOnSuccessListener {
                _favorites.value = _favorites.value + outfit.imageurl
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateGenderFilter(gender: String) {
        _genderFilter.value = gender
        fetchFilteredOutfits(gender)
        fetchFavorites() // Ensure favorites are fetched whenever the gender filter is updated
    }

    private fun generateDocumentId(input: String): String {
        return input.hashCode().toString() // Generate a valid document ID
    }

    val filteredOutfits: StateFlow<List<Outfit>> = combine(_outfits, _searchQuery) { outfits, query ->
        if (query.isBlank()) {
            outfits
        } else {
            outfits.filter { outfit ->
                outfit.classes.any { it.contains(query, ignoreCase = true) }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
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
