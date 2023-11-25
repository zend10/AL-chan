package com.zen.alchan.helper.pojo

import android.text.InputType
import com.zen.alchan.R

data class TextInputSetting(
    val inputType: Int = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES,
    val singleLine: Boolean = true,
    val characterLimit: Int = 30,
    val hintStringResource: Int = R.string.type_here
)