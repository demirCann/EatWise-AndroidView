package com.example.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.detail.databinding.DetailItemBinding

class DetailIngredientsAdapter :
    ListAdapter<String, DetailIngredientsAdapter.DetailViewHolder>(IngredientDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailIngredientsAdapter.DetailViewHolder {
        val binding = DetailItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: DetailIngredientsAdapter.DetailViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class DetailViewHolder(private val binding: DetailItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(itemText: String) {
            binding.apply {
                itemList.text = itemText
            }
        }
    }

    class IngredientDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}