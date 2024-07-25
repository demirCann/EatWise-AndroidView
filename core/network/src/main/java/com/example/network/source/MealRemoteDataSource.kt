package com.example.network.source

import com.example.network.model.MealDetailResponse
import com.example.network.model.MealResponse
import com.example.util.ApiResult
import kotlinx.coroutines.flow.Flow

interface MealRemoteDataSource {
    fun getMealsForTypes(type: String, number: Int): Flow<ApiResult<MealResponse>>
    fun getMealsForDiet(diet: String, number: Int): Flow<ApiResult<MealResponse>>
    fun getMealDetails(id: Int): Flow<ApiResult<MealDetailResponse>>
    fun searchRecipes(query: String): Flow<ApiResult<MealResponse>>
}