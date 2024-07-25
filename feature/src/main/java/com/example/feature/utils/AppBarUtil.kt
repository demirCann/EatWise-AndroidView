package com.example.feature.utils

import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

object AppBarUtil {

    fun setTopAppBar(fragment: Fragment, toolbar: Toolbar, title: String) {
        toolbar.title = title.replaceFirst(title[0], title[0].uppercaseChar())
        toolbar.setNavigationOnClickListener {
            fragment.findNavController().navigateUp()
        }
    }
}