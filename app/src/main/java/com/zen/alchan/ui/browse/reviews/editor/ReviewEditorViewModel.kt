package com.zen.alchan.ui.browse.reviews.editor

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.BrowseRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.User
import type.MediaType

class ReviewEditorViewModel(private val browseRepository: BrowseRepository) : ViewModel() {

    var reviewId: Int? = null
    var mediaId: Int? = null

    var reviewString = ""
    var summaryString = ""
    var score = 0
}