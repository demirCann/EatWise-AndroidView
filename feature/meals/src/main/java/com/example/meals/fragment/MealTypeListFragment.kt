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
import com.example.meals.databinding.FragmentMealTypeListBinding
import com.example.meals.viewModel.MealState
import com.example.meals.viewModel.MealTypeListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MealTypeListFragment : Fragment() {

    private var _binding: FragmentMealTypeListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MealTypeListViewModel by viewModels()
    private val args: MealTypeListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMealTypeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mealType = args.mealType
        viewModel.setMealType(mealType)
        NavigationUtil.setupSearchButton(
            this,
            binding.topAppBar,
            R.id.search,
            MealTypeListFragmentDirections.actionMealTypeListFragmentToSearchFragment()
        )

        LayoutUtil.setLayoutManager(
            this.context,
            binding.recyclerView,
            resources.configuration.orientation
        )

        AppBarUtil.setTopAppBar(this, binding.topAppBar, mealType)

        val mealsAdapter = MealsTypeAdapter(
            isWide = true,
            onClickedItem = { mealId ->
                val action =
                    MealTypeListFragmentDirections.actionMealTypeListFragmentToDetailFragment(mealId)
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

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.mealState.collect { mealState ->
                    when (mealState) {
                        is MealState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE
                        }

                        is MealState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.recyclerView.visibility = View.VISIBLE
                            mealsAdapter.submitList(mealState.meals.results)
                        }

                        is MealState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.recyclerView.visibility = View.GONE
                            binding.errorText.visibility = View.VISIBLE
                            binding.errorText.text = mealState.message
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