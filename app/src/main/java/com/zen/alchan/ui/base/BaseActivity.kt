package com.zen.alchan.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class BaseActivity : AppCompatActivity() {

    private val viewModel by viewModel<BaseViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(viewModel.appColorTheme)
        super.onCreate(savedInstanceState)
    }
}