package com.example.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.detail.databinding.DetailItemBinding

class DetailInstructionsAdapter : ListAdapter<String, DetailInstructionsAdapter.DetailViewHolder>(InstructionDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailInstructionsAdapter.DetailViewHolder {
        val binding = DetailItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailInstructionsAdapter.DetailViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DetailViewHolder(private val binding: DetailItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(itemText: String) {
            binding.apply {
                itemList.text = itemText
            }
        }
    }

    class InstructionDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}