package com.example.chat

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat.Constants.GEMINI_API_KEY
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

class ChatViewModel : ViewModel() {

    private var _messageState = MutableStateFlow(listOf<MessageFromGemini>())
    val messageState = _messageState.asStateFlow()

    private fun createGenerativeModel(modelName: String) = GenerativeModel(
        modelName = modelName,
        apiKey = GEMINI_API_KEY,
        safetySettings = safetySettings
    )

    private val harassmentSafety = SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.ONLY_HIGH)
    private val sexualContentSafety =
        SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.ONLY_HIGH)
    private val hateSpeechSafety =
        SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE)

    private val generativeModelVision by lazy { createGenerativeModel("gemini-1.5-flash") }
    private val generativeModelText by lazy { createGenerativeModel("gemini-pro") }

    private val safetySettings by lazy {
        listOf(
            harassmentSafety,
            sexualContentSafety,
            hateSpeechSafety
        )
    }

    fun sendMessageToGemini(message: String, image: Bitmap? = null) = viewModelScope.launch {
        _messageState.value += MessageFromGemini(message, true, getTimestamp(), image)
    }

    fun receiveMessageFromGemini() = viewModelScope.launch {
        val newMessage = _messageState.value.last().text

        val systemMessage =
            "Sen bir yemek uzmanısın. Sadece yemek tarifleri, malzemeler, besin değerleri ve yemekle ilgili diğer konular hakkında cevap ver. Başka konularla ilgili sorulara yanıt verme."
        val fullPrompt = "$systemMessage\n\nKullanıcının sorusu: $newMessage"

        val response = generativeModelText.generateContent(
            prompt = fullPrompt
        )
        if (response.text != null) {
            _messageState.value += MessageFromGemini(response.text!!, false, getTimestamp())
        }
        Log.d("ChatScreen", "Message received from Gemini: ${_messageState.value}")
    }

    fun receiveImageResponseFromGemini(bitmap: Bitmap) = viewModelScope.launch {
        val inputContent = content {
            image(bitmap)
            text(_messageState.value.last().text)
        }

        val response = generativeModelVision.generateContent(
            inputContent
        )
        if (response.text != null) {
            _messageState.value += MessageFromGemini(response.text!!, false, getTimestamp())
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getTimestamp(): String {
        val sdf = SimpleDateFormat("HH:mm aa")
        return sdf.format(Date())
    }

}