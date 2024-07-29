package com.example.meals.model

import com.example.model.Info

sealed class MealItem {
    data class Carousel(val items: List<CarouselItemData>) : MealItem()
    data class MealSection(val type: MealType, val meals: List<Info>) : MealItem()
}
