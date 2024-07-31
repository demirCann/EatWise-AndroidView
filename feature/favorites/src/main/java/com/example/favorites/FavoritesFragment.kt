package com.example.favorites

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
import com.example.favorites.databinding.FragmentFavoritesBinding
import com.example.feature.MealsTypeAdapter
import com.example.feature.toInfo
import com.example.feature.utils.LayoutUtil
import com.example.feature.utils.NavigationUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoriteViewModel by viewModels()

    private lateinit var favoriteAdapter: MealsTypeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getFavorites()
        setupRecyclerView()
        getFavoriteMeals()

        LayoutUtil.setLayoutManager(
            this.context,
            binding.recyclerView,
            resources.configuration.orientation
        )

        NavigationUtil.setupSearchButton(
            this,
            binding.topAppBar,
            R.id.search,
            FavoritesFragmentDirections.actionFavoritesFragmentToSearchFragment(true)
        )

    }

    private fun setupRecyclerView() {
        favoriteAdapter = MealsTypeAdapter(
            isWide = true,
            onFavoriteClicked = { favorite ->
                viewModel.removeMealFromFavorites(favorite.id)
            },
            onClickedItem = { favoriteId ->
                val action =
                    FavoritesFragmentDirections.actionFavoritesFragmentToDetailFragment(favoriteId)
                findNavController().navigate(action)
            }
        )
        binding.recyclerView.adapter = favoriteAdapter
    }

    private fun getFavoriteMeals() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favoritesState.collect { favoriteState ->
                    when {
                        favoriteState.isLoading -> {
                            binding.apply {
                                progressBar.visibility = View.VISIBLE
                                recyclerView.visibility = View.GONE
                                errorText.visibility = View.GONE
                            }
                        }

                        favoriteState.favorites != null -> {
                            binding.apply {
                                progressBar.visibility = View.GONE
                                recyclerView.visibility = View.VISIBLE
                                errorText.visibility = View.GONE
                            }
                            val infoList = favoriteState.favorites.map { it.toInfo() }
                            favoriteAdapter.submitList(infoList)
                        }

                        favoriteState.errorMessage != null -> {
                            binding.apply {
                                progressBar.visibility = View.GONE
                                recyclerView.visibility = View.GONE
                                errorText.text = favoriteState.errorMessage
                                errorText.visibility = View.VISIBLE
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