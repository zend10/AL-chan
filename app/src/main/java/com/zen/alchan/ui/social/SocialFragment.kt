package com.zen.alchan.ui.social

import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.databinding.FragmentSocialBinding
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class SocialFragment : BaseFragment<FragmentSocialBinding, SocialViewModel>() {

    override val viewModel: SocialViewModel by viewModel()

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSocialBinding {
        return FragmentSocialBinding.inflate(inflater, container, false)
    }

    override fun setUpObserver() {

    }

    override fun setUpLayout() {

    }

    companion object {
        @JvmStatic
        fun newInstance() = SocialFragment()
    }
}