package com.example.meals.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.MealRepository
import com.example.model.Info
import com.example.model.toFavoriteMeal
import com.example.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealTypeListViewModel @Inject constructor(
    private val repository: MealRepository
) : ViewModel() {

    private val _mealState = MutableStateFlow<MealState>(MealState.Loading)
    val mealState = _mealState.asStateFlow()

    var type: String? = null
        private set

    fun setMealType(type: String) {
        this.type = type
        fetchMealsForTypes(type)
    }

    private fun fetchMealsForTypes(mealType: String, number: Int = 10) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getMealsForTypes(mealType, number).collect { apiResult ->
                _mealState.value = when (apiResult) {
                    is ApiResult.Success -> MealState.Success(apiResult.data!!)
                    is ApiResult.Error -> MealState.Error(apiResult.message ?: "An error occurred")
                    is ApiResult.Loading -> MealState.Loading
                }
            }
        }
    }

    fun addFavorite(meal: Info) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addToFavorites(meal.toFavoriteMeal())
        }
    }

}