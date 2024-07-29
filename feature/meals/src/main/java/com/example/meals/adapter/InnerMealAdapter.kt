package com.example.meals.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.feature.R
import com.example.feature.databinding.MealItemLayoutBinding
import com.example.feature.dpToPx
import com.example.model.Info

class InnerMealAdapter(
    private val meals: List<Info>,
    private val onClickedMealItem: (Int) -> Unit,
    private val onFavoriteClicked: (Info) -> Unit
) : RecyclerView.Adapter<InnerMealAdapter.MealViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val binding = MealItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MealViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        holder.bind(meals[position])
    }

    override fun getItemCount(): Int = meals.size

    inner class MealViewHolder(private val binding: MealItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(meal: Info) {
            binding.apply {
                mealTitle.text = meal.title
                favoriteButton.isFavorite = meal.isFavorite
                mealImage.load(meal.image) {
                    placeholder(R.drawable.placeholder)
                    error(R.drawable.error)
                }
                root.setOnClickListener {
                    onClickedMealItem(meal.id)
                }
                favoriteButton.setOnClickListener {
                    meal.isFavorite = !meal.isFavorite
                    favoriteButton.isFavorite = meal.isFavorite
                    onFavoriteClicked(meal)
                }

                val layoutParams = binding.root.layoutParams as ViewGroup.MarginLayoutParams

                layoutParams.width = 150.dpToPx(binding.root.context)
                layoutParams.height = 250.dpToPx(binding.root.context)
            }
        }
    }
}
