package com.zen.alchan.ui.profile

import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.repository.BrowseRepository
import com.zen.alchan.data.repository.MediaListRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.data.response.anilist.UserStatistics
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.convertFromSnakeCase
import com.zen.alchan.helper.extensions.formatTwoDecimal
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.pojo.Affinity
import com.zen.alchan.helper.pojo.ProfileItem
import com.zen.alchan.helper.pojo.Tendency
import com.zen.alchan.helper.service.clipboard.ClipboardService
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import com.zen.alchan.type.MediaListStatus
import kotlin.math.pow
import kotlin.math.sqrt

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val browseRepository: BrowseRepository,
    private val mediaListRepository: MediaListRepository,
    private val clipboardService: ClipboardService
) : BaseViewModel<ProfileParam>() {

    private val _profileAdapterComponent = PublishSubject.create<AppSetting>()
    val profileAdapterComponent: Observable<AppSetting>
        get() = _profileAdapterComponent

    private val _viewerMenuItemVisibility = BehaviorSubject.createDefault(false)
    val viewerMenuItemVisibility: Observable<Boolean>
        get() = _viewerMenuItemVisibility

    private val _bestFriendVisibility = BehaviorSubject.createDefault(false)
    val bestFriendVisibility: Observable<Boolean>
        get() = _bestFriendVisibility

    private val _reportMenuItemVisibility = BehaviorSubject.createDefault(false)
    val reportMenuItemVisibility: Observable<Boolean>
        get() = _reportMenuItemVisibility

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

    private val _donatorAndModBadge = BehaviorSubject.createDefault<Pair<String?, String?>>(null to null)
    val donatorAndModBadge: Observable<Pair<String?, String?>>
        get() = _donatorAndModBadge

    private val _followButtonVisibility = BehaviorSubject.createDefault(false)
    val followButtonVisibility: Observable<Boolean>
        get() = _followButtonVisibility

    private val _followButtonText = BehaviorSubject.createDefault(R.string.follow)
    val followButtonText: Observable<Int>
        get() = _followButtonText

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

    private val _currentUserId = PublishSubject.create<Int>()
    val currentUserId: Observable<Int>
        get() = _currentUserId

    private var isViewer = false
    private var viewer = User()

    private var userId: Int? = null
    private var name: String? = null
    private var user = User()
    private var appSetting = AppSetting()

    override fun loadData(param: ProfileParam) {
        userId = param.userId
        name = param.username

        loadOnce {
            disposables.add(
                userRepository.getIsAuthenticated().zipWith(userRepository.getAppSetting()) { isAuthenticated, appSetting ->
                    return@zipWith isAuthenticated to appSetting
                }
                    .applyScheduler()
                    .subscribe { (isAuthenticated, appSetting) ->
                        _notLoggedInLayoutVisibility.onNext(userId == null && name == null && !isAuthenticated)
                        _viewerMenuItemVisibility.onNext(userId == null && name == null)
                        _profileAdapterComponent.onNext(appSetting)
                        loadUserData()
                    }
            )

            disposables.add(
                userRepository.refreshFavoriteTrigger
                    .applyScheduler()
                    .subscribe {
                        user = it
                        emitProfileItemList()
                    }
            )
        }
    }

    fun logout() {
        userRepository.logoutAsGuest()
    }

    fun reloadData() {
        loadUserData(true)
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
        if (user.avatar.large.isNotBlank())
            _avatarUrlForPreview.onNext(user.avatar.large to useCircular)
    }

    fun loadBannerUrl() {
        if (user.bannerImage.isNotBlank())
            _bannerUrlForPreview.onNext(user.bannerImage)
    }

    fun toggleFollow() {
        if (isViewer) {
            _error.onNext(R.string.did_you_just_try_to_follow_yourself)
            return
        }

        _loading.onNext(true)

        disposables.add(
            userRepository.toggleFollow(user.id)
                .applyScheduler()
                .doFinally { _loading.onNext(false) }
                .subscribe(
                    { isFollowing ->
                        user.isFollowing = isFollowing
                        setFollowButtonText()
                    },
                    {
                        _error.onNext(it.getStringResource())
                    }
                )
        )
    }

    private fun loadUserData(isReloading: Boolean = false) {
        _loading.onNext(true)

        val userObservable = if (userId == null && name == null)
            userRepository.getViewer(if (isReloading) Source.NETWORK else null)
                .map { user ->
                    isViewer = true
                    viewer = user
                    user
                }
        else
            browseRepository.getUser(userId, name)
                .flatMap { user ->
                    userRepository.getViewer(Source.CACHE)
                        .map { viewer ->
                            isViewer = user.id == viewer.id
                            this.viewer = viewer
                            user
                        }
                }

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

                        _reportMenuItemVisibility.onNext(!isViewer)
                        _currentUserId.onNext(user.id)
                        _avatarUrl.onNext(user.avatar.large to appSetting.useCircularAvatarForProfile)
                        _bannerUrl.onNext(user.bannerImage)
                        _username.onNext(user.name)
                        _donatorAndModBadge.onNext(
                            Pair(
                                if (user.donatorTier != 0) user.donatorBadge else null,
                                user.moderatorRoles.firstOrNull()?.name?.convertFromSnakeCase()
                            )
                        )

                        _followButtonVisibility.onNext(!isViewer)
                        setFollowButtonText()

                        _animeCompletedCount.onNext(
                            user.statistics.anime.statuses.find { anime -> anime.status == MediaListStatus.COMPLETED }?.count ?: 0
                        )
                        _mangaCompletedCount.onNext(
                            user.statistics.manga.statuses.find { manga -> manga.status == MediaListStatus.COMPLETED }?.count ?: 0
                        )

                        _followingCount.onNext(followingAndFollowersCount.first)
                        _followersCount.onNext(followingAndFollowersCount.second)

                        emitProfileItemList()
                        getAffinity()

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

        if (user.about.isNotBlank())
            profileItemList.add(ProfileItem(bio = user.about, viewType = ProfileItem.VIEW_TYPE_BIO))

        if (!isViewer)
            profileItemList.add(ProfileItem(affinity = Pair(Affinity(status = Affinity.AFFINITY_STATUS_LOADING), Affinity(status = Affinity.AFFINITY_STATUS_LOADING)), viewType = ProfileItem.VIEW_TYPE_AFFINITY))

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

    private fun getAffinity() {
        if (isViewer)
            return

        disposables.add(
            Observable.zip(
                mediaListRepository.getMediaListCollection(user = user, mediaType = MediaType.ANIME),
                mediaListRepository.getMediaListCollection(user = user, mediaType = MediaType.MANGA)
            ) { otherPersonAnimeList, otherPersonMangaList ->
                val otherPersonAnimeMediaIdToScoreMap = HashMap<Int, Double>()
                val otherPersonMangaMediaIdToScoreMap = HashMap<Int, Double>()

                otherPersonAnimeList.lists.forEach {
                    it.entries.forEach { entry ->
                        if (entry.media.getId() != 0 &&
                            entry.score != 0.0 &&
                            !otherPersonAnimeMediaIdToScoreMap.containsKey(entry.media.getId())
                        ) {
                            otherPersonAnimeMediaIdToScoreMap[entry.media.getId()] = entry.score
                        }
                    }
                }

                otherPersonMangaList.lists.forEach {
                    it.entries.forEach { entry ->
                        if (entry.media.getId() != 0 &&
                            entry.score != 0.0 &&
                            !otherPersonMangaMediaIdToScoreMap.containsKey(entry.media.getId())
                        ) {
                            otherPersonMangaMediaIdToScoreMap[entry.media.getId()] = entry.score
                        }
                    }
                }

                return@zip otherPersonAnimeMediaIdToScoreMap to otherPersonMangaMediaIdToScoreMap
            }
                .applyScheduler()
                .flatMap { (otherPersonAnimeMediaIdToScoreMap, otherPersonMangaMediaIdToScoreMap) ->
                    Observable.zip(
                        mediaListRepository.getMediaListCollection(Source.CACHE, viewer, MediaType.ANIME),
                        mediaListRepository.getMediaListCollection(Source.CACHE, viewer, MediaType.MANGA)
                    ) { viewerAnimeList, viewerMangaList ->
                        val viewerAnimeMediaIdToScoreMap = HashMap<Int, Double>()
                        val viewerMangaMediaIdToScoreMap = HashMap<Int, Double>()

                        val otherPersonAnimeScoreList = ArrayList<Double>()
                        val viewerAnimeScoreList = ArrayList<Double>()

                        val otherPersonMangaScoreList = ArrayList<Double>()
                        val viewerMangaScoreList = ArrayList<Double>()

                        viewerAnimeList.lists.forEach {
                            it.entries.forEach { entry ->
                                if (entry.media.getId() != 0 &&
                                    entry.score != 0.0 &&
                                    otherPersonAnimeMediaIdToScoreMap.containsKey(entry.media.getId()) &&
                                    !viewerAnimeMediaIdToScoreMap.containsKey(entry.media.getId())
                                ) {
                                    viewerAnimeMediaIdToScoreMap[entry.media.getId()] = entry.score
                                    otherPersonAnimeScoreList.add(otherPersonAnimeMediaIdToScoreMap[entry.media.getId()]!!)
                                    viewerAnimeScoreList.add(entry.score)
                                }
                            }
                        }

                        viewerMangaList.lists.forEach {
                            it.entries.forEach { entry ->
                                if (entry.media.getId() != 0 &&
                                    entry.score != 0.0 &&
                                    otherPersonMangaMediaIdToScoreMap.containsKey(entry.media.getId()) &&
                                    !viewerMangaMediaIdToScoreMap.containsKey(entry.media.getId())
                                ) {
                                    viewerMangaMediaIdToScoreMap[entry.media.getId()] = entry.score
                                    otherPersonMangaScoreList.add(otherPersonMangaMediaIdToScoreMap[entry.media.getId()]!!)
                                    viewerMangaScoreList.add(entry.score)
                                }
                            }
                        }

                        return@zip Pair(
                            Affinity(calculatePearsonCorrelation(otherPersonAnimeScoreList, viewerAnimeScoreList), viewerAnimeScoreList.size),
                            Affinity(calculatePearsonCorrelation(otherPersonMangaScoreList, viewerMangaScoreList), viewerAnimeScoreList.size)
                        )
                    }
                }
                .subscribe(
                    {
                        val currentProfileItemList = ArrayList(_profileItemList.value ?: listOf())
                        val affinityIndex = currentProfileItemList.indexOfFirst { it.viewType == ProfileItem.VIEW_TYPE_AFFINITY }
                        if (affinityIndex == -1)
                            return@subscribe
                        currentProfileItemList[affinityIndex].affinity = Pair(
                            it.first.copy(status = if (it.first.value != null) Affinity.AFFINITY_STATUS_COMPLETED else Affinity.AFFINITY_STATUS_FAILED),
                            it.second.copy(status = if (it.second.value != null) Affinity.AFFINITY_STATUS_COMPLETED else Affinity.AFFINITY_STATUS_FAILED)
                        )
                        _profileItemList.onNext(currentProfileItemList)
                    },
                    {
                        // do nothing
                        val currentProfileItemList = ArrayList(_profileItemList.value ?: listOf())
                        val affinityIndex = currentProfileItemList.indexOfFirst { it.viewType == ProfileItem.VIEW_TYPE_AFFINITY }
                        if (affinityIndex == -1)
                            return@subscribe
                        currentProfileItemList[affinityIndex].affinity = Pair(Affinity(status = Affinity.AFFINITY_STATUS_FAILED), Affinity(status = Affinity.AFFINITY_STATUS_FAILED))
                        _profileItemList.onNext(currentProfileItemList)
                    }
                )
        )
    }

    private fun calculatePearsonCorrelation(x: ArrayList<Double>, y: ArrayList<Double>): Double? {
        if (x.size < AFFINITY_MIN_THRESHOLD)
            return null

        val mx = x.average()
        val my = y.average()

        val xm = x.map { it - mx }
        val ym = y.map { it - my }

        val sx = xm.map { it.pow(2) }
        val sy = ym.map { it.pow(2) }

        val num = xm.zip(ym).fold(0.0, { acc, pair -> acc + pair.first * pair.second })
        val den = sqrt(sx.sum() * sy.sum())

        return if (den == 0.0) 0.0 else num / den * 100
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

        val totalCount = statistics.statuses.filter { it.status != MediaListStatus.PLANNING }.sumOf { it.count }.toDouble()

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
            }.sumOf { it.count }

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

    private fun setFollowButtonText() {
        _followButtonText.onNext(
            if (user.isFollowing && user.isFollower)
                R.string.mutual
            else if (user.isFollowing && !user.isFollower)
                R.string.following
            else if (!user.isFollowing && user.isFollower)
                R.string.follows_you
            else
                R.string.follow
        )
    }

    companion object {
        private const val TENDENCY_FAVORITES_COUNT = 3
        private const val TENDENCY_FAVORITES_SEPARATOR = "/"
        private const val TENDENCY_MINIMUM_COMPLETED = 20

        private const val FAVORITE_LIMIT = 9

        private const val AFFINITY_MIN_THRESHOLD = 10
    }
}