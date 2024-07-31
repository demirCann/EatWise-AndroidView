package com.example.network.model

import kotlinx.serialization.Serializable

@Serializable
data class WinePairing(
    val pairedWines: List<String> = emptyList(),
    val pairingText: String = "",
    val productMatches: List<ProductMatch> = emptyList()
)