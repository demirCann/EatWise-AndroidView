package com.example.meals.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.MealRepository
import com.example.feature.utils.Constants.ERROR_OCCURRED
import com.example.model.Info
import com.example.model.toFavoriteMeal
import com.example.network.model.toMealList
import com.example.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DietTypeViewModel @Inject constructor(
    private val repository: MealRepository
) : ViewModel() {

    private val _mealState = MutableStateFlow(MealState(isLoading = true))
    val mealState = _mealState.asStateFlow()

    fun fetchMealsForDiet(diet: String, number: Int = 10) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getMealsForDiet(diet, number).collect { apiResult ->
                _mealState.value = when (apiResult) {
                    is ApiResult.Success -> MealState(
                        isLoading = false,
                        mealItems = apiResult.data?.toMealList()
                    )

                    is ApiResult.Error -> MealState(
                        isLoading = false,
                        errorMessage = apiResult.message ?: ERROR_OCCURRED
                    )

                    is ApiResult.Loading -> MealState(
                        isLoading = true
                    )
                }
            }
        }
    }

    fun addFavorite(meal: Info) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addToFavorites(meal.toFavoriteMeal())
        }
    }

    fun removeFavorite(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeMealFromFavorites(id)
        }
    }
}