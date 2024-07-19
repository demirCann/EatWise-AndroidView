package com.example.data.repository

import com.example.database.model.toFavoriteMealList
import com.example.database.source.FavoriteLocalDataSource
import com.example.database.util.DaoResult
import com.example.model.FavoriteMeal
import com.example.model.Meal
import com.example.model.MealDetail
import com.example.model.toEntity
import com.example.network.model.toMeal
import com.example.network.source.MealRemoteDataSource
import com.example.network.util.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MealRepositoryImpl @Inject constructor(
    private val mealRemoteDataSource: MealRemoteDataSource,
    private val favoriteLocalDataSource: FavoriteLocalDataSource
) : MealRepository {
    override fun getMealsForTypes(type: String, number: Int): Flow<ApiResult<Meal>> {
        return mealRemoteDataSource.getMealsForTypes(type, number)
            .map { apiResult ->
                when (apiResult) {
                    is ApiResult.Success -> {
                        val meal = apiResult.data?.toMeal()
                        if (meal != null) {
                            ApiResult.Success(meal)
                        } else {
                            ApiResult.Error("Meal data is null")
                        }
                    }
                    is ApiResult.Error -> ApiResult.Error(apiResult.message ?: "Unknown error")
                    is ApiResult.Loading -> ApiResult.Loading
                }
            }
    }

    override fun getMealsForDiet(diet: String, number: Int): Flow<ApiResult<Meal>> {
        return mealRemoteDataSource.getMealsForDiet(diet, number)
            .map { apiResult ->
                when (apiResult) {
                    is ApiResult.Success -> {
                        val meal = apiResult.data?.toMeal()
                        if (meal != null) {
                            ApiResult.Success(meal)
                        } else {
                            ApiResult.Error("Meal data is null")
                        }
                    }
                    is ApiResult.Error -> ApiResult.Error(apiResult.message ?: "Unknown error")
                    is ApiResult.Loading -> ApiResult.Loading
                }
            }
    }

    override fun getMealDetails(id: Int): Flow<ApiResult<MealDetail>> {
        TODO("Not yet implemented")
    }

    override fun searchRecipes(query: String): Flow<ApiResult<Meal>> {
        return mealRemoteDataSource.searchRecipes(query)
            .map { apiResult ->
                when (apiResult) {
                    is ApiResult.Success -> {
                        val meal = apiResult.data?.toMeal()
                        if (meal != null) {
                            ApiResult.Success(meal)
                        } else {
                            ApiResult.Error("Meal data is null")
                        }
                    }
                    is ApiResult.Error -> ApiResult.Error(apiResult.message ?: "Unknown error")
                    is ApiResult.Loading -> ApiResult.Loading
                }
            }
    }

    override fun getAllFavorites(): Flow<DaoResult<List<FavoriteMeal>>> {
        return favoriteLocalDataSource.getAllFavorites()
            .map { daoResult ->
                when (daoResult) {
                    is DaoResult.Success -> {
                        val favorites = daoResult.data?.toFavoriteMealList()
                        if (favorites != null) {
                            DaoResult.Success(favorites)
                        } else {
                            DaoResult.Error("Favorite data is null")
                        }
                    }
                    is DaoResult.Error -> DaoResult.Error(daoResult.message)
                    is DaoResult.Loading -> DaoResult.Loading
                }
            }
    }

    override suspend fun addToFavorites(favoriteMeal: FavoriteMeal) {
        return favoriteLocalDataSource.addToFavorites(favoriteMeal.toEntity())
    }

    override suspend fun removeMealFromFavorites(id: Int) {
        return favoriteLocalDataSource.removeMealFromFavorites(id)
    }

}