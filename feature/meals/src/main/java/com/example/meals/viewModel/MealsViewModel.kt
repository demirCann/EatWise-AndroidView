package com.example.meals.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.MealRepository
import com.example.meals.R
import com.example.meals.model.CarouselItemData
import com.example.meals.model.MealType
import com.example.model.FavoriteMeal
import com.example.model.Meal
import com.example.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealsViewModel @Inject constructor(
    private val repository: MealRepository
) : ViewModel() {

    private val _mealStates = mutableMapOf<MealType, MutableStateFlow<MealState>>(
        MealType.BREAKFAST to MutableStateFlow(MealState.Loading),
        MealType.MAIN_COURSE to MutableStateFlow(MealState.Loading),
        MealType.DESSERT to MutableStateFlow(MealState.Loading),
        MealType.SNACK to MutableStateFlow(MealState.Loading),
        MealType.SOUP to MutableStateFlow(MealState.Loading),
        MealType.DRINK to MutableStateFlow(MealState.Loading)

    )

    val mealStates: Map<MealType, StateFlow<MealState>> =
        _mealStates.mapValues { it.value.asStateFlow() }

    val carouselItems = listOf(
        CarouselItemData("Vegetarian", R.drawable.vegetarian, "vegetarian"),
        CarouselItemData("Vegan", R.drawable.vegan, "vegan"),
        CarouselItemData("Gluten Free", R.drawable.gluten_free, "gluten free"),
        CarouselItemData("Ketogenic", R.drawable.ketogenic, "ketogenic")
    )


    fun fetchMealsForTypes(mealType: MealType, number: Int = 10) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getMealsForTypes(mealType.type, number).collect { apiResult ->
                updateMealState(mealType, apiResult)
            }
        }
    }

    private fun updateMealState(type: MealType, apiResult: ApiResult<Meal>) {
        _mealStates[type]?.value = when (apiResult) {
            is ApiResult.Success -> MealState.Success(apiResult.data!!)
            is ApiResult.Error -> MealState.Error(apiResult.message ?: "An error occurred")
            is ApiResult.Loading -> MealState.Loading
        }
    }



    fun addFavorite(favorite: FavoriteMeal) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addToFavorites(favorite)
        }
    }

}

sealed class MealState {
    data object Loading : MealState()
    data class Success(val meals: Meal) : MealState()
    data class Error(val message: String) : MealState()
}