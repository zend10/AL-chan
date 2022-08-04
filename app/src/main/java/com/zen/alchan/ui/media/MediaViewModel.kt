package com.zen.alchan.ui.media

import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.repository.BrowseRepository
import com.zen.alchan.data.repository.MediaListRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.AiringSchedule
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getMediaType
import com.zen.alchan.helper.extensions.getString
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.pojo.MediaItem
import com.zen.alchan.helper.pojo.NullableItem
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import type.MediaFormat
import type.MediaListStatus
import type.MediaRelation
import type.MediaStatus

class MediaViewModel(
    private val browseRepository: BrowseRepository,
    private val userRepository: UserRepository,
    private val mediaListRepository: MediaListRepository
) : BaseViewModel<MediaParam>() {

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

    private val _addToListButtonText = BehaviorSubject.createDefault("")
    val addToListButtonText: Observable<String>
        get() = _addToListButtonText

    private val _mediaItemList = BehaviorSubject.createDefault(listOf<MediaItem>())
    val mediaItemList: Observable<List<MediaItem>>
        get() = _mediaItemList

    private val _coverImageUrlForPreview = PublishSubject.create<String>()
    val coverImageUrlForPreview: Observable<String>
        get() = _coverImageUrlForPreview

    private val _bannerImageUrlForPreview = PublishSubject.create<String>()
    val bannerImageUrlForPreview: Observable<String>
        get() = _bannerImageUrlForPreview

    private var mediaId = 0

    private var media = Media()
    private var appSetting = AppSetting()

    private val mediaRelationPriority = mapOf(
        Pair(MediaRelation.SOURCE, 0),
        Pair(MediaRelation.ADAPTATION, 1),
        Pair(MediaRelation.PARENT, 2),
        Pair(MediaRelation.PREQUEL, 3),
        Pair(MediaRelation.SEQUEL, 4),
        Pair(MediaRelation.ALTERNATIVE, 5),
        Pair(MediaRelation.SIDE_STORY, 6),
        Pair(MediaRelation.SPIN_OFF, 7),
        Pair(MediaRelation.SUMMARY, 8),
        Pair(MediaRelation.COMPILATION, 9),
        Pair(MediaRelation.CONTAINS, 10),
        Pair(MediaRelation.CHARACTER, 11),
        Pair(MediaRelation.OTHER, 12)
    )

    override fun loadData(param: MediaParam) {
        loadOnce {
            mediaId = param.mediaId

            disposables.add(
                userRepository.getIsAuthenticated().zipWith(userRepository.getAppSetting()) { isAuthenticated, appSetting ->
                    return@zipWith isAuthenticated to appSetting
                }
                    .applyScheduler()
                    .subscribe { (isAuthenticated, appSetting) ->
                        this.appSetting = appSetting
                        _isAuthenticated.onNext(isAuthenticated)
                        _mediaAdapterComponent.onNext(appSetting)
                        loadMedia()
                    }
            )

            if (media.getId() != 0)
                checkMediaList()
        }
    }

    private fun checkMediaList() {
        if (media.getId() == 0)
            return

        if (_isAuthenticated.value != true) {
            _isAuthenticated.onNext(_isAuthenticated.value ?: false)
            return
        }

        disposables.add(
            userRepository.getViewer(Source.CACHE)
                .flatMap {
                    mediaListRepository.getMediaListCollection(Source.CACHE, it.id, media.type?.getMediaType() ?: MediaType.ANIME)
                }
                .applyScheduler()
                .subscribe { mediaListCollection ->
                    var itemFound = false

                    mediaListCollection.lists.forEach collection@{ mediaListGroup ->
                        mediaListGroup.entries.forEach { mediaList ->
                            if (mediaList.media.getId() == mediaId) {
                                _addToListButtonText.onNext(mediaList.status?.getString(media.type?.getMediaType() ?: MediaType.ANIME) ?: "")
                                itemFound = true
                                return@collection
                            }
                        }
                    }

                    if (!itemFound) {
                        _addToListButtonText.onNext("")
                    }
                }
        )
    }

    private fun loadMedia() {
        _loading.onNext(true)

        disposables.add(
            browseRepository.getMedia(mediaId)
                .applyScheduler()
                .doFinally { _loading.onNext(false) }
                .map { media ->
                    media.relations.edges = media.relations.edges.sortedBy { mediaRelationPriority[it.relationType] ?: mediaRelationPriority.size }
                    media
                }
                .subscribe(
                    { media ->
                        this.media = media

                        checkMediaList()

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

                        val mediaItemList = ArrayList<MediaItem>()

                        if (media.genres.isNotEmpty())
                            mediaItemList.add(MediaItem(media, MediaItem.VIEW_TYPE_GENRE))

                        if (media.description.isNotBlank())
                            mediaItemList.add(MediaItem(media, MediaItem.VIEW_TYPE_SYNOPSIS))

                        if (media.characters.nodes.isNotEmpty())
                            mediaItemList.add(MediaItem(media, MediaItem.VIEW_TYPE_CHARACTERS))

                        mediaItemList.add(MediaItem(media, MediaItem.VIEW_TYPE_INFO))

                        if (media.tags.isNotEmpty())
                            mediaItemList.add(MediaItem(media, MediaItem.VIEW_TYPE_TAGS))

                        if (media.staff.edges.isNotEmpty())
                            mediaItemList.add(MediaItem(media, MediaItem.VIEW_TYPE_STAFF))

                        if (media.relations.edges.isNotEmpty())
                            mediaItemList.add(MediaItem(media, MediaItem.VIEW_TYPE_RELATIONS))

                        if (media.recommendations.nodes.isNotEmpty())
                            mediaItemList.add(MediaItem(media, MediaItem.VIEW_TYPE_RECOMMENDATIONS))

                        mediaItemList.add(MediaItem(media, MediaItem.VIEW_TYPE_LINKS))

                        _mediaItemList.onNext(mediaItemList)
                    },
                    {
                        _error.onNext(it.getStringResource())
                    }
                )
        )
    }

    fun loadCoverImage() {
        if (media.coverImage.extraLarge.isNotBlank())
            _coverImageUrlForPreview.onNext(media.coverImage.extraLarge)
    }

    fun loadBannerImage() {
        if (media.bannerImage.isNotBlank())
            _bannerImageUrlForPreview.onNext(media.bannerImage)
    }

    fun updateShouldShowSpoilerTags(shouldShowSpoiler: Boolean) {
        val currentMediaListItems = _mediaItemList.value ?: return
        val tagsSectionIndex = currentMediaListItems.indexOfFirst { it.viewType == MediaItem.VIEW_TYPE_TAGS }
        if (tagsSectionIndex != -1) {
            currentMediaListItems[tagsSectionIndex].showSpoilerTags = shouldShowSpoiler
            _mediaItemList.onNext(currentMediaListItems)
        }
    }
}