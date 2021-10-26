package com.zen.alchan.ui.landing

import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.databinding.FragmentLandingBinding
import com.zen.alchan.helper.extensions.applyTopBottomPaddingInsets
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class LandingFragment : BaseFragment<FragmentLandingBinding, LandingViewModel>() {

    override val viewModel: LandingViewModel by viewModel()

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLandingBinding {
        return FragmentLandingBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            ImageUtil.loadImage(requireContext(), R.drawable.landing_wallpaper, landingBackgroundImage)

            landingGetStartedButton.clicks {
                navigation.navigateToLogin()
            }
        }
    }

    override fun setUpInsets() {
        binding.landingContentRoot.applyTopBottomPaddingInsets()
    }

    override fun setUpObserver() {
        // do nothing
    }

    companion object {
        @JvmStatic
        fun newInstance() = LandingFragment()
    }
}