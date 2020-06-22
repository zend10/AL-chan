package com.zen.alchan.ui.common

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.SocialRepository
import com.zen.alchan.helper.enums.EditorType

class TextEditorViewModel(private val socialRepository: SocialRepository) : ViewModel() {

    var editorType: EditorType? = null
    var activityId: Int? = null

    val postTextActivityResponse by lazy {
        socialRepository.postTextActivityResponse
    }

    fun post(text: String) {
        socialRepository.postTextActivity(activityId, text)
    }
}