package com.zen.alchan.helper.extensions

import android.os.Build
import android.view.View
import android.view.WindowInsets
import androidx.core.view.updatePadding
import com.zen.alchan.helper.pojo.InitialPadding

fun View.show(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun View.applyTopPaddingInsets() {
    val initialPadding = InitialPadding(paddingTop = paddingTop)
    setOnApplyWindowInsetsListener { view, windowInsets ->
        view.updatePadding(top = initialPadding.paddingTop + windowInsets.systemWindowInsetTop)
        windowInsets
    }
    requestApplyInsets()
}

fun View.applyTopBottomPaddingInsets() {
    val initialPadding = InitialPadding(paddingTop = paddingTop, paddingBottom = paddingBottom)
    setOnApplyWindowInsetsListener { view, windowInsets ->
        view.updatePadding(
            top = initialPadding.paddingTop + windowInsets.systemWindowInsetTop,
            bottom = initialPadding.paddingBottom + windowInsets.systemWindowInsetBottom
        )
        windowInsets
    }
    requestApplyInsets()
}