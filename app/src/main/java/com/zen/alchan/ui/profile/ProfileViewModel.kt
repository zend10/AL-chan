package com.zen.alchan.ui.profile

import com.zen.alchan.R
import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.data.repository.BrowseRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.data.response.anilist.UserStatistics
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.formatTwoDecimal
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.pojo.ProfileItem
import com.zen.alchan.helper.pojo.Tendency
import com.zen.alchan.helper.service.clipboard.ClipboardService
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import type.MediaListStatus

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val browseRepository: BrowseRepository,
    private val clipboardService: ClipboardService
) : BaseViewModel() {

    private val _profileAdapterComponent = PublishSubject.create<AppSetting>()
    val profileAdapterComponent: Observable<AppSetting>
        get() = _profileAdapterComponent

    private val _viewerMenuItemVisibility = BehaviorSubject.createDefault(false)
    val viewerMenuItemVisibility: Observable<Boolean>
        get() = _viewerMenuItemVisibility

    private val _bestFriendVisibility = BehaviorSubject.createDefault(false)
    val bestFriendVisibility: Observable<Boolean>
        get() = _bestFriendVisibility

    private val _notLoggedInLayoutVisibility = BehaviorSubject.createDefault(false)
    val notLoggedInLayoutVisibility: Observable<Boolean>
        get() = _notLoggedInLayoutVisibility

    private val _avatarUrl = BehaviorSubject.createDefault("" to false)
    val avatarUrl: Observable<Pair<String, Boolean>>
        get() = _avatarUrl

    private val _bannerUrl = BehaviorSubject.createDefault("")
    val bannerUrl: Observable<String>
        get() = _bannerUrl

    private val _username = BehaviorSubject.createDefault("")
    val username: Observable<String>
        get() = _username

    private val _animeCompletedCount = BehaviorSubject.createDefault(0)
    val animeCompletedCount: Observable<Int>
        get() = _animeCompletedCount

    private val _mangaCompletedCount = BehaviorSubject.createDefault(0)
    val mangaCompletedCount: Observable<Int>
        get() = _mangaCompletedCount

    private val _followingCount = BehaviorSubject.createDefault(0)
    val followingCount: Observable<Int>
        get() = _followingCount

    private val _followersCount = BehaviorSubject.createDefault(0)
    val followersCount: Observable<Int>
        get() = _followersCount

    private val _profileItemList = BehaviorSubject.createDefault(listOf<ProfileItem>())
    val profileItemList: Observable<List<ProfileItem>>
        get() = _profileItemList

    private val _profileUrlForWebView = PublishSubject.create<String>()
    val profileUrlForWebView: Observable<String>
        get() = _profileUrlForWebView

    private val _profileUrlForShareSheet = PublishSubject.create<String>()
    val profileUrlForShareSheet: Observable<String>
        get() = _profileUrlForShareSheet

    private val _avatarUrlForPreview = PublishSubject.create<Pair<String, Boolean>>()
    val avatarUrlForPreview: Observable<Pair<String, Boolean>>
        get() = _avatarUrlForPreview

    private val _bannerUrlForPreview = PublishSubject.create<String>()
    val bannerUrlForPreview: Observable<String>
        get() = _bannerUrlForPreview

    private var userId = 0
    private var user = User()
    private var appSetting = AppSetting()

    override fun loadData() {
        // do nothing
    }

    fun loadData(userId: Int) {
        this.userId = userId

        loadOnce {
            disposables.add(
                userRepository.getIsAuthenticated().zipWith(userRepository.getAppSetting()) { isAuthenticated, appSetting ->
                    return@zipWith isAuthenticated to appSetting
                }
                    .applyScheduler()
                    .subscribe { (isAuthenticated, appSetting) ->
                        _notLoggedInLayoutVisibility.onNext(userId == 0 && !isAuthenticated)
                        _viewerMenuItemVisibility.onNext(userId == 0)
                        _profileAdapterComponent.onNext(appSetting)
                        loadUserData()
                    }
            )
        }
    }

    fun logout() {
        userRepository.logoutAsGuest()
    }

    fun reloadData() {
        loadUserData()
    }

    fun loadProfileUrlForWebView() {
        _profileUrlForWebView.onNext(user.siteUrl)
    }

    fun loadProfileUrlForShareSheet() {
        _profileUrlForShareSheet.onNext(user.siteUrl)
    }

    fun copyProfileUrl() {
        disposables.add(
            clipboardService.copyPlainText(user.siteUrl)
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

    fun loadAvatarUrl(useCircular: Boolean) {
        if (user.avatar.getImageUrl(appSetting).isNotBlank())
            _avatarUrlForPreview.onNext(user.avatar.large to useCircular)
    }

    fun loadBannerUrl() {
        if (user.bannerImage.isNotBlank())
            _bannerUrlForPreview.onNext(user.bannerImage)
    }

    private fun loadUserData() {
        _loading.onNext(true)

        val userObservable = if (userId == 0)
            userRepository.getViewer()
        else
            browseRepository.getUser(userId)

        disposables.add(
            userObservable
                .flatMap {  user ->
                    Observable.just(user).zipWith(userRepository.getFollowingAndFollowersCount(user.id)) { user, followingAndFollowersCount ->
                        return@zipWith user to followingAndFollowersCount
                    }
                }
                .zipWith(userRepository.getAppSetting()) { userAndFollowsCount, appSetting ->
                    return@zipWith Triple(userAndFollowsCount.first, userAndFollowsCount.second, appSetting)
                }
                .applyScheduler()
                .subscribe(
                    { (user, followingAndFollowersCount, appSetting) ->
                        this.user = user
                        this.appSetting = appSetting

                        _avatarUrl.onNext(user.avatar.large to appSetting.useCircularAvatarForProfile)
                        _bannerUrl.onNext(user.bannerImage)
                        _username.onNext(user.name)

                        _animeCompletedCount.onNext(
                            user.statistics.anime.statuses.find { anime -> anime.status == MediaListStatus.COMPLETED }?.count ?: 0
                        )
                        _mangaCompletedCount.onNext(
                            user.statistics.manga.statuses.find { manga -> manga.status == MediaListStatus.COMPLETED }?.count ?: 0
                        )

                        _followingCount.onNext(followingAndFollowersCount.first)
                        _followersCount.onNext(followingAndFollowersCount.second)

                        emitProfileItemList()

                        _loading.onNext(false)
                        state = State.LOADED
                    },
                    {
                        _loading.onNext(false)
                        _error.onNext(it.getStringResource())
                        state = State.ERROR
                    }
                )
        )
    }

    private fun emitProfileItemList() {
        val profileItemList = ArrayList<ProfileItem>()
        profileItemList.add(ProfileItem(bio = user.about, viewType = ProfileItem.VIEW_TYPE_BIO))
        profileItemList.add(ProfileItem(animeStats = user.statistics.anime, mangaStats = user.statistics.manga, viewType = ProfileItem.VIEw_TYPE_STATS))

        if (user.favourites.anime.nodes.isNotEmpty())
            profileItemList.add(ProfileItem(favoriteMedia = user.favourites.anime.nodes.take(FAVORITE_LIMIT), viewType = ProfileItem.VIEW_TYPE_FAVORITE_ANIME))

        if (user.favourites.manga.nodes.isNotEmpty())
            profileItemList.add(ProfileItem(favoriteMedia = user.favourites.manga.nodes.take(FAVORITE_LIMIT), viewType = ProfileItem.VIEW_TYPE_FAVORITE_MANGA))

        if (user.favourites.characters.nodes.isNotEmpty())
            profileItemList.add(ProfileItem(favoriteCharacters = user.favourites.characters.nodes.take(FAVORITE_LIMIT), viewType = ProfileItem.VIEW_TYPE_FAVORITE_CHARACTER))

        if (user.favourites.staff.nodes.isNotEmpty())
            profileItemList.add(ProfileItem(favoriteStaff = user.favourites.staff.nodes.take(FAVORITE_LIMIT), viewType = ProfileItem.VIEW_TYPE_FAVORITE_STAFF))

        if (user.favourites.studios.nodes.isNotEmpty())
            profileItemList.add(ProfileItem(favoriteStudios = user.favourites.studios.nodes.take(FAVORITE_LIMIT), viewType = ProfileItem.VIEW_TYPE_FAVORITE_STUDIO))

        val animeTendency = getTendency(user.statistics.anime)
        val mangaTendency = getTendency(user.statistics.manga)
        if (animeTendency != null || mangaTendency != null)
            profileItemList.add(ProfileItem(tendency = animeTendency to mangaTendency, viewType = ProfileItem.VIEW_TYPE_TENDENCY))

        _profileItemList.onNext(profileItemList)
    }

    private fun getTendency(statistics: UserStatistics): Tendency? {
        if (statistics.statuses.find { it.status == MediaListStatus.COMPLETED }?.count ?: 0 < TENDENCY_MINIMUM_COMPLETED)
            return null

        var mostFavoriteGenres = ""
        var leastFavoriteGenre = ""
        var mostFavoriteTags = ""
        var mostFavoriteYear = ""
        var startYear = ""
        var completedSeriesPercentage = ""

        val totalCount = statistics.statuses.filter { it.status != MediaListStatus.PLANNING }.sumBy { it.count }.toDouble()

        if (statistics.genres.isNotEmpty()) {
            val genreWeightedScores = statistics.genres.map {
                Pair(it.genre, calculateWeightedScore(it.count, totalCount, it.meanScore))
            }

            val sortedGenreWeightedScores = genreWeightedScores.sortedByDescending { it.second }

            mostFavoriteGenres = sortedGenreWeightedScores
                .take(TENDENCY_FAVORITES_COUNT)
                .joinToString(TENDENCY_FAVORITES_SEPARATOR) { it.first }

            if (sortedGenreWeightedScores.size > TENDENCY_FAVORITES_COUNT) {
                leastFavoriteGenre = sortedGenreWeightedScores.last().first
            }
        }

        if (statistics.tags.isNotEmpty()) {
            val tagWeightedScores = statistics.tags.filter { it.tag?.isAdult == false }.map {
                Pair(it.tag?.name ?: "", calculateWeightedScore(it.count, totalCount, it.meanScore))
            }

            mostFavoriteTags = tagWeightedScores
                .sortedByDescending { it.second }
                .take(TENDENCY_FAVORITES_COUNT)
                .joinToString(TENDENCY_FAVORITES_SEPARATOR) { it.first }
        }

        if (statistics.releaseYears.isNotEmpty()) {
            val yearWeightedScores = statistics.releaseYears.map {
                Pair(it.releaseYear.toString(), calculateWeightedScore(it.count, totalCount, it.meanScore))
            }

            mostFavoriteYear = yearWeightedScores
                .sortedByDescending { it.second }
                .take(TENDENCY_FAVORITES_COUNT)
                .joinToString(TENDENCY_FAVORITES_SEPARATOR) { it.first }
        }

        if (statistics.startYears.isNotEmpty()) {
            startYear = statistics.startYears.minOf { it.startYear }.toString()
        }

        if (statistics.statuses.isNotEmpty()) {
            val completedTotal = statistics.statuses.filter {
                it.status == MediaListStatus.COMPLETED || it.status == MediaListStatus.REPEATING
            }.sumBy { it.count }

            if (totalCount != 0.0) {
                val completedPercentage = completedTotal.toDouble() / totalCount
                completedSeriesPercentage = (completedPercentage * 100).formatTwoDecimal() + "%"
            }
        }

        return Tendency(
            mostFavoriteGenres,
            leastFavoriteGenre,
            mostFavoriteTags,
            mostFavoriteYear,
            startYear,
            completedSeriesPercentage
        )
    }

    private fun calculateWeightedScore(count: Int, totalCount: Double, meanScore: Double): Double {
        return count.toDouble() / totalCount + meanScore / 100.0
    }

    companion object {
        private const val TENDENCY_FAVORITES_COUNT = 3
        private const val TENDENCY_FAVORITES_SEPARATOR = "/"
        private const val TENDENCY_MINIMUM_COMPLETED = 20

        private const val FAVORITE_LIMIT = 9
    }
}