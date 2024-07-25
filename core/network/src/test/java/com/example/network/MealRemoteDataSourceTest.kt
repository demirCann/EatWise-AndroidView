package com.example.network

import com.example.network.model.MealDetailResponse
import com.example.network.model.MealResponse
import com.example.network.retrofit.MealApi
import com.example.network.source.MealRemoteDataSourceImpl
import com.example.util.ApiResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import retrofit2.Response

@ExperimentalCoroutinesApi
class MealRemoteDataSourceImplTest {

    private lateinit var mealApi: MealApi
    private lateinit var mealRemoteDataSource: MealRemoteDataSourceImpl

    @Before
    fun setUp() {
        mealApi = mock(MealApi::class.java)
        mealRemoteDataSource = MealRemoteDataSourceImpl(mealApi)
    }

    @Test
    fun `getMealsForTypes returns success`() = runTest {
        val mockResponse = MealResponse(1, 0, listOf(), 1)
        `when`(mealApi.getMealsForTypes(anyString(), anyInt())).thenReturn(
            Response.success(
                mockResponse
            )
        )

        val result =
            mealRemoteDataSource.getMealsForTypes("type", 1).firstOrNull { it is ApiResult.Success }
        assert(result is ApiResult.Success)
        assertEquals((result as ApiResult.Success).data, mockResponse)
    }

    @Test
    fun `getMealsForDiet returns success`() = runTest {
        val mockResponse = MealResponse(1, 0, listOf(), 1)
        `when`(mealApi.getMealsForDiet(anyString(), anyInt())).thenReturn(
            Response.success(
                mockResponse
            )
        )

        val result = mealRemoteDataSource.getMealsForDiet("diet", 1).first()
        assert(result is ApiResult.Success)
        assertEquals((result as ApiResult.Success).data, mockResponse)
    }

    @Test
    fun `getMealDetails returns success`() = runTest {
        val mockResponse = MealDetailResponse()
        `when`(mealApi.getMealDetails(anyInt())).thenReturn(Response.success(mockResponse))

        val result = mealRemoteDataSource.getMealDetails(1).first()
        assert(result is ApiResult.Success)
        assertEquals((result as ApiResult.Success).data, mockResponse)
    }

    @Test
    fun `searchRecipes returns success`() = runTest {
        val mockResponse = MealResponse(1, 0, listOf(), 1)
        `when`(mealApi.searchRecipes(anyString())).thenReturn(Response.success(mockResponse))

        val result = mealRemoteDataSource.searchRecipes("query").first()
        assert(result is ApiResult.Success)
        assertEquals((result as ApiResult.Success).data, mockResponse)
    }
}
