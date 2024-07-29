package com.example.chat

import android.graphics.Bitmap
import java.util.UUID

data class MessageFromGemini(
    val text: String,
    val isUser: Boolean,
    val timestamp: String,
    val image: Bitmap? = null,
    val id: String = UUID.randomUUID().toString()
)