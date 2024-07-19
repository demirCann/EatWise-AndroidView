package com.example.network.retrofit

import com.example.network.model.MealDetailResponse
import com.example.network.model.MealResponse
import com.example.network.util.Constants.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MealApi {
    @GET("recipes/complexSearch")
    suspend fun getMealsForTypes(
        @Query("type") type: String = "main course",
        @Query("number") number: Int = 30,
        @Query("apiKey") apiKey: String = API_KEY
    ): Response<MealResponse>

    @GET("recipes/complexSearch")
    suspend fun getMealsForDiet(
        @Query("diet") type: String = "main course",
        @Query("number") number: Int = 30,
        @Query("apiKey") apiKey: String = API_KEY
    ): Response<MealResponse>

    @GET("recipes/{id}/information")
    suspend fun getMealDetails(
        @Path("id") id: Int,
        @Query("apiKey") apiKey: String = API_KEY
    ): Response<MealDetailResponse>

    @GET("recipes/complexSearch")
    suspend fun searchRecipes(
        @Query("query") query: String,
        @Query("number") number: Int = 20,
        @Query("apiKey") apiKey: String = API_KEY
    ): Response<MealResponse>

}