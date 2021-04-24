package com.zen.alchan.helper.utils

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide

object ImageUtil {

    fun loadImage(context: Context, url: String, imageView: AppCompatImageView) {
        Glide.with(context)
            .load(url)
            .centerCrop()
            .into(imageView)
    }

    fun loadImage(context: Context, resourceId: Int, imageView: AppCompatImageView) {
        Glide.with(context)
            .load(resourceId)
            .centerCrop()
            .into(imageView)
    }
}