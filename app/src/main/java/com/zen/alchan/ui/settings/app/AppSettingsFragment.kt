package com.zen.alchan.ui.settings.app

import android.content.Intent
import android.net.Uri
import com.zen.alchan.R
import com.zen.alchan.helper.enums.AppTheme
import com.zen.alchan.helper.extensions.applyTopPaddingInsets
import com.zen.alchan.helper.extensions.convertFromSnakeCase
import com.zen.alchan.helper.extensions.replaceUnderscore
import com.zen.alchan.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_app_settings.*
import kotlinx.android.synthetic.main.toolbar_default.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AppSettingsFragment : BaseFragment(R.layout.fragment_app_settings) {

    private val viewModel by viewModel<AppSettingsViewModel>()

    private var appThemeListDialog: AppThemeListDialog? = null

    override fun setUpLayout() {
        setUpToolbar(defaultToolbar, getString(R.string.app_settings))

        appSettingsThemeText.setOnClickListener {
            appThemeListDialog = AppThemeListDialog.newInstance(object : AppThemeListDialog.AppThemeListener {
                override fun getSelectedAppTheme(appTheme: AppTheme) {
                    appThemeListDialog?.dismiss()
                    viewModel.updateAppTheme(appTheme)
                }
            })

            appThemeListDialog?.show(childFragmentManager, null)
        }
    }

    override fun setUpInsets() {
        super.setUpInsets()
        defaultToolbar.applyTopPaddingInsets()
    }

    override fun setUpObserver() {
        disposables.add(
            viewModel.appTheme.subscribe {
                appSettingsThemeText.text = it.name.convertFromSnakeCase()
            }
        )

        viewModel.loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        appThemeListDialog = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = AppSettingsFragment()
    }
}