package com.example.chat

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter("visibilityOfMessage")
fun bindVisibility(view: View, isVisible: Boolean) {
    view.visibility = if (isVisible) View.VISIBLE else View.GONE
}

@BindingAdapter("imageBitmap")
fun bindImage(imageView: ImageView, bitmap: Bitmap?) {
    if(bitmap != null) {
        imageView.setImageBitmap(bitmap)
    } else imageView.visibility = View.GONE
}