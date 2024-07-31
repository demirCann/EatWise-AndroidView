package com.example.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.feature.MealsTypeAdapter
import com.example.feature.toInfo
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
                handleSearchState(it)
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
        binding.apply {
            searchView.isIconified = false
            searchView.clearFocus()
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (!query.isNullOrEmpty()) {
                        binding.emptyTextView.visibility = View.GONE

                        hideKeyboard()

                        if (isFavoriteSearch) {
                            viewModel.favoriteSearch(query)
                        } else viewModel.networkSearch(query)
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText.isNullOrEmpty()) {
                        searchAdapter.submitList(emptyList())
                        binding.emptyTextView.visibility = View.VISIBLE
                    }
                    return true
                }
            })
            searchView.setOnCloseListener {
                if (binding.searchView.query.isEmpty()) {
                    findNavController().navigateUp()
                } else {
                    binding.apply {
                        searchView.setQuery("", false)
                        searchView.clearFocus()
                        searchAdapter.submitList(emptyList())
                    }
                    true
                }
            }
        }
    }

    private fun handleSearchState(state: SearchState) {
        when {
            state.isLoading -> {
                binding.apply {
                    recyclerView.visibility = View.GONE
                    emptyTextView.visibility = View.GONE
                }
            }

            state.errorMessage != null -> {
                binding.apply {
                    emptyTextView.text = state.errorMessage
                    recyclerView.visibility = View.GONE
                    emptyTextView.visibility = View.VISIBLE
                }
            }

            state.searchedFavoriteMeals != null -> {
                if (state.searchedFavoriteMeals.isEmpty()) {
                    binding.apply {
                        emptyTextView.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    }
                } else {
                    binding.apply {
                        emptyTextView.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        val favoriteSearched = state.searchedFavoriteMeals.map {
                            it.toInfo()
                        }
                        searchAdapter.submitList(favoriteSearched)
                    }
                }
            }

            state.searchedMeals != null -> {
                if (state.searchedMeals.results.isEmpty()) {
                    binding.apply {
                        emptyTextView.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    }
                } else {
                    binding.apply {
                        emptyTextView.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        searchAdapter.submitList(state.searchedMeals.results)
                    }
                }
            }
        }
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}