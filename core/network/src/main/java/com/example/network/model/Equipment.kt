package com.example.network.model

import kotlinx.serialization.Serializable

@Serializable
data class Equipment(
    val id: Int = 0,
    val name: String = "",
    val image: String = ""
)