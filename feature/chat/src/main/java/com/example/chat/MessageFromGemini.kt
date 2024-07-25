package com.example.chat

import android.graphics.Bitmap

data class MessageFromGemini(
    val text: String,
    val isUser: Boolean,
    val timestamp: String,
    val image: Bitmap? = null
)