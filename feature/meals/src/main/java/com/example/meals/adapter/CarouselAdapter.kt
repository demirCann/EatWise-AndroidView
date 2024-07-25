package com.example.meals.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.meals.databinding.CarouselItemBinding
import com.example.meals.model.CarouselItemData

class CarouselAdapter(
    private val items: List<CarouselItemData>,
    private val onClickedItem: (String) -> Unit
) : RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CarouselAdapter.CarouselViewHolder {
        val binding = CarouselItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarouselViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarouselAdapter.CarouselViewHolder, position: Int) {
        holder.bind(items[position], onClickedItem)
    }

    override fun getItemCount(): Int = items.size

    inner class CarouselViewHolder(private val binding: CarouselItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CarouselItemData, onClickedItem: (String) -> Unit) {
            binding.mealName.text = item.mealName
            binding.mealImage.setImageResource(item.mealImageUri)
            binding.root.setOnClickListener {
                onClickedItem(item.diet)
            }
        }
    }
}