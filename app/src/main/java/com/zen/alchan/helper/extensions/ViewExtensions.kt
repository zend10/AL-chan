package com.zen.alchan.helper.extensions

import android.view.View
import androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.jakewharton.rxbinding2.view.RxView
import com.zen.alchan.helper.pojo.InitialPadding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

fun View.show(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun View.makeVisible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
}

fun View.applyTopPaddingInsets() {
    val initialPadding = InitialPadding(paddingTop = paddingTop)
    setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        view.updatePadding(top = initialPadding.paddingTop + windowInsets.getInsets(WindowInsetsCompat.Type.statusBars()).top)
        windowInsets
    }
    requestApplyInsets()
}

fun View.applyBottomPaddingInsets() {
    val initialPadding = InitialPadding(paddingBottom = paddingBottom)
    setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        view.updatePadding(bottom = initialPadding.paddingBottom + windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom)
        windowInsets
    }
    requestApplyInsets()
}

fun View.applySidePaddingInsets() {
    val initialPadding = InitialPadding(paddingLeft = paddingLeft, paddingRight = paddingRight)
    setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        view.updatePadding(
            left = initialPadding.paddingLeft + windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars()).left,
            right = initialPadding.paddingRight + windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars()).right
        )
        windowInsets
    }
    requestApplyInsets()
}

fun View.applyTopBottomPaddingInsets() {
    val initialPadding = InitialPadding(paddingTop = paddingTop, paddingBottom = paddingBottom)
    setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        view.updatePadding(
            top = initialPadding.paddingTop + windowInsets.getInsets(WindowInsetsCompat.Type.statusBars()).top,
            bottom = initialPadding.paddingBottom + windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
        )
        windowInsets
    }
    requestApplyInsets()
}

fun View.applyBottomSidePaddingInsets() {
    val initialPadding = InitialPadding(paddingBottom = paddingBottom, paddingLeft = paddingLeft, paddingRight = paddingRight)
    setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        view.updatePadding(
            bottom = initialPadding.paddingBottom + windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom,
            left = initialPadding.paddingLeft + windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars()).left,
            right = initialPadding.paddingRight + windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars()).right
        )
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