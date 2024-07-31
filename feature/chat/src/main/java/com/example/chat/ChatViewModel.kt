package com.example.chat

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat.Constants.DEFAULT_PROMPT
import com.example.chat.Constants.FULL_PROMPT_FORMAT
import com.example.chat.Constants.GEMINI_1_5_FLASH
import com.example.chat.Constants.GEMINI_API_KEY
import com.example.chat.Constants.GEMINI_PRO
import com.example.feature.utils.getTimestamp
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private var _messageState = MutableStateFlow(UiState())
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

    private val generativeModelVision by lazy { createGenerativeModel(GEMINI_1_5_FLASH) }
    private val generativeModelText by lazy { createGenerativeModel(GEMINI_PRO) }

    private val safetySettings by lazy {
        listOf(
            harassmentSafety,
            sexualContentSafety,
            hateSpeechSafety
        )
    }

    fun sendMessageToGemini(message: String, image: Bitmap? = null) = viewModelScope.launch {
        _messageState.update {
            val currentMessages = it.messagesGemini.orEmpty()
            val addedMessage =
                currentMessages + MessageFromGemini(message, true, getTimestamp(), image)
            it.copy(messagesGemini = addedMessage)
        }
    }

    fun receiveMessageFromGemini() = viewModelScope.launch {
        val newMessage = _messageState.value.messagesGemini?.last()?.text
        val systemMessage = DEFAULT_PROMPT
        val fullPrompt = "$systemMessage\n\n$FULL_PROMPT_FORMAT $newMessage"
        val response = generativeModelText.generateContent(
            prompt = fullPrompt
        )
        if (response.text != null) {
            _messageState.update {
                val addedMessage = it.messagesGemini?.plus(
                    MessageFromGemini(
                        response.text!!,
                        false,
                        getTimestamp()
                    )
                )
                it.copy(messagesGemini = addedMessage)
            }
        }
    }

    fun receiveImageResponseFromGemini(bitmap: Bitmap) = viewModelScope.launch {
        val inputContent = content {
            image(bitmap)
            _messageState.value.messagesGemini?.last()?.text?.let { text(it) }
        }
        val response = generativeModelVision.generateContent(
            inputContent
        )
        if (response.text != null) {
            _messageState.update {
                val addedMessage = it.messagesGemini?.plus(
                    MessageFromGemini(
                        response.text!!,
                        false,
                        getTimestamp()
                    )
                )
                it.copy(messagesGemini = addedMessage)
            }
        }
    }
}

data class UiState(
    val messagesGemini: List<MessageFromGemini>? = null
)