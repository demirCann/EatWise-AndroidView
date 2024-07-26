package com.example.network.model

import com.example.model.MealList
import kotlinx.serialization.Serializable

@Serializable
data class MealResponse(
    val number: Int,
    val offset: Int,
    val results: List<Result>,
    val totalResults: Int
)

fun MealResponse.toMealList() = MealList(
    number = number,
    offset = offset,
    results = results.map { it.toInfo() },
    totalResults = totalResults
)
