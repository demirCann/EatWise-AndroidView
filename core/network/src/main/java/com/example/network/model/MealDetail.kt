package com.example.network.model

import com.example.model.Info

data class MealDetail(
    val id: Int = 0,
    val title: String = "",
    val image: String = "",
    val imageType: String = "",
    val summary: String = "",
    val analyzedInstructions: List<AnalyzedInstruction> = emptyList(),
    val extendedIngredients: List<ExtendedIngredient> = emptyList(),
    var isFavorite: Boolean = false
)

fun MealDetail.toInfo() = Info(
    id = id,
    image = image,
    imageType = imageType,
    title = title
)
