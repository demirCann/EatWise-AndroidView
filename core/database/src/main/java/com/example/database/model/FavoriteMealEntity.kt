package com.example.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.database.util.Constants.FAVORITES_TABLE

@Entity(tableName = FAVORITES_TABLE)
data class FavoriteMealEntity(
    @PrimaryKey
    val id: Int,
    val image: String,
    val imageType: String,
    val title: String
)