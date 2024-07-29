package com.example.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.MealRepository
import com.example.database.model.FavoriteMealEntity
import com.example.util.DaoResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
            repository.getAllFavorites().collect {
                when (it) {
                    is DaoResult.Success -> {
                        _favoritesState.value = FavoriteState(
                            isLoading = false,
                            favorites = it.data
                        )
                    }

                    is DaoResult.Error -> {
                        _favoritesState.value = FavoriteState(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }

                    is DaoResult.Loading -> {
                        _favoritesState.value = FavoriteState(
                            isLoading = true
                        )
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