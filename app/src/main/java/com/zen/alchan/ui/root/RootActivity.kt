package com.zen.alchan.ui.root

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.zen.alchan.R
import com.zen.alchan.helper.extensions.changeStatusBarColor
import com.zen.alchan.ui.base.*
import io.reactivex.disposables.CompositeDisposable
import org.koin.androidx.viewmodel.ext.android.viewModel

class RootActivity : AppCompatActivity(), ViewContract {

    private val viewModel by viewModel<RootViewModel>()

    lateinit var navigationManager: NavigationManager
    lateinit var dialogManager: DialogManager

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(viewModel.getAppThemeResource())

        if (viewModel.isLightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        navigationManager = DefaultNavigationManager(this, supportFragmentManager, R.id.rootLayout)
        dialogManager = DefaultDialogManager(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        )

        val flags = window.decorView.systemUiVisibility

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (viewModel.isLightMode()) {
                changeStatusBarColor(R.color.black)
            }
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            if (viewModel.isLightMode()) {
                window.decorView.systemUiVisibility = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                window.navigationBarColor = getColor(R.color.whiteTransparent70)
            } else {
                window.decorView.systemUiVisibility = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                window.navigationBarColor = getColor(R.color.pureBlackTransparent70)
            }
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (viewModel.isLightMode()) {
                window.decorView.systemUiVisibility = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                window.navigationBarColor = getColor(R.color.whiteTransparent70)
            } else {
                window.decorView.systemUiVisibility = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
                window.navigationBarColor = getColor(R.color.pureBlackTransparent70)
            }
        } else {
            if (viewModel.isLightMode()) {
                window.decorView.systemUiVisibility = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            } else {
                window.decorView.systemUiVisibility = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
            }
        }

        setUpLayout()
    }

    override fun setUpLayout() {
        navigationManager.navigateToSplash()
    }

    override fun setUpObserver() {
        val deepLink = intent.data?.encodedFragment
        val accessToken = deepLink?.substring("access_token=".length, deepLink.indexOf("&"))

        if (!accessToken.isNullOrBlank()) {
            intent.data = null
            navigationManager.navigateToLogin(accessToken)
        }
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