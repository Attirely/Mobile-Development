import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.attirely.data.ClosetItem
import com.capstone.attirely.data.Content
import com.capstone.attirely.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _favoritesList = MutableStateFlow<List<Pair<String, Content>>>(emptyList())
    val favoritesList: StateFlow<List<Pair<String, Content>>> = _favoritesList

    private val _closetItems = MutableStateFlow<List<ClosetItem>>(emptyList())
    val closetItems: StateFlow<List<ClosetItem>> = _closetItems

    private val _selectedClosetItems = MutableStateFlow<List<ClosetItem>>(emptyList())
    val selectedClosetItems: StateFlow<List<ClosetItem>> = _selectedClosetItems

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites

    private val _isLoadingFavorites = MutableStateFlow(false)
    val isLoadingFavorites: StateFlow<Boolean> = _isLoadingFavorites

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode: StateFlow<Boolean> = _isSelectionMode

    init {
        fetchUserDetails()
        fetchFavorites()
        fetchCloset()
    }

    private fun fetchUserDetails() {
        val currentUser = auth.currentUser ?: return
        val user = User(
            avatarUrl = currentUser.photoUrl?.toString() ?: "",
            username = currentUser.displayName ?: "",
            email = currentUser.email ?: ""
        )
        _user.value = user
    }

    fun fetchFavorites() {
        _isLoadingFavorites.value = true
        val user = auth.currentUser ?: return
        db.collection("users").document(user.uid).collection("favorite")
            .get()
            .addOnSuccessListener { result ->
                val favorites = result.map { document ->
                    document.id to document.toObject(Content::class.java)
                }
                _favoritesList.value = favorites
                _favorites.value = favorites.map { it.first }.toSet()
                _isLoadingFavorites.value = false
            }
            .addOnFailureListener {
                _isLoadingFavorites.value = false
            }
    }

    fun fetchCloset() {
        val user = auth.currentUser ?: return
        db.collection("users").document(user.uid).collection("closet")
            .get()
            .addOnSuccessListener { result ->
                val closetItems = result.map { document ->
                    document.toObject(ClosetItem::class.java)
                }
                _closetItems.value = closetItems
            }
            .addOnFailureListener {
                // Handle the error
            }
    }

    fun toggleFavorite(contentId: String, content: Content) {
        val user = auth.currentUser ?: return
        val favoritesCollection = db.collection("users").document(user.uid).collection("favorite")
        val docRef = favoritesCollection.document(contentId)

        if (_favorites.value.contains(contentId)) {
            docRef.delete().addOnSuccessListener {
                _favorites.value = _favorites.value - contentId
                fetchFavorites() // Refresh the favorites list
            }
        } else {
            docRef.set(content).addOnSuccessListener {
                _favorites.value = _favorites.value + contentId
                fetchFavorites() // Refresh the favorites list
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    val filteredClosetItems: StateFlow<List<ClosetItem>> = combine(_closetItems, _searchQuery) { items, query ->
        if (query.isBlank()) {
            items
        } else {
            items.filter { it.text.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun toggleSelectClosetItem(item: ClosetItem) {
        _selectedClosetItems.value = if (_selectedClosetItems.value.contains(item)) {
            _selectedClosetItems.value - item
        } else {
            _selectedClosetItems.value + item
        }
    }

    fun activateSelectionMode() {
        _isSelectionMode.value = true
    }

    fun deactivateSelectionMode() {
        _isSelectionMode.value = false
        _selectedClosetItems.value = emptyList()
    }
}