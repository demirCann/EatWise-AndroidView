package com.example.detail

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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: MealRepository
) : ViewModel() {

    private val _mealDetailState = MutableStateFlow(MealDetailViewState(isLoading = true))
    val mealDetailState = _mealDetailState.asStateFlow()

    fun updateIngredientsExpanded() {
        _mealDetailState.update { it.copy(isIngredientsExpanded = !it.isIngredientsExpanded) }
    }

    fun updateInstructionsExpanded() {
        _mealDetailState.update { it.copy(isInstructionsExpanded = !it.isInstructionsExpanded) }
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
                        _mealDetailState.update { it.copy(isLoading = false, detail = detail) }
                    }

                    is ApiResult.Error -> {
                        _mealDetailState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = apiResult.message ?: ERROR_OCCURRED
                            )
                        }
                    }

                    is ApiResult.Loading -> {
                        _mealDetailState.update { it.copy(isLoading = true) }
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

data class MealDetailViewState(
    val isLoading: Boolean = false,
    val detail: MealDetail? = null,
    val errorMessage: String? = null,
    var isIngredientsExpanded: Boolean = false,
    var isInstructionsExpanded: Boolean = false
)