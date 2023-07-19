package com.zen.alchan.ui.base

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewbinding.ViewBinding
import com.zen.alchan.R
import com.zen.alchan.helper.utils.DeepLink
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class BaseActivity<T: ViewBinding> : AppCompatActivity(), ViewContract {

    private val viewModel by viewModel<BaseActivityViewModel>()

    abstract var navigationManager: NavigationManager
        protected set

    abstract var dialogManager: DialogManager
        protected set

    abstract val incomingDeepLink: Observable<DeepLink>

    protected val disposables = CompositeDisposable()

    private var _binding: T? = null
    protected val binding: T
        get() = _binding!!

    abstract fun generateViewBinding(): T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appThemeResource = viewModel.getAppThemeResource()
        val isLightMode = viewModel.isLightMode()

        setTheme(appThemeResource)

        AppCompatDelegate.setDefaultNightMode(
            if (isLightMode) AppCompatDelegate.MODE_NIGHT_NO else AppCompatDelegate.MODE_NIGHT_YES
        )

        _binding = generateViewBinding()
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val controller = WindowInsetsControllerCompat(window, window.decorView)

        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.O -> {
                controller.isAppearanceLightStatusBars = isLightMode
                window.navigationBarColor = getColor(R.color.pureBlack)
            }
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> {
                controller.isAppearanceLightStatusBars = isLightMode
                controller.isAppearanceLightNavigationBars = isLightMode
                window.navigationBarColor = getColor(if (isLightMode) R.color.whiteTransparent70 else R.color.pureBlackTransparent70)
            }
            else -> {
                controller.isAppearanceLightStatusBars = isLightMode
                controller.isAppearanceLightNavigationBars = isLightMode
            }
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