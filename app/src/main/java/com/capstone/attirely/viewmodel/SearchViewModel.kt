package com.capstone.attirely.viewmodel

import DataStoreManager
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.attirely.data.Content
import com.capstone.attirely.data.Outfit
import com.capstone.attirely.retrofit.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStoreManager = DataStoreManager(application)

    private val _outfits = MutableStateFlow<List<Outfit>>(emptyList())
    val outfits: StateFlow<List<Outfit>> = _outfits

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _genderFilter = MutableStateFlow("all")
    val genderFilter: StateFlow<String> = _genderFilter

    private val _selectedCategories = MutableStateFlow<List<String>>(emptyList())
    val selectedCategories: StateFlow<List<String>> = _selectedCategories

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    val imageUrls: StateFlow<List<String>> = dataStoreManager.imageUrls.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val categories: StateFlow<List<String>> = dataStoreManager.categories.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

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
                    document["imageUrl"].toString()
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
        fetchFavorites()
    }

    fun updateSelectedCategories(categories: List<String>) {
        _selectedCategories.value = categories
    }

    private fun generateDocumentId(input: String): String {
        return input.hashCode().toString()
    }

    val filteredOutfits: StateFlow<List<Outfit>> = combine(_outfits, _searchQuery, _selectedCategories) { outfits, query, categories ->
        outfits.filter { outfit ->
            val matchesQuery = query.isBlank() || outfit.classes.any { it.contains(query, ignoreCase = true) }
            val matchesCategory = categories.isEmpty() || outfit.classes.any { it in categories }

            // Determine if there is a topper and an under category
            val topperCategories = setOf("White Shirt")
            val underCategories = setOf("Brown Skirt")

            val hasTopper = categories.any { it in topperCategories }
            val hasUnder = categories.any { it in underCategories }

            if (hasTopper && hasUnder) {
                val combinedClasses = categories.map { category ->
                    outfit.classes.any { it.contains(category, ignoreCase = true) }
                }
                combinedClasses.all { it } // Check if all selected categories are present in the classes
            } else {
                matchesQuery && matchesCategory
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun saveImageUrls(urls: List<String>) {
        viewModelScope.launch {
            dataStoreManager.saveImageUrls(urls)
        }
    }

    fun saveCategories(categories: List<String>) {
        viewModelScope.launch {
            dataStoreManager.saveCategories(categories)
        }
    }

    fun clearData() {
        viewModelScope.launch {
            dataStoreManager.clearData()
        }
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