package com.zen.alchan.ui.common

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.SocialRepository
import com.zen.alchan.helper.enums.EditorType

class TextEditorViewModel(private val socialRepository: SocialRepository) : ViewModel() {

    var editorType: EditorType? = null

    var activityId: Int? = null
    var originalText: String? = null
    var recipientId: Int? = null
    var recipientName: String? = null
    var isInit = false

    val postTextActivityResponse by lazy {
        socialRepository.postTextActivityResponse
    }

    val postMessageActivityResponse by lazy {
        socialRepository.postMessageActivityResponse
    }

    fun post(text: String, private: Boolean? = null) {
        if (recipientId != null) {
            socialRepository.postMessageActivity(activityId, text, recipientId!!, private ?: false)
        } else {
            socialRepository.postTextActivity(activityId, text)
        }
    }
}