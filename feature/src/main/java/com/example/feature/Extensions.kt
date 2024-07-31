package com.example.feature

import android.content.Context
import org.jsoup.Jsoup

fun String.formatSummary(): String {
    val document = Jsoup.parse(this)
    val text = document.text()
    return text.replace("<b>", "").replace("</b>", "")
}

fun Int.dpToPx(context: Context): Int = (this * context.resources.displayMetrics.density).toInt()