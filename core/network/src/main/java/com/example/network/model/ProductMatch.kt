package com.example.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductMatch(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val price: String = "",
    val imageUrl: String = "",
    val averageRating: Double = 0.0,
    val ratingCount: Double? = null,
    val score: Double = 0.0,
    val link: String = ""
)