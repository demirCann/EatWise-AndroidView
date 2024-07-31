package com.example.feature.utils


import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.MaterialToolbar

object NavigationUtil {
    fun setupSearchButton(
        fragment: Fragment,
        toolbar: MaterialToolbar,
        searchMenuItemId: Int,
        navDirections: NavDirections
    ) {
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                searchMenuItemId -> {
                    fragment.findNavController().navigate(navDirections)
                    true
                }

                else -> false
            }
        }
    }
}
