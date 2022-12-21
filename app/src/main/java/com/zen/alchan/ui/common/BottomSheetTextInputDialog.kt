package com.zen.alchan.ui.common

import android.text.InputFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.databinding.DialogBottomSheetTextInputBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.pojo.TextInputSetting
import com.zen.alchan.ui.base.BaseDialogFragment

class BottomSheetTextInputDialog : BaseDialogFragment<DialogBottomSheetTextInputBinding>() {

    private var listener: BottomSheetTextInputListener? = null
    private var currentText = ""
    private var textInputSetting = TextInputSetting()

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogBottomSheetTextInputBinding {
        return DialogBottomSheetTextInputBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            dialogEditText.setText(currentText)

            dialogEditText.apply {
                inputType = textInputSetting.inputType
                isSingleLine = textInputSetting.singleLine
                filters = arrayOf(InputFilter.LengthFilter(textInputSetting.characterLimit))
                hint = getString(textInputSetting.hintStringResource)
                setSelection(0, dialogEditText.text?.length ?: 0)
            }

            dialogSaveButton.clicks {
                val newText = dialogEditText.text?.toString()?.trim() ?: ""
                listener?.getNewText(newText)
                dismiss()
            }

            dialogEditText.requestFocus()
            openKeyboard()
        }
    }

    override fun setUpObserver() {
        // do nothing
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listener = null
    }

    companion object {
        fun newInstance(currentText: String, textInputSetting: TextInputSetting, listener: BottomSheetTextInputListener) = BottomSheetTextInputDialog().apply {
            this.currentText = currentText
            this.textInputSetting = textInputSetting
            this.listener = listener
        }
    }

    interface BottomSheetTextInputListener {
        fun getNewText(newText: String)
    }
}