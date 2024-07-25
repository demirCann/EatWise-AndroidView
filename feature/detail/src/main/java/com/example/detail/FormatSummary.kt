package com.example.detail

import org.jsoup.Jsoup

fun formatSummary(html: String): String {
    val document = Jsoup.parse(html)
    val text = document.text()
    return text.replace("<b>", "").replace("</b>", "")
}
