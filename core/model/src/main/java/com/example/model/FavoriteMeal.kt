package com.example.model

import com.example.database.model.FavoriteMealEntity


data class FavoriteMeal(
    val id: Int,
    val image: String,
    val imageType: String,
    val title: String
)

fun FavoriteMeal.toEntity() = FavoriteMealEntity(
    id = id,
    image = image,
    imageType = imageType,
    title = title
)