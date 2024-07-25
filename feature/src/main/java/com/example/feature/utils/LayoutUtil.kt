package com.example.feature.utils

import android.content.Context
import android.content.res.Configuration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

object LayoutUtil {
    private const val LAND_SCAPE_SPAN_COUNT = 4
    private const val PORTRAIT_SPAN_COUNT = 2

    fun setLayoutManager(context: Context?, recyclerView: RecyclerView, orientation: Int) {
        val spanCount =
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                LAND_SCAPE_SPAN_COUNT
            } else {
                PORTRAIT_SPAN_COUNT
            }

        val layoutManager = GridLayoutManager(context, spanCount)
        recyclerView.layoutManager = layoutManager
    }
}