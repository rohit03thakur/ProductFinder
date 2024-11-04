package com.productfinder.utils

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter("imageUrl", "errorImage", requireAll = false)
fun setImageUrl(imageView: ImageView, url: String?, errorImage: Drawable? = null) {
    if (!url.isNullOrEmpty()) {
        ImageLoaderUtils.loadImage(url, imageView)
    } else {
        if (errorImage != null) imageView.setImageDrawable(errorImage)
    }
}
