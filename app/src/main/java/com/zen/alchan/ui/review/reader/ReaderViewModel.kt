package com.zen.alchan.ui.review.reader

import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.repository.ContentRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.Review
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.pojo.NullableItem
import com.zen.alchan.helper.service.clipboard.ClipboardService
import com.zen.alchan.helper.utils.TimeUtil
import com.zen.alchan.type.MediaType
import com.zen.alchan.type.ReviewRating
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

class ReaderViewModel(
    private val userRepository: UserRepository,
    private val contentRepository: ContentRepository,
    private val clipboardService: ClipboardService
) : BaseViewModel<ReaderParam>() {

    private val _bannerImage = BehaviorSubject.createDefault("")
    val bannerImage: Observable<String>
        get() = _bannerImage

    private val _mediaType = BehaviorSubject.createDefault(R.string.anime_review)
    val mediaType: Observable<Int>
        get() = _mediaType

    private val _titleAndUsername = BehaviorSubject.createDefault("" to "")
    val titleAndUsername: Observable<Pair<String, String>>
        get() = _titleAndUsername

    private val _summary = BehaviorSubject.createDefault("")
    val summary: Observable<String>
        get() = _summary

    private val _avatar = BehaviorSubject.createDefault("")
    val avatar: Observable<String>
        get() = _avatar

    private val _username = BehaviorSubject.createDefault("")
    val username: Observable<String>
        get() = _username

    private val _date = BehaviorSubject.createDefault("")
    val date: Observable<String>
        get() = _date

    private val _reviewContent = BehaviorSubject.createDefault("")
    val reviewContent: Observable<String>
        get() = _reviewContent

    private val _score = BehaviorSubject.createDefault(0)
    val score: Observable<Int>
        get() = _score

    private val _isLiked = BehaviorSubject.createDefault(NullableItem<Boolean>(null))
    val isLiked: Observable<NullableItem<Boolean>>
        get() = _isLiked

    private val _ratingAndRatingAmount = BehaviorSubject.createDefault(0 to 0)
    val ratingAndRatingAmount: Observable<Pair<Int, Int>>
        get() = _ratingAndRatingAmount

    private val _reviewLink = PublishSubject.create<String>()
    val reviewLink: Observable<String>
        get() = _reviewLink

    private val _updatedReview = PublishSubject.create<Review>()
    val updatedReview: Observable<Review>
        get() = _updatedReview

    private lateinit var appSetting: AppSetting
    private lateinit var review: Review

    override fun loadData(param: ReaderParam) {
        loadOnce {
            review = param.review

            disposables.add(
                userRepository.getAppSetting()
                    .applyScheduler()
                    .subscribe {
                        appSetting = it

                        val media = review.media

                        if (media.bannerImage.isNotBlank())
                            _bannerImage.onNext(media.bannerImage)

                        _mediaType.onNext(
                            if (media.type == MediaType.MANGA)
                                R.string.manga_review
                            else
                                R.string.anime_review
                        )

                        _titleAndUsername.onNext(media.getTitle(appSetting) to review.user.name)

                        _summary.onNext(review.summary)

                        _avatar.onNext(review.user.avatar.getImageUrl(appSetting))

                        _username.onNext(review.user.name)

                        _date.onNext(TimeUtil.displayInDateFormat(review.createdAt))

                        _reviewContent.onNext(review.body)

                        _score.onNext(review.score)

                        _isLiked.onNext(
                            NullableItem(
                                when (review.userRating) {
                                    ReviewRating.UP_VOTE -> true
                                    ReviewRating.DOWN_VOTE -> false
                                    else -> null
                                }
                            )
                        )

                        _ratingAndRatingAmount.onNext(review.rating to review.ratingAmount)
                    }
            )
        }
    }

    fun loadReviewLink() {
        _reviewLink.onNext(review.siteUrl)
    }

    fun copyReviewLink() {
        disposables.add(
            clipboardService.copyPlainText(review.siteUrl)
                .applyScheduler()
                .subscribe(
                    {
                        _success.onNext(R.string.link_copied)
                    },
                    {
                        it.printStackTrace()
                    }
                )
        )
    }

    fun like() {
        rateReview(if (_isLiked.value?.data == true) ReviewRating.NO_VOTE else ReviewRating.UP_VOTE)
    }

    fun dislike() {
        rateReview(if (_isLiked.value?.data == false) ReviewRating.NO_VOTE else ReviewRating.DOWN_VOTE)
    }

    private fun rateReview(rating: ReviewRating) {
        _loading.onNext(true)
        state = State.LOADING

        disposables.add(
            contentRepository.rateReview(review.id, rating)
                .applyScheduler()
                .doFinally {
                    _loading.onNext(false)
                }
                .subscribe(
                    {
                        review.rating = it.rating
                        review.ratingAmount = it.ratingAmount
                        review.userRating = it.userRating

                        _ratingAndRatingAmount.onNext(review.rating to review.ratingAmount)
                        _isLiked.onNext(
                            NullableItem(
                                when (review.userRating) {
                                    ReviewRating.UP_VOTE -> true
                                    ReviewRating.DOWN_VOTE -> false
                                    else -> null
                                }
                            )
                        )

                        _updatedReview.onNext(review)

                        state = State.LOADED
                    },
                    {
                        _error.onNext(it.getStringResource())
                        state = State.ERROR
                    }
                )
        )
    }
}