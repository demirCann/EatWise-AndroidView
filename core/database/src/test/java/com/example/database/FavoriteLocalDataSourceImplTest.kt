package com.example.database

import com.example.database.dao.FavoriteDao
import com.example.database.model.FavoriteMealEntity
import com.example.database.source.FavoriteLocalDataSourceImpl
import com.example.database.util.DaoResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`

@ExperimentalCoroutinesApi
class FavoriteLocalDataSourceImplTest {

    private lateinit var favoriteDao: FavoriteDao
    private lateinit var favoriteLocalDataSource: FavoriteLocalDataSourceImpl

    @Before
    fun setUp() {
        favoriteDao = Mockito.mock(FavoriteDao::class.java)
        favoriteLocalDataSource = FavoriteLocalDataSourceImpl(favoriteDao)
    }

    @Test
    fun `getAllFavorites returns success`() = runTest {
        val mockFavoriteMeals = listOf(
            FavoriteMealEntity(id = 1, title = "Pasta", image = "image_url", imageType = "jpg"),
            FavoriteMealEntity(id = 2, title = "Pizza", image = "image_url", imageType = "jpg")
        )
        `when`(favoriteDao.getAllFavorites()).thenReturn(flowOf(mockFavoriteMeals))

        val result =
            favoriteLocalDataSource.getAllFavorites().firstOrNull { it is DaoResult.Success }

        assert(result is DaoResult.Success)
        assertEquals((result as DaoResult.Success).data, mockFavoriteMeals)
    }

    @Test
    fun `searchFavoritesByName returns success`() = runTest {
        val searchQuery = "Pasta"
        val mockSearchResult = listOf(
            FavoriteMealEntity(id = 1, title = "Pasta", image = "image_url", imageType = "jpg")
        )
        `when`(favoriteDao.searchFavoritesByName(searchQuery)).thenReturn(flowOf(mockSearchResult))

        val result = favoriteLocalDataSource.searchFavoritesByName(searchQuery)
            .firstOrNull { it is DaoResult.Success }

        assert(result is DaoResult.Success)
        assertEquals((result as DaoResult.Success).data, mockSearchResult)
    }

    @Test
    fun `addToFavorites adds a favorite meal`() = runTest {
        val favoriteMeal =
            FavoriteMealEntity(id = 1, title = "Pasta", image = "image_url", imageType = "jpg")

        favoriteLocalDataSource.addToFavorites(favoriteMeal)

        // does dao method called with the favoriteMeal
        Mockito.verify(favoriteDao).addToFavorites(favoriteMeal)
    }

    @Test
    fun `removeMealFromFavorites removes a favorite meal`() = runTest {
        val mealId = 1

        favoriteLocalDataSource.removeMealFromFavorites(mealId)

        // does dao method called with the mealId
        Mockito.verify(favoriteDao).removeMealFromFavorites(mealId)
    }
}
