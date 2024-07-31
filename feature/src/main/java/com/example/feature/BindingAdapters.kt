package com.example.feature

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load

@BindingAdapter("visibilityOfItem")
fun bindVisibility(view: View, isVisible: Boolean) {
    view.visibility = if (isVisible) View.VISIBLE else View.GONE
}

@BindingAdapter("imageBitmap")
fun bindImage(imageView: ImageView, bitmap: Bitmap?) {
    if (bitmap != null) {
        imageView.setImageBitmap(bitmap)
    } else imageView.visibility = View.GONE
}

@BindingAdapter("loadImage")
fun bindLoadImage(imageView: ImageView, imageUrl: String?) {
    if (imageUrl != null) {
        imageView.load(imageUrl)
    }
}