package com.example.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.detail.databinding.DetailItemBinding

class DetailIngredientsAdapter : RecyclerView.Adapter<DetailIngredientsAdapter.DetailViewHolder>() {

    private var items: List<String> = emptyList()

    fun submitList(newItems: List<String>) {
        items = newItems
        notifyDataSetChanged()
    }

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
        val itemText = items[position]
        holder.bind(itemText)
    }

    override fun getItemCount(): Int = items.size

    inner class DetailViewHolder(private val binding: DetailItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(itemText: String) {
            binding.apply {
                itemList.text = itemText
            }
        }
    }
}