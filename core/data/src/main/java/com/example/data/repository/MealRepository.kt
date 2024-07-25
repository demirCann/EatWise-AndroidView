package com.example.data.repository

import com.example.util.DaoResult
import com.example.model.FavoriteMeal
import com.example.model.Meal
import com.example.network.model.MealDetailResponse
import com.example.util.ApiResult
import kotlinx.coroutines.flow.Flow

interface MealRepository {
    fun getMealsForTypes(type: String, number: Int): Flow<ApiResult<Meal>>
    fun getMealsForDiet(diet: String, number: Int): Flow<ApiResult<Meal>>
    fun getMealDetails(id: Int): Flow<ApiResult<MealDetailResponse>>
    fun searchRecipes(query: String): Flow<ApiResult<Meal>>
    fun getAllFavorites(): Flow<DaoResult<List<FavoriteMeal>>>
    suspend fun addToFavorites(favoriteMeal: FavoriteMeal)
    suspend fun removeMealFromFavorites(id: Int)
    fun searchFavoritesByName(searchQuery: String): Flow<DaoResult<List<FavoriteMeal>>>
}