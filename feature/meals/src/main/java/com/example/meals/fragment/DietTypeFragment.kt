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
import androidx.navigation.fragment.navArgs
import com.example.feature.MealsTypeAdapter
import com.example.feature.utils.AppBarUtil
import com.example.feature.utils.LayoutUtil
import com.example.feature.utils.NavigationUtil
import com.example.meals.R
import com.example.meals.databinding.FragmentDietTypeBinding
import com.example.meals.viewModel.DietTypeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DietTypeFragment : Fragment() {

    private var _binding: FragmentDietTypeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DietTypeViewModel by viewModels()
    private val args: DietTypeFragmentArgs by navArgs()

    private lateinit var mealsAdapter: MealsTypeAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDietTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dietType = args.dietType

        viewModel.fetchMealsForDiet(dietType)

        NavigationUtil.setupSearchButton(
            this,
            binding.topAppBar,
            R.id.search,
            DietTypeFragmentDirections.actionDietTypeFragmentToSearchFragment()
        )

        LayoutUtil.setLayoutManager(
            this.context,
            binding.recyclerView,
            resources.configuration.orientation
        )

        AppBarUtil.setTopAppBar(this, binding.topAppBar, dietType)

        mealsAdapter = MealsTypeAdapter(
            isWide = true,
            onClickedItem = { mealId ->
                // Navigate to meal detail
                val action =
                    DietTypeFragmentDirections.actionDietTypeFragmentToDetailFragment(mealId)
                findNavController().navigate(action)
            },
            onFavoriteClicked = { meal ->
                if (meal.isFavorite) {
                    viewModel.addFavorite(meal)
                } else {
                    viewModel.removeFavorite(meal.id)
                }
            }
        )
        binding.recyclerView.adapter = mealsAdapter

        fetchMealsForDiet(dietType)
    }

    private fun fetchMealsForDiet(dietType: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fetchMealsForDiet(dietType)
                viewModel.mealState.collect { mealState ->
                    when {
                        mealState.isLoading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE
                            binding.errorText.visibility = View.GONE
                        }

                        mealState.errorMessage != null -> {
                            binding.progressBar.visibility = View.GONE
                            binding.recyclerView.visibility = View.GONE
                            binding.errorText.text = mealState.errorMessage
                            binding.errorText.visibility = View.VISIBLE
                        }

                        mealState.mealItems != null -> {
                            binding.progressBar.visibility = View.GONE
                            binding.recyclerView.visibility = View.VISIBLE
                            binding.errorText.visibility = View.GONE
                            mealsAdapter.submitList(mealState.mealItems.results)
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

