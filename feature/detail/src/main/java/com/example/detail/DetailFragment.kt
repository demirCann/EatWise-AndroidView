package com.example.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.detail.databinding.FragmentDetailBinding
import com.example.feature.DetailFragmentArguments
import com.example.feature.formatSummary
import com.example.feature.utils.AppBarUtil
import com.example.network.model.toInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val detailViewModel: DetailViewModel by viewModels()
    private val args: DetailFragmentArguments by navArgs()

    private lateinit var detailInstructionsAdapter: DetailInstructionsAdapter
    private lateinit var detailIngredientsAdapter: DetailIngredientsAdapter

    private var ingredients: List<String> = emptyList()
    private var instructions: List<String> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AppBarUtil.setTopAppBar(this, binding.toolbar, binding.toolbar.title.toString())
        setupIngredientsCard()
        setupInstructionsCard()
        fetchMealDetail()
        setUpRecyclersView()
        detailViewModel.fetchMealDetail(args.mealId)
    }

    private fun fetchMealDetail() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                detailViewModel.mealDetailState.collect { detailState ->
                    when {
                        detailState.detail != null -> {
                            binding.apply {
                                viewModel = detailViewModel

                                val mealDetail = detailState.detail
                                ingredients = mealDetail.extendedIngredients.map { it.original }
                                instructions = mealDetail.analyzedInstructions.flatMap { it.steps }
                                    .map { it.step }
                                mealSummaryTextView.text = mealDetail.summary.formatSummary()

                                favoriteButton.isFavorite = mealDetail.isFavorite
                                favoriteButton.setOnClickListener {
                                    mealDetail.isFavorite = !mealDetail.isFavorite
                                    favoriteButton.isFavorite = mealDetail.isFavorite
                                    if (mealDetail.isFavorite) {
                                        detailViewModel.addFavorite(mealDetail.toInfo())
                                    } else {
                                        detailViewModel.removeFavorite(mealDetail.id)
                                    }
                                }

                                val isIngredientsExpanded = detailState.isIngredientsExpanded
                                if (isIngredientsExpanded) {
                                    detailIngredientsAdapter.submitList(ingredients)
                                }
                                ingredientsArrow.setImageResource(
                                    if (isIngredientsExpanded) R.drawable.arrow_up else R.drawable.ic_arrow_down
                                )

                                val isInstructionsExpanded = detailState.isInstructionsExpanded
                                if (isInstructionsExpanded) {
                                    detailInstructionsAdapter.submitList(instructions)
                                }
                                instructionsArrow.setImageResource(
                                    if (isInstructionsExpanded) R.drawable.arrow_up else R.drawable.ic_arrow_down
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setUpRecyclersView() {
        detailInstructionsAdapter = DetailInstructionsAdapter()
        detailIngredientsAdapter = DetailIngredientsAdapter()

        binding.ingredientsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = detailIngredientsAdapter
        }

        binding.instructionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = detailInstructionsAdapter
        }
    }

    private fun setupIngredientsCard() {
        binding.ingredientsCardView.setOnClickListener {
            detailViewModel.updateIngredientsExpanded()
        }
    }

    private fun setupInstructionsCard() {
        binding.instructionsCardView.setOnClickListener {
            detailViewModel.updateInstructionsExpanded()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}