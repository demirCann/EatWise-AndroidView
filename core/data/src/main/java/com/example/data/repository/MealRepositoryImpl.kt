package com.example.data.repository

import com.example.database.model.FavoriteMealEntity
import com.example.database.source.FavoriteLocalDataSource
import com.example.model.FavoriteMeal
import com.example.model.toEntity
import com.example.network.model.MealDetailResponse
import com.example.network.model.MealResponse
import com.example.network.source.MealRemoteDataSource
import com.example.util.ApiResult
import com.example.database.util.DaoResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MealRepositoryImpl @Inject constructor(
    private val mealRemoteDataSource: MealRemoteDataSource,
    private val favoriteLocalDataSource: FavoriteLocalDataSource
) : MealRepository {

    override fun getMealsForTypes(type: String, number: Int): Flow<ApiResult<MealResponse>> {
        return mealRemoteDataSource.getMealsForTypes(type, number)
    }

    override fun getMealsForDiet(diet: String, number: Int): Flow<ApiResult<MealResponse>> {
        return mealRemoteDataSource.getMealsForDiet(diet, number)
    }

    override fun getMealDetails(id: Int): Flow<ApiResult<MealDetailResponse>> {
        return mealRemoteDataSource.getMealDetails(id)
    }

    override fun searchRecipes(query: String): Flow<ApiResult<MealResponse>> {
        return mealRemoteDataSource.searchRecipes(query)
    }

    override fun getAllFavorites(): Flow<DaoResult<List<FavoriteMealEntity>>> {
        return favoriteLocalDataSource.getAllFavorites()
    }

    override suspend fun addToFavorites(favoriteMeal: FavoriteMeal) {
        return favoriteLocalDataSource.addToFavorites(favoriteMeal.toEntity())
    }

    override suspend fun removeMealFromFavorites(id: Int) {
        return favoriteLocalDataSource.removeMealFromFavorites(id)
    }

    override fun searchFavoritesByName(searchQuery: String): Flow<DaoResult<List<FavoriteMealEntity>>> {
        return favoriteLocalDataSource.searchFavoritesByName(searchQuery)
    }

    override suspend fun isFavorite(mealId: Int): Boolean {
        return favoriteLocalDataSource.isFavorite(mealId)
    }
}