package com.example.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.detail.databinding.FragmentDetailBinding
import com.example.feature.DetailFragmentArguments
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailViewModel by viewModels()
    private val args: DetailFragmentArguments by navArgs()

    private val isIngredientsExpanded = MutableStateFlow(false)
    private val isInstructionsExpanded = MutableStateFlow(false)
    private lateinit var ingredients: List<String>
    private lateinit var instructions: List<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTopBar()
        setupIngredientsCard()
        setupInstructionsCard()


        viewModel.fetchMealDetail(args.mealId)
        fetchMealDetail()
        observeExpandedStates()
    }

    private fun fetchMealDetail() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedMeal.collect { detailState ->
                    when (detailState) {
                        is MealDetailState.Success -> {
                            val mealDetail = detailState.detail
                            binding.mealTitleTextView.text = mealDetail.title
                            binding.mealSummaryTextView.text = formatSummary(mealDetail.summary)
                            binding.mealImageView.load(mealDetail.image)

                            ingredients = mealDetail.extendedIngredients.map { it.original }
                            Log.d("DetailFragment", "Ingredients: $ingredients")

                            instructions =
                                mealDetail.analyzedInstructions.flatMap { it.steps }.map { it.step }
                            Log.d("DetailFragment", "Instructions: $instructions")

                        }

                        is MealDetailState.Error -> {
                            binding.mealTitleTextView.text = detailState.message
                        }

                        is MealDetailState.Loading -> {
                            // show loading
                        }
                    }
                }
            }
        }
    }

    private fun setIngredients(ingredients: List<String>) {
        binding.ingredientsList.removeAllViews()
        ingredients.forEach { ingredient ->
            val textView = TextView(requireContext()).apply {
                text = ingredient
                textSize = 16f
                setPadding(8, 8, 8, 8)
            }
            binding.ingredientsList.addView(textView)
        }
    }

    private fun setInstructions(instructions: List<String>) {
        binding.instructionsList.removeAllViews()
        instructions.forEach { instruction ->
            val textView = TextView(requireContext()).apply {
                text = instruction
                textSize = 16f
                setPadding(8, 8, 8, 8)
            }
            binding.instructionsList.addView(textView)
        }
    }

    private fun setupTopBar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupIngredientsCard() {
        binding.ingredientsCardView.setOnClickListener {
            isIngredientsExpanded.value = !isIngredientsExpanded.value
        }
    }

    private fun setupInstructionsCard() {
        binding.instructionsCardView.setOnClickListener {
            isInstructionsExpanded.value = !isInstructionsExpanded.value
        }
    }

    private fun observeExpandedStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                isIngredientsExpanded.collect { isExpanded ->
                    binding.ingredientsList.isVisible = isExpanded
                    if (isExpanded) {
                        setIngredients(ingredients)
                    }
                    binding.ingredientsArrow.setImageResource(
                        if (isExpanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down
                    )
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                isInstructionsExpanded.collect { isExpanded ->
                    binding.instructionsList.isVisible = isExpanded
                    if (isExpanded) {
                        setInstructions(instructions)
                    }
                    binding.instructionsArrow.setImageResource(
                        if (isExpanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}