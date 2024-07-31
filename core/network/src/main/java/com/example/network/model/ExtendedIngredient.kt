package com.example.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExtendedIngredient(
    val aisle: String? = null,
    val amount: Double = 0.0,
    @SerialName("consitency") val consistency: String? = null,
    val id: Int = 0,
    val image: String = "",
    val measures: Measures = Measures(),
    val meta: List<String> = emptyList(),
    val name: String = "",
    val original: String = "",
    val originalName: String = "",
    val unit: String = ""
)