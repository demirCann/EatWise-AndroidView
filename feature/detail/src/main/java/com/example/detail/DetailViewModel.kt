package com.example.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.MealRepository
import com.example.feature.utils.Constants.ERROR_OCCURRED
import com.example.model.Info
import com.example.model.toFavoriteMeal
import com.example.network.model.MealDetail
import com.example.network.model.toMealDetail
import com.example.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: MealRepository
) : ViewModel() {

    private val _isIngredientsExpanded = MutableStateFlow(false)
    val isIngredientsExpanded = _isIngredientsExpanded.asStateFlow()

    private val _isInstructionsExpanded = MutableStateFlow(false)
    val isInstructionsExpanded = _isInstructionsExpanded.asStateFlow()

    private val _selectedMeal = MutableStateFlow(MealDetailState(isLoading = true))
    val selectedMeal = _selectedMeal.asStateFlow()

    fun updateIngredientsExpanded() {
        Log.d(
            "DetailViewModel",
            "Ingredients expanded state updated: ${_isIngredientsExpanded.value}"
        )
        _isIngredientsExpanded.value = !_isIngredientsExpanded.value
    }

    fun updateInstructionsExpanded() {
        _isInstructionsExpanded.value = !_isInstructionsExpanded.value
    }

    fun fetchMealDetail(mealId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getMealDetails(mealId).collect { apiResult ->
                when (apiResult) {
                    is ApiResult.Success -> {
                        val detail = apiResult.data?.toMealDetail()
                        if (detail != null) {
                            detail.isFavorite = repository.isFavorite(mealId)
                        }
                        _selectedMeal.value = MealDetailState(
                            isLoading = false,
                            detail = detail
                        )
                    }

                    is ApiResult.Error -> {
                        _selectedMeal.value =
                            MealDetailState(
                                isLoading = false,
                                errorMessage = apiResult.message ?: ERROR_OCCURRED
                            )
                    }

                    is ApiResult.Loading -> {
                        _selectedMeal.value = MealDetailState(
                            isLoading = true
                        )
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

data class MealDetailState(
    val isLoading: Boolean = false,
    val detail: MealDetail? = null,
    val errorMessage: String? = null
)