package com.zen.alchan.ui.base

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import com.zen.alchan.R
import com.zen.alchan.helper.changeStatusBarColor
import com.zen.alchan.helper.utils.Utility
import org.koin.androidx.viewmodel.ext.android.viewModel

// for Activity so that theme color is applied
abstract class BaseActivity : AppCompatActivity() {

    private val viewModel by viewModel<BaseViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(viewModel.appColorThemeResource)

        if (Utility.isLightTheme(viewModel.appColorTheme)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (Utility.isLightTheme(viewModel.appColorTheme)) {
                changeStatusBarColor(R.color.black)
            }
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            if (Utility.isLightTheme(viewModel.appColorTheme)) {
                val flags = window.decorView.systemUiVisibility
                window.decorView.systemUiVisibility = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//                window.statusBarColor = getColor(R.color.whiteTransparent70)
                window.navigationBarColor = getColor(R.color.whiteTransparent70)
            } else {
                val flags = window.decorView.systemUiVisibility
                window.decorView.systemUiVisibility = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
//                window.statusBarColor = getColor(R.color.pureBlackTransparent70)
                window.navigationBarColor = getColor(R.color.pureBlackTransparent70)
            }
        } else {
            if (Utility.isLightTheme(viewModel.appColorTheme)) {
                val flags = window.decorView.systemUiVisibility
                window.decorView.systemUiVisibility = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
//                window.statusBarColor = getColor(R.color.whiteTransparent70)
                window.navigationBarColor = getColor(R.color.whiteTransparent70)
            } else {
                val flags = window.decorView.systemUiVisibility
                window.decorView.systemUiVisibility = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
//                window.statusBarColor = getColor(R.color.pureBlackTransparent70)
                window.navigationBarColor = getColor(R.color.pureBlackTransparent70)
            }
        }
    }
}