package com.zen.alchan.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zen.alchan.R

abstract class BaseActivity(private val layout: Int) : AppCompatActivity(), ViewContract {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_ThemeDefaultYellow)

        super.onCreate(savedInstanceState)
        setContentView(layout)

        setupObserver()
        setupLayout()
    }
}