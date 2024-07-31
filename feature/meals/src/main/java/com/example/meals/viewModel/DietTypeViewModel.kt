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
import kotlinx.coroutines.flow.update
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
                when (apiResult) {
                    is ApiResult.Success -> {
                        val mealList = apiResult.data?.toMealList()
                        mealList?.results?.forEach { info ->
                            info.isFavorite = repository.isFavorite(info.id)
                        }
                        _mealState.update { it.copy(isLoading = false, mealItems = mealList) }
                    }

                    is ApiResult.Error -> {
                        _mealState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = apiResult.message ?: ERROR_OCCURRED
                            )
                        }
                    }

                    is ApiResult.Loading -> {
                        _mealState.update { it.copy(isLoading = true) }
                    }
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