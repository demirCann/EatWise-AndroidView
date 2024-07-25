package com.example.meals.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meals.R
import com.example.meals.adapter.CarouselAdapter
import com.example.feature.MealsTypeAdapter
import com.example.feature.utils.NavigationUtil
import com.example.meals.databinding.FragmentMealsBinding
import com.example.meals.databinding.MealSectionLayoutBinding
import com.example.meals.model.MealType
import com.example.meals.viewModel.MealState
import com.example.meals.viewModel.MealsViewModel
import com.example.model.toFavoriteMeal
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MealsFragment : Fragment() {

    private var _binding: FragmentMealsBinding? = null

    private val binding get() = _binding!!

    private val viewModel: MealsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMealsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCarousel()

        NavigationUtil.setupSearchButton(
            this,
            binding.topAppBar,
            R.id.search,
            MealsFragmentDirections.actionMealsFragmentToSearchFragment()
        )

        val mealSections = listOf(
            binding.breakfastSection to MealType.BREAKFAST,
            binding.mainCourseSection to MealType.MAIN_COURSE,
            binding.dessertSection to MealType.DESSERT,
            binding.snackSection to MealType.SNACK,
            binding.soupSection to MealType.SOUP,
            binding.drinkSection to MealType.DRINK
        )

        mealSections.forEach { (sectionBinding, mealType) ->
            setupRecyclerView(sectionBinding, mealType)
            viewModel.fetchMealsForTypes(mealType)
        }

    }

    private fun setupCarousel() {

        val carouselItem = viewModel.carouselItems

        val carouselAdapter = CarouselAdapter(carouselItem) { diet ->
            // Handle click to navigate diet screen
            val action = MealsFragmentDirections.actionMealsFragmentToDietTypeFragment(diet)
            findNavController().navigate(action)
        }

        binding.carousel.carouselRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = carouselAdapter
        }
    }

    private fun setupRecyclerView(sectionBinding: MealSectionLayoutBinding, mealType: MealType) {

        sectionBinding.mealTypeTitle.text = mealType.type.replaceFirst(mealType.type[0], mealType.type[0].uppercaseChar())

        val adapter = MealsTypeAdapter(
            isWide = false,
            onClickedItem = { mealId ->
                // Handle click to navigate to meal details screen
                val action = MealsFragmentDirections.actionMealsFragmentToDetailFragment(mealId)
                findNavController().navigate(action)
            },
            onFavoriteClicked = { meal ->
                // Handle click to add to favorites
                viewModel.addFavorite(meal.toFavoriteMeal())
            }
        )

        sectionBinding.mealRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        sectionBinding.mealRecyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.mealStates[mealType]?.collect { state ->
                    when (state) {
                        is MealState.Loading -> {
                            // Show loading
                        }
                        is MealState.Success -> {
                            adapter.submitList(state.meals.results)
                        }
                        is MealState.Error -> {
                            // Error handling
                        }
                    }
                }
            }
        }

        sectionBinding.seeAllText.setOnClickListener {
            // Handle click to navigate to meal type screen
            val action =
                MealsFragmentDirections.actionMealsFragmentToMealTypeListFragment(mealType.type)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}