package com.example.network.model

import kotlinx.serialization.Serializable

@Serializable
data class Metric(
    val amount: Double = 0.0,
    val unitLong: String = "",
    val unitShort: String = ""
)