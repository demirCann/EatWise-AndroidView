package com.example.data.repository

import com.example.database.model.FavoriteMealEntity
import com.example.model.FavoriteMeal
import com.example.network.model.MealDetailResponse
import com.example.network.model.MealResponse
import com.example.util.ApiResult
import com.example.util.DaoResult
import kotlinx.coroutines.flow.Flow

interface MealRepository {
    fun getMealsForTypes(type: String, number: Int): Flow<ApiResult<MealResponse>>
    fun getMealsForDiet(diet: String, number: Int): Flow<ApiResult<MealResponse>>
    fun getMealDetails(id: Int): Flow<ApiResult<MealDetailResponse>>
    fun searchRecipes(query: String): Flow<ApiResult<MealResponse>>
    fun getAllFavorites(): Flow<DaoResult<List<FavoriteMealEntity>>>
    suspend fun addToFavorites(favoriteMeal: FavoriteMeal)
    suspend fun removeMealFromFavorites(id: Int)
    fun searchFavoritesByName(searchQuery: String): Flow<DaoResult<List<FavoriteMealEntity>>>
    suspend fun isFavorite(mealId: Int): Boolean
}