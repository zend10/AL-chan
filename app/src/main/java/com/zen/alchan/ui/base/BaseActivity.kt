package com.zen.alchan.ui.base

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewbinding.ViewBinding
import com.zen.alchan.R
import com.zen.alchan.helper.extensions.changeStatusBarColor
import io.reactivex.disposables.CompositeDisposable
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class BaseActivity<T: ViewBinding> : AppCompatActivity(), ViewContract {

    private val viewModel by viewModel<BaseActivityViewModel>()

    protected val disposables = CompositeDisposable()

    private var _binding: T? = null
    protected val binding: T
        get() = _binding!!

    abstract fun generateViewBinding(): T

    override fun onCreate(savedInstanceState: Bundle?) {
        val appThemeResource = viewModel.getAppThemeResource()
        val isLightMode = viewModel.isLightMode()

        setTheme(appThemeResource)

        AppCompatDelegate.setDefaultNightMode(
            if (isLightMode) AppCompatDelegate.MODE_NIGHT_NO else AppCompatDelegate.MODE_NIGHT_YES
        )

        super.onCreate(savedInstanceState)
        _binding = generateViewBinding()
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val controller = WindowInsetsControllerCompat(window, window.decorView)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (isLightMode) {
                // TODO: thinking if still need to support API below 23
                changeStatusBarColor(R.color.black)
            }
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            controller.isAppearanceLightStatusBars = isLightMode
            window.navigationBarColor = getColor(R.color.pureBlack)
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            controller.isAppearanceLightStatusBars = isLightMode
            controller.isAppearanceLightNavigationBars = isLightMode
            window.navigationBarColor = getColor(if (isLightMode) R.color.whiteTransparent70 else R.color.pureBlackTransparent70)
        } else {
            controller.isAppearanceLightStatusBars = isLightMode
            controller.isAppearanceLightNavigationBars = isLightMode
        }

        setUpLayout()
    }

    override fun onStart() {
        super.onStart()
        setUpObserver()
    }

    override fun onResume() {
        super.onResume()
        if (disposables.isDisposed) {
            setUpObserver()
        }
    }

    override fun onPause() {
        super.onPause()
        disposables.clear()
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }
}