package com.zen.alchan.ui.customise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.databinding.FragmentCustomiseBinding
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.extensions.applyBottomSidePaddingInsets
import com.zen.alchan.helper.extensions.applySidePaddingInsets
import com.zen.alchan.helper.extensions.applyTopPaddingInsets
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class CustomiseFragment : BaseFragment<FragmentCustomiseBinding, CustomiseViewModel>() {

    override val viewModel: CustomiseViewModel by viewModel()
    private val sharedViewModel by sharedViewModel<SharedCustomiseViewModel>()

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCustomiseBinding {
        return FragmentCustomiseBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            viewModel.mediaType = MediaType.valueOf(it.getString(MEDIA_TYPE) ?: MediaType.ANIME.name)
        }
    }

    override fun setUpLayout() {
        binding.apply {

        }
    }

    override fun setUpInsets() {
        binding.apply {
            defaultToolbar.defaultToolbar.applyTopPaddingInsets()
            customiseLayout.applySidePaddingInsets()
            customiseApplyLayout.twoButtonsLayout.applyBottomSidePaddingInsets()
        }
    }

    override fun setUpObserver() {

        viewModel.loadData()
    }

    companion object {
        private const val MEDIA_TYPE = "mediaType"

        @JvmStatic
        fun newInstance(mediaType: MediaType) =
            CustomiseFragment().apply {
                arguments = Bundle().apply {
                    putString(MEDIA_TYPE, mediaType.name)
                }
            }
    }
}