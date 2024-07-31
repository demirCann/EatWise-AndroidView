package com.example.feature.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("SimpleDateFormat")
fun getTimestamp(): String {
    val sdf = SimpleDateFormat("HH:mm aa")
    return sdf.format(Date())
}