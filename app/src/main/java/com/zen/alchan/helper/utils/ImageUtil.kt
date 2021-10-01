package com.zen.alchan.helper.utils

import android.content.Context
import android.content.res.ColorStateList
import android.net.Uri
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.zen.alchan.R

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

    fun loadImage(context: Context, uri: Uri, imageView: AppCompatImageView) {
        Glide.with(context)
            .load(uri)
            .signature(ObjectKey(TimeUtil.getCurrentTimeInMillis()))
            .centerCrop()
            .into(imageView)
    }

    fun loadCircleImage(context: Context, url: String, imageView: AppCompatImageView)  {
        imageView.background = ContextCompat.getDrawable(context, R.drawable.shape_oval_with_border)
        imageView.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))

        Glide.with(context)
            .load(url)
            .apply(RequestOptions.circleCropTransform())
            .into(imageView)
    }

    fun loadRectangleImage(context: Context, url: String, imageView: AppCompatImageView) {
        imageView.background = ContextCompat.getDrawable(context, R.drawable.shape_rectangle)
        imageView.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.transparent))

        Glide.with(context)
            .load(url)
            .into(imageView)
    }
}