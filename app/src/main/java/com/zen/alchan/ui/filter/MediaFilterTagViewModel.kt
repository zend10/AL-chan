package com.zen.alchan.ui.filter

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.MediaRepository
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class MediaFilterTagViewModel(private val mediaRepository: MediaRepository) : ViewModel() {

    private val tagList = ArrayList<MediaFilterTagDialog.MediaFilterTagItem>()
    val filteredTagList = ArrayList<MediaFilterTagDialog.MediaFilterTagItem>()
    val selectedTags = ArrayList<String>()

    fun initTagList() {
        val headerSet = HashSet<String>()
        mediaRepository.tagList.sortedBy { it.category }.forEach {
            if (it.category != null) {
                if (headerSet.add(it.category)) {
                    tagList.add(MediaFilterTagDialog.MediaFilterTagItem(
                        it.category,
                        null,
                        isCategory = true,
                        isChecked = false
                    ))
                }
                tagList.add(MediaFilterTagDialog.MediaFilterTagItem(
                    it.name,
                    it.description,
                    isCategory = false,
                    isChecked = selectedTags.contains(it.name)
                ))
            }
        }

        filteredTagList.addAll(tagList)
    }

    fun filterTagList(keyword: String?) {
        filteredTagList.clear()

        if (keyword.isNullOrBlank()) {
            filteredTagList.addAll(tagList)
            return
        }

        tagList.forEach {
            if (it.isCategory) {
                filteredTagList.add(it)
            } else if (it.name.toLowerCase(Locale.US).contains(keyword.toLowerCase(Locale.US))) {
                filteredTagList.add(it)
            }
        }
    }
}