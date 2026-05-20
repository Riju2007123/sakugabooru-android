package com.sakuga.app.data.local.dao

import androidx.room.*
import com.sakuga.app.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorites ORDER BY savedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT id FROM favorites")
    fun getAllFavoriteIds(): Flow<Set<Int>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :postId)")
    suspend fun isFavorited(postId: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE id = :postId")
    suspend fun delete(postId: Int)
}
