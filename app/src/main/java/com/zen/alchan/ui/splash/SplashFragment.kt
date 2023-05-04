package com.zen.alchan.ui.splash

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.databinding.FragmentSplashBinding
import com.zen.alchan.helper.utils.DeepLink
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.base.NavigationManager
import org.koin.androidx.viewmodel.ext.android.viewModel


class SplashFragment : BaseFragment<FragmentSplashBinding, SplashViewModel>() {

    override val viewModel: SplashViewModel by viewModel()

    private var deepLink: DeepLink? = null
    private var bypassSplash: Boolean = false

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSplashBinding {
        return FragmentSplashBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        // do nothing
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.isLoggedIn.subscribe {
                // in a case where there's a deep link passed to Splash screen,
                // navigate directly to Main screen
                if (it || deepLink?.uri != null) {
                    navigation.navigateToMain(deepLink)
                    deepLink = null
                } else {
                    navigation.navigateToLanding()
                }
            },
            viewModel.isSessionExpired.subscribe {
                dialog.showMessageDialog(R.string.session_expired, R.string.your_session_has_ended, R.string.ok)
            },
            viewModel.updateDialog.subscribe { (message, requiredUpdate) ->
                if (requiredUpdate) {
                    dialog.showActionDialog(
                        getString(R.string.new_update_is_available),
                        message,
                        R.string.ok
                    ) {
                        navigation.openWebView(NavigationManager.Url.ALCHAN_PLAY_STORE)
                    }
                } else {
                    dialog.showConfirmationDialog(
                        getString(R.string.new_update_is_available),
                        message,
                        R.string.ok,
                        {
                            navigation.openWebView(NavigationManager.Url.ALCHAN_PLAY_STORE)
                        },
                        R.string.later,
                        {
                            viewModel.goToNextPage()
                        }
                    )
                }
            },
            viewModel.announcementDialog.subscribe {
                dialog.showConfirmationDialog(
                    getString(R.string.announcement),
                    it,
                    R.string.ok,
                    {
                        viewModel.goToNextPage()
                    },
                    R.string.dont_show_again,
                    {
                        viewModel.setNotShowAgain()
                    }
                )
            }
        )

        // currently bypassSplash is only used after restarting app from AppSettings screen
        if (bypassSplash) {
            navigation.navigateToMain(deepLink)
            deepLink = null
        } else {
            viewModel.loadData(Unit)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(deepLink: DeepLink?, bypassSplash: Boolean) = SplashFragment().apply {
            this.deepLink = deepLink
            this.bypassSplash = bypassSplash
        }
    }
}