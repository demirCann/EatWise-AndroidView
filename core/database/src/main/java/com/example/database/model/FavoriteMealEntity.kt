package com.example.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.database.util.Constants.FAVORITES_TABLE
import com.example.model.FavoriteMeal

@Entity(tableName = FAVORITES_TABLE)
data class FavoriteMealEntity(
    @PrimaryKey
    val id: Int,
    val image: String,
    val imageType: String,
    val title: String
)

fun FavoriteMealEntity.toFavoriteMeal() = FavoriteMeal(
    id = id,
    image = image,
    imageType = imageType,
    title = title
)

fun List<FavoriteMealEntity>.toFavoriteMealList() = map { it.toFavoriteMeal() }

fun FavoriteMeal.toEntity() = FavoriteMealEntity(
    id = id,
    image = image,
    imageType = imageType,
    title = title
)
