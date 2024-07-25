package com.example.database.source

import com.example.database.dao.FavoriteDao
import com.example.database.model.FavoriteMealEntity
import com.example.util.DaoResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class FavoriteLocalDataSourceImpl @Inject constructor(
    private val favoriteDao: FavoriteDao
) : FavoriteLocalDataSource {
    override fun getAllFavorites(): Flow<DaoResult<List<FavoriteMealEntity>>> = flow {
        emit(DaoResult.Loading)

        val favorites = favoriteDao.getAllFavorites().firstOrNull()
        if (favorites != null) {
            emit(DaoResult.Success(favorites))
        } else {
            emit(DaoResult.Error("Error"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun addToFavorites(favoriteMealEntity: FavoriteMealEntity) {
        favoriteDao.addToFavorites(favoriteMealEntity)
    }

    override suspend fun removeMealFromFavorites(id: Int) {
        favoriteDao.removeMealFromFavorites(id)
    }

    override fun searchFavoritesByName(searchQuery: String): Flow<DaoResult<List<FavoriteMealEntity>>> =
        flow {
            emit(DaoResult.Loading)

            val searchedFavorites = favoriteDao.searchFavoritesByName(searchQuery).firstOrNull()
            if (searchedFavorites != null) {
                emit(DaoResult.Success(searchedFavorites))
            } else {
                emit(DaoResult.Error("Error"))
            }
        }.flowOn(Dispatchers.IO)

}