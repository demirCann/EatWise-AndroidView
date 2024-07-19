package com.example.network.model

import com.example.model.Meal
import kotlinx.serialization.Serializable

@Serializable
data class MealResponse(
    val number: Int,
    val offset: Int,
    val results: List<Result>,
    val totalResults: Int
)

fun MealResponse.toMeal() = Meal(
    number = number,
    offset = offset,
    results = results.map { it.toInfo() },
    totalResults = totalResults
)
