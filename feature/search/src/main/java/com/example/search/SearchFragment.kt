package com.example.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.feature.MealsTypeAdapter
import com.example.feature.utils.LayoutUtil
import com.example.search.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()
    private lateinit var searchAdapter: MealsTypeAdapter
    private var isFavoriteSearch = false

    private val args: SearchFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isFavoriteSearch = args.isFromFavorite

        setupRecyclerView()
        setupSearchToolBar()

        binding.searchToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        LayoutUtil.setLayoutManager(
            this.context,
            binding.recyclerView,
            resources.configuration.orientation
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchedMeals.collect {
                handleNetworkSearchState(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favoriteSearchedMeals.collect {
                handleFavoriteSearchState(it)
            }
        }
    }

    private fun setupRecyclerView() {
        searchAdapter = MealsTypeAdapter(
            isWide = true,
            onClickedItem = { mealId ->
                val action = SearchFragmentDirections.actionSearchFragmentToDetailFragment(mealId)
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
        binding.recyclerView.adapter = searchAdapter
    }

    private fun setupSearchToolBar() {
        binding.searchView.isIconified = false
        binding.searchView.clearFocus()
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.emptyTextView.visibility = View.GONE

                    if (isFavoriteSearch) {
                        viewModel.favoriteSearch(query)
                    } else viewModel.networkSearch(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    searchAdapter.submitList(emptyList())
                    binding.progressBar.visibility = View.GONE
                    binding.emptyTextView.visibility = View.VISIBLE
                }
                return true
            }
        })
        binding.searchView.setOnCloseListener {
            if (binding.searchView.query.isEmpty()) {
                findNavController().navigateUp()
            } else {
                binding.searchView.setQuery("", false)
                binding.searchView.clearFocus()
                searchAdapter.submitList(emptyList())
                true
            }
        }
    }

    private fun handleNetworkSearchState(state: NetworkSearchState) {
        binding.progressBar.visibility = View.GONE
        when {
            state.isLoading -> {
                binding.progressBar.visibility = View.VISIBLE
            }

            state.errorMessage != null -> {
                binding.emptyTextView.text = state.errorMessage
                binding.emptyTextView.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            }

            state.searchedMeals != null -> {
                if (state.searchedMeals.results.isEmpty()) {
                    binding.emptyTextView.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                } else {
                    binding.emptyTextView.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    searchAdapter.submitList(state.searchedMeals.results)
                }
            }
        }
    }

    private fun handleFavoriteSearchState(state: FavoriteSearchState) {
        Log.d("SearchFragment", "handleFavoriteSearchState: $state")
        binding.progressBar.visibility = View.GONE
        when {
            state.isLoading -> {
                binding.progressBar.visibility = View.VISIBLE
            }

            state.errorMessage != null -> {
                binding.emptyTextView.text = state.errorMessage
                binding.emptyTextView.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            }

            state.searchedMeals != null -> {
                if (state.searchedMeals.isEmpty()) {
                    binding.emptyTextView.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                } else {
                    binding.emptyTextView.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    searchAdapter.submitFavoriteList(state.searchedMeals)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}