package com.zen.alchan.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.R
import com.zen.databinding.FragmentSettingsBinding
import com.zen.alchan.helper.extensions.applyTopPaddingInsets
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : BaseFragment<FragmentSettingsBinding, SettingsViewModel>() {

    override val viewModel: SettingsViewModel by viewModel()

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            setUpToolbar(defaultToolbar.defaultToolbar, getString(R.string.settings), R.drawable.ic_delete)

            appSettingsLayout.clicks {
                navigation.navigateToAppSettings()
            }

            aniListSettingsLayout.clicks {
                navigation.navigateToAniListSettings()
            }

            listSettingsLayout.clicks {
                navigation.navigateToListSettings()
            }

            notificationsSettingsLayout.clicks {
                navigation.navigateToNotificationsSettings()
            }

            accountSettingsLayout.clicks {
                navigation.navigateToAccountSettings()
            }

            aboutLayout.clicks {
                navigation.navigateToAbout()
            }
        }
    }

    override fun setUpInsets() {
        binding.defaultToolbar.defaultToolbar.applyTopPaddingInsets()
    }

    override fun setUpObserver() {
        // do nothing
    }

    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }
}