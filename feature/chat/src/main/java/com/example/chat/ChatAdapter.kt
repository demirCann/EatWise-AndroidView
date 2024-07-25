package com.example.chat

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.chat.databinding.ItemChatMessageBinding

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private var messages: List<MessageFromGemini> = emptyList()

    fun submitList(newMessages: List<MessageFromGemini>) {
        messages = newMessages
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ChatViewHolder {
        val binding =
            ItemChatMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)
    }

    override fun getItemCount(): Int = messages.size

    inner class ChatViewHolder(private val binding: ItemChatMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: MessageFromGemini) {
            // Bind the message to the view
            binding.apply {
                if (message.isUser) {
                    Log.d(
                        "ChatAdapter",
                        "User message visibility: ${userMessageTextView.isVisible}"
                    )
                    userMessageTextView.text = message.text
                    userMessageTextView.visibility = View.VISIBLE
                    aiMessageTextView.visibility = View.GONE
                    if (message.image != null) {
                        userMessageImageView.setImageBitmap(message.image)
                        userMessageImageView.visibility = View.VISIBLE
                    } else {
                        userMessageImageView.visibility = View.GONE
                    }
                } else {
                    aiMessageTextView.text = message.text
                    userMessageCardView.visibility = View.GONE
                    aiMessageTextView.visibility = View.VISIBLE
                }
            }
        }
    }
}