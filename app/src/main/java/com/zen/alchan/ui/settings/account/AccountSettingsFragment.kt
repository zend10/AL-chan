package com.zen.alchan.ui.settings.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.R
import com.zen.databinding.FragmentAccountSettingsBinding
import com.zen.alchan.helper.extensions.applyBottomPaddingInsets
import com.zen.alchan.helper.extensions.applySidePaddingInsets
import com.zen.alchan.helper.extensions.applyTopPaddingInsets
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.base.NavigationManager
import org.koin.androidx.viewmodel.ext.android.viewModel

class AccountSettingsFragment : BaseFragment<FragmentAccountSettingsBinding, AccountSettingsViewModel>() {

    override val viewModel: AccountSettingsViewModel by viewModel()

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAccountSettingsBinding {
        return FragmentAccountSettingsBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            setUpToolbar(defaultToolbar.defaultToolbar, getString(R.string.account_settings))

            accountSettingsUpdateProfileLayout.clicks {
                navigation.openWebView(NavigationManager.Url.ANILIST_PROFILE_SETTINGS)
            }

            accountSettingsUpdateAccountLayout.clicks {
                navigation.openWebView(NavigationManager.Url.ANILIST_ACCOUNT_SETTINGS)
            }

            accountSettingsForceUpdateStatsLayout.clicks {
                navigation.openWebView(NavigationManager.Url.ANILIST_LISTS_SETTINGS)
            }

            accountSettingsImportListsLayout.clicks {
                navigation.openWebView(NavigationManager.Url.ANILIST_IMPORT_LISTS)
            }

            accountSettingsConnectWithTwitterLayout.clicks {
                navigation.openWebView(NavigationManager.Url.ANILIST_CONNECT_WITH_TWITTER)
            }

            accountSettingsDeleteAccountLayout.clicks {
                navigation.openWebView(NavigationManager.Url.ANILIST_ACCOUNT_SETTINGS)
            }

            accountSettingsLogoutButton.text = getString(R.string.logout)
            accountSettingsLogoutButton.clicks {
                dialog.showConfirmationDialog(
                    R.string.logout_from_alchan,
                    R.string.logging_out_from_alchan_doesnt_mean_logging_out_from_anilist,
                    R.string.logout,
                    {
                        viewModel.logout()
                        restartApp()
                    },
                    R.string.cancel,
                    { }
                )
            }
        }
    }

    override fun setUpInsets() {
        binding.defaultToolbar.defaultToolbar.applyTopPaddingInsets()
        binding.accountSettingsLayout.applySidePaddingInsets()
        binding.accountSettingsLogoutLayout.applyBottomPaddingInsets()
    }

    override fun setUpObserver() {

    }

    companion object {
        @JvmStatic
        fun newInstance() = AccountSettingsFragment()
    }
}