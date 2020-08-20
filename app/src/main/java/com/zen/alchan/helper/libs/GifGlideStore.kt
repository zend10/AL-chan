package com.zen.alchan.helper.libs

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import io.noties.markwon.image.AsyncDrawable
import io.noties.markwon.image.glide.GlideImagesPlugin

class GifGlideStore(private val requestManager: RequestManager) : GlideImagesPlugin.GlideStore {

    override fun load(drawable: AsyncDrawable): RequestBuilder<Drawable> {
        val destination = drawable.destination
        return requestManager
            .asDrawable()
            .apply(RequestOptions().override(1280, 720))
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (resource is Animatable) {
                        (resource as Animatable).start()
                    }
                    return false
                }
            })
            .load(destination)
    }

    override fun cancel(target: Target<*>) {
        requestManager.clear(target)
    }
}