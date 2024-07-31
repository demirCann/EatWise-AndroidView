package com.example.network.model

import kotlinx.serialization.Serializable

@Serializable
data class MealDetailResponse(
    val id: Int = 0,
    val title: String = "",
    val image: String = "",
    val imageType: String = "",
    val servings: Int = 0,
    val readyInMinutes: Int = 0,
    val license: String? = null,
    val sourceName: String = "",
    val sourceUrl: String = "",
    val spoonacularSourceUrl: String = "",
    val healthScore: Double = 0.0,
    val spoonacularScore: Double = 0.0,
    val pricePerServing: Double = 0.0,
    val analyzedInstructions: List<AnalyzedInstruction> = emptyList(),
    val cheap: Boolean = false,
    val creditsText: String = "",
    val cuisines: List<String> = emptyList(),
    val dairyFree: Boolean = false,
    val diets: List<String> = emptyList(),
    val gaps: String = "",
    val glutenFree: Boolean = false,
    val instructions: String = "",
    val ketogenic: Boolean = false,
    val lowFodmap: Boolean = false,
    val occasions: List<String> = emptyList(),
    val sustainable: Boolean = false,
    val vegan: Boolean = false,
    val vegetarian: Boolean = false,
    val veryHealthy: Boolean = false,
    val veryPopular: Boolean = false,
    val whole30: Boolean = false,
    val weightWatcherSmartPoints: Int = 0,
    val dishTypes: List<String> = emptyList(),
    val extendedIngredients: List<ExtendedIngredient> = emptyList(),
    val summary: String = "",
    val winePairing: WinePairing = WinePairing()
)

fun MealDetailResponse.toMealDetail() = MealDetail(
    id = id,
    title = title,
    image = image,
    imageType = imageType,
    summary = summary,
    analyzedInstructions = analyzedInstructions,
    extendedIngredients = extendedIngredients
)