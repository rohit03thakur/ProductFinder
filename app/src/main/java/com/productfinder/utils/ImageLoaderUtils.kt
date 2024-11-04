package com.productfinder.utils

import android.widget.ImageView
import com.productfinder.R
import com.squareup.picasso.Picasso

object ImageLoaderUtils {
    fun loadImage(url: String?, imageView: ImageView) {
        if (!url.isNullOrEmpty()) {
            Picasso.get().load(url).placeholder(R.drawable.img_placeholder).into(imageView)
        }
    }
}