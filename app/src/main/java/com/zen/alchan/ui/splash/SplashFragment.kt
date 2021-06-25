package com.zen.alchan.ui.splash

import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.databinding.FragmentSplashBinding
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class SplashFragment : BaseFragment<FragmentSplashBinding, SplashViewModel>() {

    override val viewModel: SplashViewModel by viewModel()

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSplashBinding {
        return FragmentSplashBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {

    }

    override fun setUpObserver() {
        disposables.add(
            viewModel.isLoggedIn.subscribe {
                if (it)
                    navigation.navigateToMain()
                else
                    navigation.navigateToLanding()
            }
        )

        viewModel.loadData()
    }

    companion object {
        @JvmStatic
        fun newInstance() = SplashFragment()
    }
}