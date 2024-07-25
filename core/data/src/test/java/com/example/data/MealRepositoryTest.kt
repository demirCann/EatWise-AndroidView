package com.example.data

import com.example.data.repository.MealRepositoryImpl
import com.example.database.source.FavoriteLocalDataSource
import com.example.network.model.MealResponse
import com.example.network.model.Result
import com.example.network.model.toMeal
import com.example.network.source.MealRemoteDataSource
import com.example.util.ApiResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MealRepositoryTest {
    private lateinit var mealRemoteDataSource: MealRemoteDataSource
    private lateinit var mealLocalDataSource: FavoriteLocalDataSource
    private lateinit var repository: MealRepositoryImpl

    @Before
    fun setUp() {
        mealRemoteDataSource = mockk()
        mealLocalDataSource = mockk()
        repository = MealRepositoryImpl(mealRemoteDataSource, mealLocalDataSource)
    }

    @Test
    fun fetchMealsForTypes_success() = runTest {
        val mockResult = Result(
            id = 1,
            image = "http://example.com/image.jpg",
            imageType = "jpg",
            title = "Mock Meal"
        )
        val mockResponse = MealResponse(
            number = 1,
            offset = 0,
            results = listOf(mockResult),
            totalResults = 1
        )
        coEvery { mealRemoteDataSource.getMealsForTypes(any(), any()) } returns flow {
            emit(ApiResult.Success(mockResponse))
        }

        val result = repository.getMealsForTypes("main course", 10).first()
        assert(result is ApiResult.Success)
        assertEquals(mockResponse.toMeal(), (result as ApiResult.Success).data)
    }

    @Test
    fun fetchMealsForTypes_error() = runTest {
        coEvery { mealRemoteDataSource.getMealsForTypes(any(), any()) } returns flow {
            emit(ApiResult.Error("An error occurred"))
        }

        val result = repository.getMealsForTypes("main course", 10).first()
        assert(result is ApiResult.Error)
        assertEquals("An error occurred", (result as ApiResult.Error).message)
    }
}