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
import androidx.recyclerview.widget.GridLayoutManager
import com.example.feature.utils.NavigationUtil
import com.example.meals.R
import com.example.feature.MealsTypeAdapter
import com.example.meals.databinding.FragmentDietTypeBinding
import com.example.meals.viewModel.DietTypeViewModel
import com.example.meals.viewModel.MealState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DietTypeFragment : Fragment() {

    private var _binding: FragmentDietTypeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DietTypeViewModel by viewModels()
    private val args: DietTypeFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
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

        binding.topAppBar.title = dietType.replaceFirst(dietType[0], dietType[0].uppercaseChar())
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        val mealsAdapter = MealsTypeAdapter(
            isWide = true,
            onClickedItem = { mealId ->
                // Navigate to meal detail
                val action =
                    DietTypeFragmentDirections.actionDietTypeFragmentToDetailFragment(mealId)
                findNavController().navigate(action)
            },
            onFavoriteClicked = { meal ->
                viewModel.addFavorite(meal)
            }
        )
        binding.recyclerView.apply {
            adapter = mealsAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fetchMealsForDiet(dietType)
                viewModel.mealState.collect { mealState ->
                    when (mealState) {
                        is MealState.Loading -> {
                            binding.recyclerView.visibility = View.GONE
                        }

                        is MealState.Success -> {
                            binding.recyclerView.visibility = View.VISIBLE
                            mealsAdapter.submitList(mealState.meals.results)
                        }

                        is MealState.Error -> {
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

