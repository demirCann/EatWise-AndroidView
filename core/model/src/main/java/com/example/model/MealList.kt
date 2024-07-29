package com.example.model

data class MealList(
    val number: Int,
    val offset: Int,
    val results: List<Info>,
    val totalResults: Int
)
