package com.example.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.MealRepository
import com.example.database.model.FavoriteMealEntity
import com.example.database.util.DaoResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val repository: MealRepository
) : ViewModel() {

    private val _favoritesState = MutableStateFlow(FavoriteState(isLoading = true))
    val favoritesState = _favoritesState.asStateFlow()

    fun getFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllFavorites().collect { daoResult ->
                when (daoResult) {
                    is DaoResult.Success -> {
                        _favoritesState.update {
                            it.copy(
                                isLoading = false,
                                favorites = daoResult.data
                            )
                        }
                    }

                    is DaoResult.Error -> {
                        _favoritesState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = daoResult.message
                            )
                        }
                    }

                    is DaoResult.Loading -> {
                        _favoritesState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun removeMealFromFavorites(mealId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeMealFromFavorites(mealId)
        }
        getFavorites()
    }
}

data class FavoriteState(
    val isLoading: Boolean = false,
    val favorites: List<FavoriteMealEntity>? = null,
    val errorMessage: String? = null
)