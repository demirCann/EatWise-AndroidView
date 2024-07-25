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
import androidx.recyclerview.widget.GridLayoutManager
import com.example.feature.MealsTypeAdapter
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
        // Inflate the layout for this fragment
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchedMeals.collect {
                Log.d("SearchFragment", "onViewCreated: $it")
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
                viewModel.addFavorite(meal)
            }
        )
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = searchAdapter
        }
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
            if(binding.searchView.query.isEmpty()) {
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
        when (state) {
            is NetworkSearchState.Success -> {
                if (state.searchedMeals.isEmpty()) {
                    binding.emptyTextView.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                } else {
                    binding.emptyTextView.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    searchAdapter.submitList(state.searchedMeals)
                }
            }

            is NetworkSearchState.Error -> {
                binding.emptyTextView.text = state.message
                binding.emptyTextView.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            }

            NetworkSearchState.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
            }
        }
    }

    private fun handleFavoriteSearchState(state: FavoriteSearchState) {
        Log.d("SearchFragment", "handleFavoriteSearchState: $state")
        binding.progressBar.visibility = View.GONE
        when (state) {
            is FavoriteSearchState.Success -> {
                if (state.searchedMeals.isEmpty()) {
                    binding.emptyTextView.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                } else {
                    binding.emptyTextView.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    searchAdapter.submitFavoriteList(state.searchedMeals)
                }
            }

            is FavoriteSearchState.Error -> {
                binding.emptyTextView.text = state.message
                binding.emptyTextView.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            }

            FavoriteSearchState.Loading -> {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}