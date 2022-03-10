package com.zen.alchan.ui.media

import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.repository.BrowseRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.AiringSchedule
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getMediaType
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.pojo.MediaItem
import com.zen.alchan.helper.pojo.NullableItem
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import type.MediaFormat
import type.MediaStatus

class MediaViewModel(
    private val browseRepository: BrowseRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val _mediaAdapterComponent = PublishSubject.create<AppSetting>()
    val mediaAdapterComponent: Observable<AppSetting>
        get() = _mediaAdapterComponent

    private val _bannerImage = BehaviorSubject.createDefault("")
    val bannerImage: Observable<String>
        get() = _bannerImage

    private val _coverImage = BehaviorSubject.createDefault("")
    val coverImage: Observable<String>
        get() = _coverImage

    private val _mediaTitle = BehaviorSubject.createDefault("")
    val mediaTitle: Observable<String>
        get() = _mediaTitle

    private val _mediaYear = BehaviorSubject.createDefault("")
    val mediaYear: Observable<String>
        get() = _mediaYear

    private val _mediaYearVisibility = BehaviorSubject.createDefault(false)
    val mediaYearVisibility: Observable<Boolean>
        get() = _mediaYearVisibility

    private val _mediaFormat = BehaviorSubject.createDefault(NullableItem<MediaFormat>(null))
    val mediaFormat: Observable<NullableItem<MediaFormat>>
        get() = _mediaFormat

    private val _mediaLength = BehaviorSubject.createDefault(0 to MediaType.ANIME)
    val mediaLength: Observable<Pair<Int, MediaType>>
        get() = _mediaLength

    private val _mediaLengthVisibility = BehaviorSubject.createDefault(false)
    val mediaLengthVisibility: Observable<Boolean>
        get() = _mediaLengthVisibility

    private val _airingSchedule = BehaviorSubject.createDefault(NullableItem<AiringSchedule>(null))
    val airingSchedule: Observable<NullableItem<AiringSchedule>>
        get() = _airingSchedule

    private val _averageScore = BehaviorSubject.createDefault(0)
    val averageScore: Observable<Int>
        get() = _averageScore

    private val _favorites = BehaviorSubject.createDefault(0)
    val favorites: Observable<Int>
        get() = _favorites

    private val _popularity = BehaviorSubject.createDefault(0)
    val popularity: Observable<Int>
        get() = _popularity

    private val _mediaItemList = BehaviorSubject.createDefault(listOf<MediaItem>())
    val mediaItemList: Observable<List<MediaItem>>
        get() = _mediaItemList

    private var mediaId = 0

    private var media = Media()
    private var appSetting = AppSetting()

    override fun loadData() {
        // do nothing
    }

    fun loadOnce(mediaId: Int) {
        loadOnce {
            this.mediaId = mediaId

            disposables.add(
                userRepository.getIsAuthenticated().zipWith(userRepository.getAppSetting()) { isAuthenticated, appSetting ->
                    return@zipWith isAuthenticated to appSetting
                }
                    .applyScheduler()
                    .subscribe { (isAuthenticated, appSetting) ->
                        this.appSetting = appSetting
                        _mediaAdapterComponent.onNext(appSetting)
                        loadMedia()
                    }
            )
        }
    }

    private fun loadMedia(isReloading: Boolean = false) {
        _loading.onNext(true)

        disposables.add(
            browseRepository.getMedia(mediaId)
                .applyScheduler()
                .doFinally { _loading.onNext(false) }
                .subscribe(
                    { media ->
                        this.media = media

                        _bannerImage.onNext(media.bannerImage)
                        _coverImage.onNext(media.getCoverImage(appSetting))
                        _mediaTitle.onNext(media.getTitle(appSetting))
                        _mediaYear.onNext(media.startDate?.year?.toString() ?: "TBA")
                        _mediaYearVisibility.onNext(media.startDate?.year != null || media.status == MediaStatus.NOT_YET_RELEASED)
                        _mediaFormat.onNext(NullableItem(media.format))
                        _mediaLength.onNext((media.getLength() ?: 0) to (media.type?.getMediaType() ?: MediaType.ANIME))
                        _mediaLengthVisibility.onNext(media.getLength() != null && media.getLength() != 0)
                        _airingSchedule.onNext(NullableItem(media.nextAiringEpisode))

                        _averageScore.onNext(media.averageScore)
                        _favorites.onNext(media.favourites)
                        _popularity.onNext(media.popularity)

                        _mediaItemList.onNext(
                            listOf(
                                MediaItem(media = media, viewType = MediaItem.VIEW_TYPE_SYNOPSIS),
                                MediaItem(media = media, viewType = MediaItem.VIEW_TYPE_CHARACTERS)
                            )
                        )
                    },
                    {
                        _error.onNext(it.getStringResource())
                    }
                )
        )
    }
}