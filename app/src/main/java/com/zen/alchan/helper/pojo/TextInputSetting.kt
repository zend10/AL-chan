package com.zen.alchan.helper.pojo

import android.text.InputFilter
import android.text.InputType

data class TextInputSetting(
    val inputType: Int = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES,
    val singleLine: Boolean = true,
    val characterLimit: Int = 30
)