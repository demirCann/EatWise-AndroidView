package com.example.network.model

import kotlinx.serialization.Serializable

@Serializable
data class Step(
    val number: Int = 0,
    val step: String = "",
    val ingredients: List<Ingredient> = emptyList(),
    val equipment: List<Equipment> = emptyList()
)