package com.example.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.MealRepository
import com.example.model.FavoriteMeal
import com.example.model.Info
import com.example.model.Meal
import com.example.model.toFavoriteMeal
import com.example.util.ApiResult
import com.example.util.DaoResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MealRepository
) : ViewModel() {

    private val _favoriteSearchedMeals = MutableStateFlow<FavoriteSearchState>(FavoriteSearchState.Loading)
    val favoriteSearchedMeals = _favoriteSearchedMeals.asStateFlow()


    private val _searchedMeals = MutableStateFlow<NetworkSearchState>(NetworkSearchState.Loading)
    val searchedMeals = _searchedMeals.asStateFlow()

    fun favoriteSearch(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.searchFavoritesByName(query).collect { daoResult ->
                when (daoResult) {
                    is DaoResult.Success -> {
                        val searchedMeals = daoResult.data!!
                        _favoriteSearchedMeals.value = FavoriteSearchState.Success(searchedMeals)
                    }
                    is DaoResult.Error -> {
                        _favoriteSearchedMeals.value = FavoriteSearchState.Error(daoResult.message)
                    }
                    is DaoResult.Loading -> {
                        _favoriteSearchedMeals.value = FavoriteSearchState.Loading
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
                        val searchedMeals = apiResult.data!!.results
                        _searchedMeals.value = NetworkSearchState.Success(searchedMeals)
                    }
                    is ApiResult.Error -> {
                        _searchedMeals.value = NetworkSearchState.Error(apiResult.message!!)
                    }
                    is ApiResult.Loading -> {
                        _searchedMeals.value = NetworkSearchState.Loading
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

}


sealed class NetworkSearchState {
    data object Loading : NetworkSearchState()
    data class Success(val searchedMeals: List<Info>) : NetworkSearchState()
    data class Error(val message: String) : NetworkSearchState()
}

sealed class FavoriteSearchState {
    data object Loading : FavoriteSearchState()
    data class Success(val searchedMeals: List<FavoriteMeal>) : FavoriteSearchState()
    data class Error(val message: String) : FavoriteSearchState()
}