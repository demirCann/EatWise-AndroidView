package com.example.network.source

import com.example.network.model.MealDetailResponse
import com.example.network.model.MealResponse
import com.example.network.retrofit.MealApi
import com.example.util.ApiResult
import com.example.util.apiFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MealRemoteDataSourceImpl @Inject constructor(
    private val mealApi: MealApi
) : MealRemoteDataSource {
    override fun getMealsForTypes(type: String, number: Int): Flow<ApiResult<MealResponse>> =
        apiFlow {
            mealApi.getMealsForTypes(type, number)
        }

    override fun getMealsForDiet(diet: String, number: Int): Flow<ApiResult<MealResponse>> =
        apiFlow {
            mealApi.getMealsForDiet(diet, number)
        }

    override fun getMealDetails(id: Int): Flow<ApiResult<MealDetailResponse>> = apiFlow {
        mealApi.getMealDetails(id)
    }

    override fun searchRecipes(query: String): Flow<ApiResult<MealResponse>> = apiFlow {
        mealApi.searchRecipes(query)
    }
}