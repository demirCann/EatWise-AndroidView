package com.example.network.model

import kotlinx.serialization.Serializable

@Serializable
data class Measures(
    val metric: Metric = Metric(),
    val us: Us = Us()
)