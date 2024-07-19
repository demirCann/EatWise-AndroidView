package com.example.database.source

import com.example.database.model.FavoriteMealEntity
import com.example.database.util.DaoResult
import kotlinx.coroutines.flow.Flow

interface FavoriteLocalDataSource {
    fun getAllFavorites(): Flow<DaoResult<List<FavoriteMealEntity>>>
    suspend fun addToFavorites(favoriteMealEntity: FavoriteMealEntity)
    suspend fun removeMealFromFavorites(id: Int)
}