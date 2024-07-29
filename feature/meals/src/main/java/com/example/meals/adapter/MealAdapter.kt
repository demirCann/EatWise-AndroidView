package com.example.meals.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meals.databinding.CarouselLayoutBinding
import com.example.meals.databinding.MealSectionLayoutBinding
import com.example.meals.model.MealItem
import com.example.model.Info
import com.google.android.material.carousel.CarouselLayoutManager

class MealAdapter(
    private val onClickedCarouselItem: (String) -> Unit,
    private val onClickedMealItem: (Int) -> Unit,
    private val onFavoriteClicked: (Info) -> Unit,
    private val onClickedSeeAll: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: MutableList<MealItem> = mutableListOf()

    fun submitList(newItems: List<MealItem>) {
        items = newItems.toMutableList()
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is MealItem.Carousel -> VIEW_TYPE_CAROUSEL
            is MealItem.MealSection -> VIEW_TYPE_MEAL_SECTION
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_CAROUSEL -> CarouselViewHolder(
                CarouselLayoutBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )

            VIEW_TYPE_MEAL_SECTION -> MealSectionViewHolder(
                MealSectionLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is CarouselViewHolder -> holder.bind(item as MealItem.Carousel)
            is MealSectionViewHolder -> holder.bind(item as MealItem.MealSection)
        }
    }

    override fun getItemCount(): Int = items.size

    companion object {
        private const val VIEW_TYPE_CAROUSEL = 0
        private const val VIEW_TYPE_MEAL_SECTION = 1
    }

    inner class CarouselViewHolder(private val binding: CarouselLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MealItem.Carousel) {
            val carouselAdapter = CarouselAdapter(item.items) {
                onClickedCarouselItem(it)
            }
            binding.carouselRecyclerView.apply {
                layoutManager = CarouselLayoutManager()
                adapter = carouselAdapter
            }
        }
    }

    inner class MealSectionViewHolder(private val binding: MealSectionLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MealItem.MealSection) {
            // Meal section binding logic here
            binding.mealTypeTitle.text = item.type.type.replaceFirst(
                item.type.type[0],
                item.type.type[0].uppercaseChar()
            )

            val mealAdapter = InnerMealAdapter(item.meals, onClickedMealItem, onFavoriteClicked)
            binding.mealRecyclerView.apply {
                layoutManager =
                    LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
                adapter = mealAdapter
            }

            binding.seeAllText.setOnClickListener {
                onClickedSeeAll(item.type.type)
            }
        }
    }
}
