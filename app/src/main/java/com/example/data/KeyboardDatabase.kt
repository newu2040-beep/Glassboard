package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "clipboard_items")
data class ClipboardItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val category: String = "Text" // "Text", "URL", "Email", "Phone"
)

@Entity(tableName = "custom_themes")
data class CustomTheme(
    @PrimaryKey val id: String,
    val displayName: String,
    val isDark: Boolean,
    val backgroundColor: String, // Hex string
    val glassColor: String, // Hex string
    val keyColor: String, // Hex string
    val textColor: String, // Hex string
    val accentColor: String, // Hex string
    val glassOpacity: Float,
    val blurIntensity: Float,
    val reflectionIntensity: Float,
    val shadowDepth: Float,
    val cornerRadius: Int
)

@Dao
interface ClipboardDao {
    @Query("SELECT * FROM clipboard_items ORDER BY isPinned DESC, timestamp DESC")
    fun getAllClips(): Flow<List<ClipboardItem>>

    @Query("SELECT * FROM clipboard_items WHERE category = :category ORDER BY isPinned DESC, timestamp DESC")
    fun getClipsByCategory(category: String): Flow<List<ClipboardItem>>

    @Query("SELECT * FROM clipboard_items WHERE content LIKE '%' || :query || '%' ORDER BY isPinned DESC, timestamp DESC")
    fun searchClips(query: String): Flow<List<ClipboardItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClip(item: ClipboardItem)

    @Update
    suspend fun updateClip(item: ClipboardItem)

    @Delete
    suspend fun deleteClip(item: ClipboardItem)

    @Query("DELETE FROM clipboard_items WHERE id = :id")
    suspend fun deleteClipById(id: Int)

    @Query("DELETE FROM clipboard_items")
    suspend fun clearAllClips()

    @Query("DELETE FROM clipboard_items WHERE isPinned = 0")
    suspend fun clearUnpinnedClips()
}

@Dao
interface CustomThemeDao {
    @Query("SELECT * FROM custom_themes")
    fun getAllCustomThemes(): Flow<List<CustomTheme>>

    @Query("SELECT * FROM custom_themes WHERE id = :id LIMIT 1")
    suspend fun getThemeById(id: String): CustomTheme?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTheme(theme: CustomTheme)

    @Delete
    suspend fun deleteTheme(theme: CustomTheme)
}

@Database(entities = [ClipboardItem::class, CustomTheme::class], version = 1, exportSchema = false)
abstract class KeyboardDatabase : RoomDatabase() {
    abstract fun clipboardDao(): ClipboardDao
    abstract fun customThemeDao(): CustomThemeDao

    companion object {
        @Volatile
        private var INSTANCE: KeyboardDatabase? = null

        fun getDatabase(context: android.content.Context): KeyboardDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KeyboardDatabase::class.java,
                    "glassboard_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
