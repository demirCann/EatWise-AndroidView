package com.example.database.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.database.model.FavoriteMealEntity
import kotlinx.coroutines.flow.Flow

interface FavoriteDao {
    @Query("SELECT * FROM favorites_table")
    fun getAllFavorites(): Flow<List<FavoriteMealEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorites(favoriteMealEntity: FavoriteMealEntity)

    @Query("DELETE FROM favorites_table WHERE id = :id")
    suspend fun removeMealFromFavorites(id: Int)
}