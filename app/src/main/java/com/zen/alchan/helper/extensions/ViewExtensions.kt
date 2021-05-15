package com.zen.alchan.helper.extensions

import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import android.view.WindowInsets
import androidx.core.view.updatePadding
import com.jakewharton.rxbinding2.view.RxView
import com.zen.alchan.helper.pojo.InitialPadding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

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

fun View.applyBottomPaddingInsets() {
    val initialPadding = InitialPadding(paddingBottom = paddingBottom)
    setOnApplyWindowInsetsListener { view, windowInsets ->
        view.updatePadding(bottom = initialPadding.paddingBottom + windowInsets.systemWindowInsetBottom)
        windowInsets
    }
    requestApplyInsets()
}

fun View.clicks(action: () -> Unit): Disposable {
    return RxView.clicks(this)
        .throttleFirst(1000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
        .subscribe {
            action()
        }
}