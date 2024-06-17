import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreManager(private val context: Context) {

    companion object {
        private val Context.dataStore by preferencesDataStore("attirely_datastore")
        val IMAGE_URLS_KEY = stringPreferencesKey("image_urls")
        val CATEGORIES_KEY = stringPreferencesKey("categories")
    }

    val imageUrls: Flow<List<String>> = context.dataStore.data
        .map { preferences ->
            preferences[IMAGE_URLS_KEY]?.split(",") ?: emptyList()
        }

    val categories: Flow<List<String>> = context.dataStore.data
        .map { preferences ->
            preferences[CATEGORIES_KEY]?.split(",") ?: emptyList()
        }

    suspend fun saveImageUrls(urls: List<String>) {
        context.dataStore.edit { preferences ->
            preferences[IMAGE_URLS_KEY] = urls.joinToString(",")
        }
    }

    suspend fun saveCategories(categories: List<String>) {
        context.dataStore.edit { preferences ->
            preferences[CATEGORIES_KEY] = categories.joinToString(",")
        }
    }

    suspend fun clearData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
