package com.example.network.model

import com.example.model.Info
import kotlinx.serialization.Serializable


@Serializable
data class Result(
    val id: Int,
    val image: String,
    val imageType: String,
    val title: String
)

fun Result.toInfo() = Info(
    id = id,
    image = image,
    imageType = imageType,
    title = title
)