package com.example.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.MealRepository
import com.example.network.model.MealDetailResponse
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

    private val _selectedMeal = MutableStateFlow<MealDetailState>(MealDetailState.Loading)
    val selectedMeal = _selectedMeal.asStateFlow()


    fun fetchMealDetail(mealId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getMealDetails(mealId).collect { apiResult ->
                when (apiResult) {
                    is ApiResult.Success -> {
                        _selectedMeal.value = MealDetailState.Success(apiResult.data!!)
                    }

                    is ApiResult.Error -> {
                        _selectedMeal.value =
                            MealDetailState.Error(apiResult.message ?: "An error occurred")
                    }

                    is ApiResult.Loading -> {
                        _selectedMeal.value = MealDetailState.Loading
                    }
                }
            }
        }
    }
}

sealed class MealDetailState {
    data object Loading : MealDetailState()
    data class Success(val detail: MealDetailResponse) : MealDetailState()
    data class Error(val message: String) : MealDetailState()
}