package com.example.network.retrofit

import com.example.network.model.MealDetailResponse
import com.example.network.model.MealResponse
import com.example.network.util.Constants.API_KEY
import com.example.network.util.Constants.DEFAULT_MEAL_NUMBER
import com.example.network.util.Constants.GET_MEALS
import com.example.network.util.Constants.GET_MEAL_DETAILS
import com.example.network.util.Constants.MAIN_COURSE
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MealApi {
    @GET(GET_MEALS)
    suspend fun getMealsForTypes(
        @Query("type") type: String = MAIN_COURSE,
        @Query("number") number: Int = DEFAULT_MEAL_NUMBER,
        @Query("apiKey") apiKey: String = API_KEY
    ): Response<MealResponse>

    @GET(GET_MEALS)
    suspend fun getMealsForDiet(
        @Query("diet") type: String = MAIN_COURSE,
        @Query("number") number: Int = DEFAULT_MEAL_NUMBER,
        @Query("apiKey") apiKey: String = API_KEY
    ): Response<MealResponse>

    @GET(GET_MEAL_DETAILS)
    suspend fun getMealDetails(
        @Path("id") id: Int,
        @Query("apiKey") apiKey: String = API_KEY
    ): Response<MealDetailResponse>

    @GET(GET_MEALS)
    suspend fun searchRecipes(
        @Query("query") query: String,
        @Query("number") number: Int = DEFAULT_MEAL_NUMBER,
        @Query("apiKey") apiKey: String = API_KEY
    ): Response<MealResponse>

}