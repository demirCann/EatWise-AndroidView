package com.example.data.repository

import com.example.database.util.DaoResult
import com.example.model.FavoriteMeal
import com.example.model.Meal
import com.example.model.MealDetail
import com.example.network.model.MealDetailResponse
import com.example.network.model.MealResponse
import com.example.network.util.ApiResult
import kotlinx.coroutines.flow.Flow

interface MealRepository {
    fun getMealsForTypes(type: String, number: Int): Flow<ApiResult<Meal>>
    fun getMealsForDiet(diet: String, number: Int): Flow<ApiResult<Meal>>
    fun getMealDetails(id: Int): Flow<ApiResult<MealDetail>>
    fun searchRecipes(query: String): Flow<ApiResult<Meal>>
    fun getAllFavorites(): Flow<DaoResult<List<FavoriteMeal>>>
    suspend fun addToFavorites(favoriteMeal: FavoriteMeal)
    suspend fun removeMealFromFavorites(id: Int)
}