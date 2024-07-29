package com.example.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.detail.databinding.FragmentDetailBinding
import com.example.feature.DetailFragmentArguments
import com.example.feature.utils.AppBarUtil
import com.example.network.model.toInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.jsoup.Jsoup

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailViewModel by viewModels()
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

        observeExpandedStates()

    }

    private fun fetchMealDetail() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fetchMealDetail(args.mealId)
                viewModel.selectedMeal.collect { detailState ->
                    when {
                        detailState.isLoading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.nestedScrollView.visibility = View.GONE
                            binding.errorText.visibility = View.GONE
                        }

                        detailState.detail != null -> {
                            binding.progressBar.visibility = View.GONE
                            binding.nestedScrollView.visibility = View.VISIBLE
                            val mealDetail = detailState.detail
                            binding.mealTitleTextView.text = mealDetail.title
                            binding.mealSummaryTextView.text = mealDetail.summary.formatSummary()
                            binding.mealImageView.load(mealDetail.image)
                            ingredients = mealDetail.extendedIngredients.map { it.original }
                            instructions =
                                mealDetail.analyzedInstructions.flatMap { it.steps }.map { it.step }
                            binding.favoriteButton.isFavorite = mealDetail.isFavorite
                            binding.favoriteButton.setOnClickListener {
                                mealDetail.isFavorite = !mealDetail.isFavorite
                                binding.favoriteButton.isFavorite = mealDetail.isFavorite
                                if (mealDetail.isFavorite) {
                                    viewModel.addFavorite(mealDetail.toInfo())
                                } else {
                                    viewModel.removeFavorite(mealDetail.id)
                                }
                            }
                        }

                        detailState.errorMessage != null -> {
                            binding.progressBar.visibility = View.GONE
                            binding.nestedScrollView.visibility = View.GONE
                            binding.mealTitleTextView.text = detailState.errorMessage
                            binding.errorText.visibility = View.VISIBLE
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
        Log.d("DetailFragment", "setup")
        binding.ingredientsCardView.setOnClickListener {
            Log.d("DetailFragment", "setupIngredientsCard")
            viewModel.updateIngredientsExpanded()
        }
    }

    private fun setupInstructionsCard() {
        binding.instructionsCardView.setOnClickListener {
            viewModel.updateInstructionsExpanded()
        }
    }

    private fun observeExpandedStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isIngredientsExpanded.collect { isExpanded ->
                    binding.ingredientsRecyclerView.isVisible = isExpanded
                    Log.d("DetailFragment", "isIngredientsExpanded: $isExpanded")
                    if (isExpanded) {
                        detailIngredientsAdapter.submitList(ingredients)
                        binding.ingredientsRecyclerView.visibility = View.VISIBLE
                    } else {
                        binding.ingredientsRecyclerView.visibility = View.GONE
                    }
                    binding.ingredientsArrow.setImageResource(
                        if (isExpanded) R.drawable.arrow_up else R.drawable.ic_arrow_down
                    )
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isInstructionsExpanded.collect { isExpanded ->
                    Log.d("DetailFragment", "isInstructionsExpanded: $isExpanded")
                    binding.instructionsRecyclerView.isVisible = isExpanded
                    if (isExpanded) {
                        detailInstructionsAdapter.submitList(instructions)
                        binding.instructionsRecyclerView.visibility = View.VISIBLE
                    } else {
                        binding.instructionsRecyclerView.visibility = View.GONE
                    }
                    binding.instructionsArrow.setImageResource(
                        if (isExpanded) R.drawable.arrow_up else R.drawable.ic_arrow_down
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

fun String.formatSummary(): String {
    val document = Jsoup.parse(this)
    val text = document.text()
    return text.replace("<b>", "").replace("</b>", "")
}