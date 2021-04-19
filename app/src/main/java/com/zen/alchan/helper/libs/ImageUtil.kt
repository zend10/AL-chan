package com.zen.alchan.helper.libs

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
}