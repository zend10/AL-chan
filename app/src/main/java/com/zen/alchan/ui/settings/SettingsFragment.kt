package com.zen.alchan.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.zen.alchan.R
import com.zen.alchan.helper.extensions.applyTopPaddingInsets
import com.zen.alchan.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.toolbar_default.*

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    override fun setUpLayout() {
        setUpToolbar(defaultToolbar, getString(R.string.settings), R.drawable.ic_delete)

        appSettingsLayout.setOnClickListener {
            navigation.navigateToAppSettings()
        }
    }

    override fun setUpInsets() {
        super.setUpInsets()
        defaultToolbar.applyTopPaddingInsets()
    }

    override fun setUpObserver() {

    }

    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }
}