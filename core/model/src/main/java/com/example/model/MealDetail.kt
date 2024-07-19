package com.example.model


data class MealDetail(
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


data class AnalyzedInstruction(
    val name: String = "",
    val steps: List<Step> = emptyList()
)

data class Step(
    val number: Int = 0,
    val step: String = "",
    val ingredients: List<Ingredient> = emptyList(),
    val equipment: List<Equipment> = emptyList()
)

data class Ingredient(
    val id: Int = 0,
    val name: String = "",
    val image: String = ""
)


data class Equipment(
    val id: Int = 0,
    val name: String = "",
    val image: String = ""
)


data class ExtendedIngredient(
    val aisle: String? = null,
    val amount: Double = 0.0,
    val consistency: String? = null,
    val id: Int = 0,
    val image: String = "",
    val measures: Measures = Measures(),
    val meta: List<String> = emptyList(),
    val name: String = "",
    val original: String = "",
    val originalName: String = "",
    val unit: String = ""
)


data class Measures(
    val metric: Metric = Metric(),
    val us: Us = Us()
)


data class Metric(
    val amount: Double = 0.0,
    val unitLong: String = "",
    val unitShort: String = ""
)


data class Us(
    val amount: Double = 0.0,
    val unitLong: String = "",
    val unitShort: String = ""
)


data class WinePairing(
    val pairedWines: List<String> = emptyList(),
    val pairingText: String = "",
    val productMatches: List<ProductMatch> = emptyList()
)


data class ProductMatch(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val price: String = "",
    val imageUrl: String = "",
    val averageRating: Double = 0.0,
    val ratingCount: Double? = null,
    val score: Double = 0.0,
    val link: String = ""
)
