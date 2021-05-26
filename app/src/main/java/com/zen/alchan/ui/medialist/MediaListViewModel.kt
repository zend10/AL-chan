package com.zen.alchan.ui.medialist

import com.zen.alchan.data.repository.MediaListRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.pojo.MediaListItem
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import type.MediaType

class MediaListViewModel(
    private val mediaListRepository: MediaListRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val _mediaListItems = BehaviorSubject.createDefault(listOf<MediaListItem>())
    val mediaListItems: Observable<List<MediaListItem>>
        get() = _mediaListItems

    var mediaType: MediaType? = null
    var userId = 0

    override fun loadData() {
        if (userId == 0) {
            disposables.add(
                userRepository.viewer
                    .applyScheduler()
                    .subscribe {
                        userId = it.id
                        getMediaListCollection()
                    }
            )

            userRepository.loadViewer(Source.CACHE)
        } else {
            getMediaListCollection()
        }
    }

    private fun getMediaListCollection() {
        disposables.add(
            mediaListRepository.getMediaListCollection(userId, mediaType ?: MediaType.ANIME)
                .applyScheduler()
                .subscribe(
                    { mediaListCollection ->
                        val tempList = ArrayList<MediaListItem>()
                        mediaListCollection.lists.forEach { mediaListGroup ->
                            mediaListGroup.entries.forEach { mediaList ->
                                tempList.add(MediaListItem(mediaList, viewType = MediaListItem.VIEW_TYPE_MEDIA_LIST))
                            }
                        }
                        _mediaListItems.onNext(tempList)
                    },
                    {

                    }
                )
        )
    }
}