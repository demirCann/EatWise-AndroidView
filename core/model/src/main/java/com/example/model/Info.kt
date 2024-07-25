package com.example.model

data class Info(
    val id: Int,
    val image: String,
    val imageType: String,
    val title: String
)

fun Info.toFavoriteMeal(): FavoriteMeal {
    return FavoriteMeal(
        id = id,
        image = image,
        imageType = imageType,
        title = title
    )
}
