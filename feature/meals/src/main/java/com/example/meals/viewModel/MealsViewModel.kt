package com.example.meals.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.MealRepository
import com.example.feature.utils.Constants.ERROR_OCCURRED
import com.example.meals.R
import com.example.meals.model.CarouselItemData
import com.example.meals.model.MealType
import com.example.meals.util.Constants.GLUTEN_CAP
import com.example.meals.util.Constants.GLUTEN_LOW
import com.example.meals.util.Constants.KETOGENIC_CAP
import com.example.meals.util.Constants.KETOGENIC_LOW
import com.example.meals.util.Constants.MEAL_NUMBER_TO_FETCH
import com.example.meals.util.Constants.VEGAN_CAP
import com.example.meals.util.Constants.VEGAN_LOW
import com.example.meals.util.Constants.VEGETARIAN_CAP
import com.example.meals.util.Constants.VEGETARIAN_LOW
import com.example.model.Info
import com.example.model.MealList
import com.example.model.toFavoriteMeal
import com.example.network.model.MealResponse
import com.example.network.model.toMealList
import com.example.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealsViewModel @Inject constructor(
    private val repository: MealRepository
) : ViewModel() {

    private val _mealState = MutableStateFlow(MealState(isLoading = true))
    val mealState: StateFlow<MealState> = _mealState.asStateFlow()

    val carouselItems = listOf(
        CarouselItemData(VEGETARIAN_CAP, R.drawable.vegetarian, VEGETARIAN_LOW),
        CarouselItemData(VEGAN_CAP, R.drawable.vegan, VEGAN_LOW),
        CarouselItemData(GLUTEN_CAP, R.drawable.gluten_free, GLUTEN_LOW),
        CarouselItemData(KETOGENIC_CAP, R.drawable.ketogenic, KETOGENIC_LOW)
    )

    fun fetchMealsForTypes(mealType: MealType, number: Int = MEAL_NUMBER_TO_FETCH) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getMealsForTypes(mealType.type, number).collect { apiResult ->
                updateMealState(mealType, apiResult)
            }
        }
    }

    private suspend fun updateMealState(type: MealType, apiResult: ApiResult<MealResponse>) {
        when (apiResult) {
            is ApiResult.Success -> {
                val mealList = apiResult.data?.toMealList()
                mealList?.results?.forEach { info ->
                    info.isFavorite = repository.isFavorite(info.id)
                }

                _mealState.update {
                    val newMealTypeItems = it.mealTypeItems.toMutableMap()
                    if (mealList != null) {
                        newMealTypeItems[type] = mealList
                    }
                    it.copy(
                        isLoading = false,
                        mealTypeItems = newMealTypeItems
                    )
                }
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

data class MealState(
    val isLoading: Boolean = false,
    val mealTypeItems: Map<MealType, MealList> = emptyMap(),
    val mealItems: MealList? = null,
    val errorMessage: String? = null
)