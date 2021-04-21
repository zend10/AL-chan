package com.zen.alchan.ui.landing

import com.zen.alchan.R
import com.zen.alchan.helper.extensions.applyTopBottomPaddingInsets
import com.zen.alchan.helper.libs.ImageUtil
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.base.NavigationManager
import kotlinx.android.synthetic.main.fragment_landing.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class LandingFragment : BaseFragment(R.layout.fragment_landing) {

    private val viewModel by viewModel<LandingViewModel>()

    override fun setupLayout() {
        ImageUtil.loadImage(requireContext(), R.drawable.landing_wallpaper, landingBackgroundImage)
        getStartedButton.setOnClickListener { viewModel.pressGetStarted() }
    }

    override fun setupInsets() {
        landingContentRoot.applyTopBottomPaddingInsets()
    }

    override fun setupObserver() {
        disposables.add(
            viewModel.navigation.subscribe {
                navigate(it.first, it.second)
            }
        )
    }

    companion object {
        @JvmStatic
        fun newInstance() = LandingFragment()
    }
}