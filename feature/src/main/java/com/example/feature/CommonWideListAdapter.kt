package com.example.feature

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.database.model.FavoriteMealEntity
import com.example.feature.databinding.MealItemLayoutBinding
import com.example.model.Info

class MealsTypeAdapter(
    private val isWide: Boolean,
    private val onClickedItem: (Int) -> Unit,
    private val onFavoriteClicked: (Info) -> Unit
) : ListAdapter<Info, MealsTypeAdapter.MealTypeViewHolder>(MealItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealTypeViewHolder {
        val binding =
            MealItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MealTypeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealTypeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MealTypeViewHolder(private val binding: MealItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(meal: Info) {
            binding.apply {
                mealTitle.text = meal.title
                favoriteButton.isFavorite = meal.isFavorite
                mealImage.load(meal.image) {
                    placeholder(R.drawable.placeholder)
                    error(R.drawable.error)
                }
                root.setOnClickListener {
                    onClickedItem(meal.id)
                }
                favoriteButton.setOnClickListener {
                    meal.isFavorite = !meal.isFavorite
                    favoriteButton.isFavorite = meal.isFavorite
                    onFavoriteClicked(meal)
                }

                val layoutParams = binding.root.layoutParams as ViewGroup.MarginLayoutParams
                if (isWide) {
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                } else {
                    layoutParams.width = 150.dpToPx(binding.root.context)
                    layoutParams.height = 250.dpToPx(binding.root.context)
                }
            }
        }
    }

    class MealItemDiffCallback : DiffUtil.ItemCallback<Info>() {
        override fun areItemsTheSame(oldItem: Info, newItem: Info): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Info, newItem: Info): Boolean {
            return oldItem == newItem
        }
    }
}

fun FavoriteMealEntity.toInfo() = Info(
    id = id,
    image = image,
    imageType = imageType,
    title = title,
    isFavorite = true
)