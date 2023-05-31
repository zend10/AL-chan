package com.zen.alchan.ui.review

import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.repository.ContentRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.Review
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.enums.ReviewSort
import com.zen.alchan.helper.enums.getAniListMediaType
import com.zen.alchan.helper.enums.getStringResource
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getMediaType
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.helper.pojo.NullableItem
import com.zen.alchan.helper.pojo.ReviewAdapterComponent
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

class ReviewViewModel(
    private val userRepository: UserRepository,
    private val contentRepository: ContentRepository
) : BaseViewModel<ReviewParam>() {

    private val _reviewAdapterComponent = PublishSubject.create<ReviewAdapterComponent>()
    val reviewAdapterComponent: Observable<ReviewAdapterComponent>
        get() = _reviewAdapterComponent

    private val _sort = BehaviorSubject.createDefault(ReviewSort.NEWEST)
    val sort: Observable<ReviewSort>
        get() = _sort

    private val _mediaType = BehaviorSubject.createDefault(NullableItem<MediaType>(null))
    val mediaType: Observable<NullableItem<MediaType>>
        get() = _mediaType

    private val _reviews = BehaviorSubject.createDefault(listOf<Review?>())
    val reviews: Observable<List<Review?>>
        get() = _reviews

    private val _mediaTypes = PublishSubject.create<List<ListItem<MediaType?>>>()
    val mediaTypes: Observable<List<ListItem<MediaType?>>>
        get() = _mediaTypes

    private val _sorts = PublishSubject.create<List<ListItem<ReviewSort>>>()
    val sorts: Observable<List<ListItem<ReviewSort>>>
        get() = _sorts

    private val _emptyLayoutVisibility = BehaviorSubject.createDefault(false)
    val emptyLayoutVisibility: Observable<Boolean>
        get() = _emptyLayoutVisibility

    private var hasNextPage = false
    private var currentPage = 0

    private var mediaId: Int? = null
    private var userId: Int? = null

    override fun loadData(param: ReviewParam) {
        loadOnce {
            mediaId = param.media?.getId()
            userId = param.userId

            if (mediaId != null) {
                _mediaType.onNext(NullableItem(param.media?.type?.getMediaType()))
            }

            disposables.add(
                userRepository.getAppSetting()
                    .applyScheduler()
                    .subscribe {
                        _reviewAdapterComponent.onNext(ReviewAdapterComponent(it, mediaId != null, userId != null))
                        loadReviews()
                    }
            )
        }
    }

    fun reloadData() {
        loadReviews()
    }

    fun loadNextPage() {
        if ((state == State.LOADED || state == State.ERROR) && hasNextPage) {
            val currentReviews = ArrayList(_reviews.value ?: listOf())
            currentReviews.add(null)
            _reviews.onNext(currentReviews)

            loadReviews(true)
        }
    }

    private fun loadReviews(isLoadingNextPage: Boolean = false) {
        if (!isLoadingNextPage)
            _loading.onNext(true)

        state = State.LOADING

        disposables.add(
            contentRepository.getReviews(mediaId, userId, _mediaType.value?.data?.getAniListMediaType(), _sort.value ?: ReviewSort.NEWEST,  if (isLoadingNextPage) currentPage + 1 else 1)
                .applyScheduler()
                .doFinally {
                    if (!isLoadingNextPage) {
                        _loading.onNext(false)
                    }

                    _emptyLayoutVisibility.onNext(_reviews.value.isNullOrEmpty())
                }
                .subscribe(
                    {
                        hasNextPage = it.pageInfo.hasNextPage
                        currentPage = it.pageInfo.currentPage

                        if (isLoadingNextPage) {
                            val currentReviews = ArrayList(_reviews.value ?: listOf())
                            currentReviews.remove(null)
                            currentReviews.addAll(it.data)
                            _reviews.onNext(currentReviews)
                        } else {
                            _reviews.onNext(it.data)
                        }

                        state = State.LOADED
                    },
                    {
                        if (isLoadingNextPage) {
                            val currentReviews = ArrayList(_reviews.value ?: listOf())
                            currentReviews.remove(null)
                            _reviews.onNext(currentReviews)
                        }

                        _error.onNext(it.getStringResource())
                        state = State.ERROR
                    }
                )
        )
    }

    fun loadMediaTypes() {
        val items = ArrayList<ListItem<MediaType?>>()
        items.add(ListItem(R.string.all, null))
        items.add(ListItem(MediaType.ANIME.getStringResource(), MediaType.ANIME))
        items.add(ListItem(MediaType.MANGA.getStringResource(), MediaType.MANGA))
        _mediaTypes.onNext(items)
    }

    fun updateMediaType(newMediaType: MediaType?) {
        _mediaType.onNext(NullableItem(newMediaType))
        reloadData()
    }

    fun loadSorts() {
        val items = ArrayList<ListItem<ReviewSort>>()
        ReviewSort.values().forEach {
            items.add(ListItem(it.getStringResource(), it))
        }
        _sorts.onNext(items)
    }

    fun updateSort(newSort: ReviewSort) {
        _sort.onNext(newSort)
        reloadData()
    }
}