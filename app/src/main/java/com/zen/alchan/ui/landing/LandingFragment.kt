package com.zen.alchan.ui.landing

import com.zen.alchan.R
import com.zen.alchan.helper.extensions.applyTopBottomPaddingInsets
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_landing.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class LandingFragment : BaseFragment(R.layout.fragment_landing) {

    private val viewModel by viewModel<LandingViewModel>()

    override fun setUpLayout() {
        ImageUtil.loadImage(requireContext(), R.drawable.landing_wallpaper, landingBackgroundImage)
        getStartedButton.setOnClickListener { navigation.navigateToLogin() }
    }

    override fun setUpInsets() {
        landingContentRoot.applyTopBottomPaddingInsets()
    }

    override fun setUpObserver() {

    }

    companion object {
        @JvmStatic
        fun newInstance() = LandingFragment()
    }
}