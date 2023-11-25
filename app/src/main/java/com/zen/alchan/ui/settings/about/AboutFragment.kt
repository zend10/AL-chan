package com.zen.alchan.ui.settings.about

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zen.BuildConfig
import com.zen.R
import com.zen.databinding.FragmentAboutBinding
import com.zen.alchan.helper.extensions.applyBottomSidePaddingInsets
import com.zen.alchan.helper.extensions.applyTopPaddingInsets
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.base.NavigationManager
import org.koin.androidx.viewmodel.ext.android.viewModel


class AboutFragment : BaseFragment<FragmentAboutBinding, AboutViewModel>() {

    override val viewModel: AboutViewModel by viewModel()

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAboutBinding {
        return FragmentAboutBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            setUpToolbar(defaultToolbar.defaultToolbar, getString(R.string.about_al_chan))

            aboutSettingsAppVersionText.text = getString(R.string.version, BuildConfig.VERSION_NAME)

            aboutSettingsAniListLink.clicks {
                navigation.openWebView(NavigationManager.Url.ALCHAN_FORUM_THREAD)
            }

            aboutSettingsGitHubLink.clicks {
                navigation.openWebView(NavigationManager.Url.ALCHAN_GITHUB)
            }

            aboutSettingsGmailLink.clicks {
                navigation.openEmailClient()
            }

            aboutSettingsPlayStoreLink.clicks {
                navigation.openWebView(NavigationManager.Url.ALCHAN_PLAY_STORE)
            }

            aboutSettingsTwitterLink.clicks {
                navigation.openWebView(NavigationManager.Url.ALCHAN_TWITTER)
            }

            aboutSettingsPrivacyPolicyText.clicks {
                navigation.openWebView(NavigationManager.Url.ALCHAN_PRIVACY_POLICY)
            }
        }
    }

    override fun setUpInsets() {
        binding.defaultToolbar.defaultToolbar.applyTopPaddingInsets()
        binding.aboutSettingsLayout.applyBottomSidePaddingInsets()
    }

    override fun setUpObserver() {
        // do nothing
    }

    companion object {
        @JvmStatic
        fun newInstance() = AboutFragment()
    }
}