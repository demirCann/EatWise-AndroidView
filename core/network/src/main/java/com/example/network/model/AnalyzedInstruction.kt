package com.example.network.model

import kotlinx.serialization.Serializable

@Serializable
data class AnalyzedInstruction(
    val name: String = "",
    val steps: List<Step> = emptyList()
)
