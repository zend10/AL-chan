package com.zen.alchan.ui.splash

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.databinding.FragmentSplashBinding
import com.zen.alchan.helper.utils.DeepLink
import com.zen.alchan.ui.base.BaseFragment
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
        disposables.add(
            viewModel.isLoggedIn.subscribe {
                // in a case where there's a deep link passed to Splash screen,
                // navigate directly to Main screen
                if (it || deepLink?.uri != null) {
                    navigation.navigateToMain(deepLink)
                    deepLink = null
                } else {
                    navigation.navigateToLanding()
                }
            }
        )

        // currently bypassSplash is only used after restarting app from AppSettings screen
        if (bypassSplash) {
            navigation.navigateToMain(deepLink)
            deepLink = null
        } else {
            viewModel.loadData()
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