package com.example.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chat.databinding.ItemChatMessageBinding

class ChatAdapter : ListAdapter<MessageFromGemini, ChatAdapter.ChatViewHolder>(DiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ChatViewHolder {
        val binding =
            ItemChatMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = getItem(position)
        holder.bind(message)
    }

    inner class ChatViewHolder(private val binding: ItemChatMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(messageFromGemini: MessageFromGemini) {
            binding.apply {
                message = messageFromGemini
            }
        }
    }

    class DiffCallBack : DiffUtil.ItemCallback<MessageFromGemini>() {
        override fun areItemsTheSame(
            oldItem: MessageFromGemini,
            newItem: MessageFromGemini
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: MessageFromGemini,
            newItem: MessageFromGemini
        ): Boolean {
            return oldItem == newItem
        }
    }
}