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
import com.example.feature.utils.NavigationUtil
import com.example.meals.R
import com.example.meals.adapter.MealAdapter
import com.example.meals.databinding.FragmentMealsBinding
import com.example.meals.model.MealItem
import com.example.meals.model.MealType
import com.example.meals.viewModel.MealsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MealsFragment : Fragment() {

    private var _binding: FragmentMealsBinding? = null

    private val binding get() = _binding!!

    private val viewModel: MealsViewModel by viewModels()
    private lateinit var mealsAdapter: MealAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMealsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()

        NavigationUtil.setupSearchButton(
            this,
            binding.topAppBar,
            R.id.search,
            MealsFragmentDirections.actionMealsFragmentToSearchFragment()
        )

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                MealType.entries.forEach {
                    viewModel.fetchMealsForTypes(it)
                }
            }
        }
    }

    private fun setupRecyclerView() {

        mealsAdapter = MealAdapter(
            onClickedCarouselItem = { dietType ->
                val action = MealsFragmentDirections.actionMealsFragmentToDietTypeFragment(dietType)
                findNavController().navigate(action)
            },
            onClickedMealItem = { mealId ->
                val action = MealsFragmentDirections.actionMealsFragmentToDetailFragment(mealId)
                findNavController().navigate(action)
            },
            onFavoriteClicked = { meal ->
                if (meal.isFavorite) {
                    viewModel.addFavorite(meal)
                } else {
                    viewModel.removeFavorite(meal.id)
                }
            },
            onClickedSeeAll = { mealType ->
                val action =
                    MealsFragmentDirections.actionMealsFragmentToMealTypeListFragment(mealType)
                findNavController().navigate(action)

            }
        )

        binding.mealsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = mealsAdapter
        }
    }

    private fun setupObservers() {
        val allItems = mutableListOf<MealItem>()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Collect carousel items once and add to list
                val carouselItems = MealItem.Carousel(viewModel.carouselItems)
                allItems.add(carouselItems)
                mealsAdapter.submitList(allItems.toList())

                // Collect meal states
                viewModel.mealStates.forEach { (mealType, stateFlow) ->
                    launch {
                        stateFlow.collect { mealState ->
                            when {
                                mealState.isLoading -> {
                                    binding.progressBar.visibility = View.VISIBLE
                                    binding.mealsRecyclerView.visibility = View.GONE
                                    binding.errorText.visibility = View.GONE
                                }

                                mealState.mealItems != null -> {
                                    binding.progressBar.visibility = View.GONE
                                    binding.mealsRecyclerView.visibility = View.VISIBLE
                                    binding.errorText.visibility = View.GONE
                                    val mealSection =
                                        MealItem.MealSection(mealType, mealState.mealItems.results)
                                    allItems.add(mealSection)
                                    mealsAdapter.submitList(allItems.toList())
                                }

                                mealState.errorMessage != null -> {
                                    binding.progressBar.visibility = View.GONE
                                    binding.mealsRecyclerView.visibility = View.GONE
                                    binding.errorText.text = mealState.errorMessage
                                    binding.errorText.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}