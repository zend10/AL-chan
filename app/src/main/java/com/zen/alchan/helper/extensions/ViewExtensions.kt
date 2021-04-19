package com.zen.alchan.helper.extensions

import android.os.Build
import android.view.View
import android.view.WindowInsets
import androidx.core.view.updatePadding
import com.zen.alchan.helper.pojo.InitialPadding

fun View.applyTopPaddingInsets() {
    val initialPadding = InitialPadding(paddingTop = paddingTop)
    setOnApplyWindowInsetsListener { view, windowInsets ->
        view.updatePadding(top = initialPadding.paddingTop + windowInsets.systemWindowInsetTop)
        windowInsets
    }
    requestApplyInsets()
}