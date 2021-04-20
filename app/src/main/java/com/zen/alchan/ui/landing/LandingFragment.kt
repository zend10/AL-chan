package com.zen.alchan.ui.landing

import com.zen.alchan.R
import com.zen.alchan.helper.extensions.applyTopBottomPaddingInsets
import com.zen.alchan.helper.libs.ImageUtil
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.base.NavigationManager
import kotlinx.android.synthetic.main.fragment_landing.*


class LandingFragment : BaseFragment(R.layout.fragment_landing) {

    override fun setupLayout() {
        ImageUtil.loadImage(requireContext(), R.drawable.landing_wallpaper, landingBackgroundImage)
        getStartedButton.setOnClickListener { navigate(NavigationManager.Page.LOGIN) }
    }

    override fun setupInsets() {
        landingContentRoot.applyTopBottomPaddingInsets()
    }

    override fun setupObserver() {

    }

    companion object {
        @JvmStatic
        fun newInstance() = LandingFragment()
    }
}