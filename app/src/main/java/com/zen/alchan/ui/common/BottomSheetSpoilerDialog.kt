package com.zen.alchan.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.databinding.DialogBottomSheetSpoilerBinding
import com.zen.alchan.helper.utils.MarkdownUtil
import com.zen.alchan.ui.base.BaseDialogFragment

class BottomSheetSpoilerDialog : BaseDialogFragment<DialogBottomSheetSpoilerBinding>() {

    private var spoilerText = ""

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogBottomSheetSpoilerBinding {
        return DialogBottomSheetSpoilerBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        val markdownSetup = MarkdownUtil.getMarkdownSetup(requireContext(), screenWidth)
        MarkdownUtil.applyMarkdown(requireContext(), markdownSetup, binding.dialogSpoilerText, spoilerText)
    }

    override fun setUpObserver() {
        // do nothing
    }

    companion object {
        fun newInstance(spoilerText: String) =
            BottomSheetSpoilerDialog().apply {
                this.spoilerText = spoilerText
            }
    }
}