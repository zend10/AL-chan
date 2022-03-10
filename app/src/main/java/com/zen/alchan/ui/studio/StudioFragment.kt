package com.zen.alchan.ui.studio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.databinding.FragmentStudioBinding
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class StudioFragment : BaseFragment<FragmentStudioBinding, StudioViewModel>() {

    override val viewModel: StudioViewModel by viewModel()

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentStudioBinding {
        return FragmentStudioBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {

    }

    override fun setUpObserver() {

    }

    companion object {
        private const val STUDIO_ID = "studioId"

        @JvmStatic
        fun newInstance(studioId: Int) =
            StudioFragment().apply {
                arguments = Bundle().apply {
                    putInt(STUDIO_ID, studioId)
                }
            }
    }
}