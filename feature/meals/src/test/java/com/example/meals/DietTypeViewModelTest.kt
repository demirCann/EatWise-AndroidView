package com.example.meals

import com.example.data.repository.MealRepository
import com.example.meals.viewModel.DietTypeViewModel
import com.example.meals.viewModel.MealState
import com.example.network.model.MealResponse
import com.example.network.model.toMeal
import com.example.util.ApiResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

@ExperimentalCoroutinesApi
class DietTypeViewModelTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var repository: MealRepository

    private lateinit var viewModel: DietTypeViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel = DietTypeViewModel(repository)
    }

    @Test
    fun `fetchMealsForDiet returns expected result`() = runTest(testDispatcher) {
        val diet = "Keto"
        val number = 5
        val expectedMealResponse = MealResponse(
            offset = 0,
            number = 5,
            results = emptyList(),
            totalResults = 0
        )
        `when`(repository.getMealsForDiet(diet, number)).thenReturn(
            flow {
                emit(ApiResult.Loading)
                emit(ApiResult.Success(expectedMealResponse.toMeal()))
            }
        )

        viewModel.fetchMealsForDiet(diet, number)

        val result = viewModel.mealState.value
        assertEquals(MealState.Loading, result)
        testScheduler.apply { advanceTimeBy(1000); runCurrent() } // Simulate passage of time
        assertEquals(
            MealState.Success(expectedMealResponse.toMeal()),
            viewModel.mealState.firstOrNull { it is MealState.Success })
    }
}