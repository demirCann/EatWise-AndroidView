package com.example.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.MealRepository
import com.example.database.model.FavoriteMealEntity
import com.example.database.util.DaoResult
import com.example.feature.utils.Constants.ERROR_OCCURRED
import com.example.model.Info
import com.example.model.MealList
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
class SearchViewModel @Inject constructor(
    private val repository: MealRepository
) : ViewModel() {

    private val _searchedMeals = MutableStateFlow(SearchState(isLoading = true))
    val searchedMeals = _searchedMeals.asStateFlow()

    fun favoriteSearch(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.searchFavoritesByName(query).collect { daoResult ->
                when (daoResult) {
                    is DaoResult.Success -> {
                        val searchedMeals = daoResult.data!!
                        _searchedMeals.update {
                            it.copy(
                                isLoading = false,
                                searchedFavoriteMeals = searchedMeals
                            )
                        }
                    }

                    is DaoResult.Error -> {
                        _searchedMeals.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = daoResult.message
                            )
                        }
                    }

                    is DaoResult.Loading -> {
                        _searchedMeals.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun networkSearch(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.searchRecipes(query).collect { apiResult ->
                when (apiResult) {
                    is ApiResult.Success -> {
                        val searchedMeals = apiResult.data!!.toMealList()
                        _searchedMeals.update {
                            it.copy(
                                isLoading = false,
                                searchedMeals = searchedMeals
                            )
                        }
                    }

                    is ApiResult.Error -> {
                        _searchedMeals.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = apiResult.message ?: ERROR_OCCURRED
                            )
                        }
                    }

                    is ApiResult.Loading -> {
                        _searchedMeals.update { it.copy(isLoading = true) }
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

data class SearchState(
    val isLoading: Boolean = false,
    val searchedMeals: MealList? = null,
    val searchedFavoriteMeals: List<FavoriteMealEntity>? = null,
    val errorMessage: String? = null
)