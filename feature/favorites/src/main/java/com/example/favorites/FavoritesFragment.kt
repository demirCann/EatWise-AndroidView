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
import androidx.recyclerview.widget.GridLayoutManager
import com.example.favorites.databinding.FragmentFavoritesBinding
import com.example.feature.MealsTypeAdapter
import com.example.feature.utils.NavigationUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoriteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getFavorites()

        val favoriteAdapter = MealsTypeAdapter(
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

        NavigationUtil.setupSearchButton(
            this,
            binding.topAppBar,
            R.id.search,
            FavoritesFragmentDirections.actionFavoritesFragmentToSearchFragment(true)
        )

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = favoriteAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favoritesState.collect { favoriteState ->
                    when (favoriteState) {
                        is FavoriteState.Loading -> {
                            // show loading
                        }

                        is FavoriteState.Success -> {
                            favoriteAdapter.submitFavoriteList(favoriteState.favorites)
                        }

                        is FavoriteState.Error -> {
                            //show error message
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