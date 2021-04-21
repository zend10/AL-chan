package com.zen.alchan.ui.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.base.NavigationManager
import org.koin.androidx.viewmodel.ext.android.viewModel


class SplashFragment : BaseFragment(R.layout.fragment_splash) {

    private val viewModel by viewModel<SplashViewModel>()

    override fun setupLayout() {

    }

    override fun setupObserver() {
        disposables.add(
            viewModel.navigation.subscribe {
                navigate(it.first, it.second)
            }
        )

        viewModel.checkIsLoggedIn()
    }

    companion object {
        @JvmStatic
        fun newInstance() = SplashFragment()
    }
}