package com.zen.alchan.ui.base

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

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (Utility.isLightTheme(viewModel.appColorTheme)) {
                changeStatusBarColor(R.color.black)
            }
        } else {
            if (Utility.isLightTheme(viewModel.appColorTheme)) {
                val flags = window.decorView.systemUiVisibility
                window.decorView.systemUiVisibility = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                val flags = window.decorView.systemUiVisibility
                window.decorView.systemUiVisibility = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }
    }
}