package com.zen.alchan.helper.extensions

import android.os.Build
import android.view.View
import android.view.WindowInsets
import androidx.core.view.updatePadding
import com.zen.alchan.helper.pojo.InitialPadding

fun View.applyInsets() {
    val initialPadding = InitialPadding(paddingTop = paddingTop)
    setOnApplyWindowInsetsListener { view, windowInsets ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.updatePadding(top = initialPadding.paddingTop + windowInsets.getInsets(WindowInsets.Type.statusBars()).top)
        } else {
            view.updatePadding(top = initialPadding.paddingTop + windowInsets.systemWindowInsetTop)
        }
        windowInsets
    }
    requestApplyInsets()
}