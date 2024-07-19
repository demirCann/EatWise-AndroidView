package com.example.model

data class Meal(
    val number: Int,
    val offset: Int,
    val results: List<Info>,
    val totalResults: Int
)
