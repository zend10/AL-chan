package com.zen.alchan.ui.browse

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.BrowseRepository

class BrowseViewModel(private val browseRepository: BrowseRepository) : ViewModel() {

    val idFromNameData by lazy {
        browseRepository.idFromNameData
    }

    fun getIdFromName(name: String) {
        browseRepository.getIdFromName(name)
    }
}