package com.zen.alchan.ui.common

import android.content.Context
import android.text.InputFilter
import android.view.inputmethod.InputMethodManager
import com.zen.alchan.R
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.pojo.TextInputSetting
import com.zen.alchan.ui.base.BaseDialogFragment
import kotlinx.android.synthetic.main.dialog_bottom_sheet_text_input.*

class BottomSheetTextInputDialog : BaseDialogFragment(R.layout.dialog_bottom_sheet_text_input) {

    private var listener: BottomSheetTextInputListener? = null
    private var currentText = ""
    private var textInputSetting = TextInputSetting.DEFAULT_TEXT_INPUT_SETTING

    override fun setUpLayout() {
        dialogEditText.setText(currentText)

        dialogEditText.apply {
            inputType = textInputSetting.inputType
            isSingleLine = textInputSetting.singleLine
            filters = arrayOf(InputFilter.LengthFilter(textInputSetting.characterLimit))
            setSelection(0, dialogEditText.text?.length ?: 0)
        }

        dialogSaveButton.clicks {
            val newText = dialogEditText.text?.toString()?.trim() ?: ""
            if (newText.isNotBlank()) {
                listener?.getNewText(newText)
            }
            dismiss()
        }

        dialogEditText.requestFocus()
        dialogEditText.postDelayed({
            val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.showSoftInput(dialogEditText, 0)
        }, 200)
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