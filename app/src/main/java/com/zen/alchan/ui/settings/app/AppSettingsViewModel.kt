package com.zen.alchan.ui.settings.app

import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.helper.enums.*
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.pojo.AppThemeItem
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import com.zen.alchan.helper.enums.MediaType
import java.util.*
import kotlin.collections.ArrayList

class AppSettingsViewModel(
    private val userRepository: UserRepository
) : BaseViewModel<Unit>() {

    private val _appTheme = BehaviorSubject.createDefault(AppTheme.DEFAULT_THEME_YELLOW)
    val appTheme: Observable<AppTheme>
        get() = _appTheme

    private val _useCircularAvatarForProfile = BehaviorSubject.createDefault(true)
    val useCircularAvatarForProfile: Observable<Boolean>
        get() = _useCircularAvatarForProfile

    private val _showRecentReviewsAtHome = BehaviorSubject.createDefault(true)
    val showRecentReviewsAtHome: Observable<Boolean>
        get() = _showRecentReviewsAtHome

    private val _isAllAnimeListPositionAtTop = BehaviorSubject.createDefault(true)
    val isAllAnimeListPositionAtTop: Observable<Boolean>
        get() = _isAllAnimeListPositionAtTop

    private val _isAllMangaListPositionAtTop = BehaviorSubject.createDefault(true)
    val isAllMangaListPositionAtTop: Observable<Boolean>
        get() = _isAllMangaListPositionAtTop

    private val _useRelativeDateForNextAiringEpisode = BehaviorSubject.createDefault(false)
    val useRelativeDateForNextAiringEpisode: Observable<Boolean>
        get() = _useRelativeDateForNextAiringEpisode

    private val _japaneseStaffNaming = BehaviorSubject.createDefault(StaffNaming.FOLLOW_ANILIST)
    val japaneseStaffNaming: Observable<StaffNaming>
        get() = _japaneseStaffNaming

    private val _japaneseMediaNaming = BehaviorSubject.createDefault(MediaNaming.FOLLOW_ANILIST)
    val japaneseMediaNaming: Observable<MediaNaming>
        get() = _japaneseMediaNaming

    private val _koreanMediaNaming = BehaviorSubject.createDefault(MediaNaming.FOLLOW_ANILIST)
    val koreanMediaNaming: Observable<MediaNaming>
        get() = _koreanMediaNaming

    private val _chineseMediaNaming = BehaviorSubject.createDefault(MediaNaming.FOLLOW_ANILIST)
    val chineseMediaNaming: Observable<MediaNaming>
        get() = _chineseMediaNaming

    private val _taiwaneseMediaNaming = BehaviorSubject.createDefault(MediaNaming.FOLLOW_ANILIST)
    val taiwaneseMediaNaming: Observable<MediaNaming>
        get() = _taiwaneseMediaNaming

    private val _sendAiringPushNotifications = BehaviorSubject.createDefault(true)
    val sendAiringPushNotifications: Observable<Boolean>
        get() = _sendAiringPushNotifications

    private val _sendActivityPushNotifications = BehaviorSubject.createDefault(true)
    val sendActivityPushNotifications: Observable<Boolean>
        get() = _sendActivityPushNotifications

    private val _sendForumPushNotifications = BehaviorSubject.createDefault(true)
    val sendForumPushNotifications: Observable<Boolean>
        get() = _sendForumPushNotifications

    private val _sendFollowsPushNotifications = BehaviorSubject.createDefault(true)
    val sendFollowsPushNotifications: Observable<Boolean>
        get() = _sendFollowsPushNotifications

    private val _sendRelationsPushNotifications = BehaviorSubject.createDefault(true)
    val sendRelationsPushNotifications: Observable<Boolean>
        get() = _sendRelationsPushNotifications

    private val _mergePushNotifications = BehaviorSubject.createDefault(false)
    val mergePushNotifications: Observable<Boolean>
        get() = _mergePushNotifications

    private val _showPushNotificationsInterval = BehaviorSubject.createDefault(1)
    val showPushNotificationsInterval: Observable<Int>
        get() = _showPushNotificationsInterval

    private val _useHighestQualityImage = BehaviorSubject.createDefault(false)
    val useHighestQualityImage: Observable<Boolean>
        get() = _useHighestQualityImage

    private val _enableSocialFeature = BehaviorSubject.createDefault(true)
    val enableSocialFeature: Observable<Boolean>
        get() = _enableSocialFeature

    private val _showBioAutomatically = BehaviorSubject.createDefault(true)
    val showBioAutomatically: Observable<Boolean>
        get() = _showBioAutomatically

    private val _showStatsChartAutomatically = BehaviorSubject.createDefault(true)
    val showStatsChartAutomatically: Observable<Boolean>
        get() = _showStatsChartAutomatically

    private val _appThemeItems = PublishSubject.create<List<AppThemeItem>>()
    val appThemeItems: Observable<List<AppThemeItem>>
        get() = _appThemeItems

    private val _allAnimeListPositionItems = PublishSubject.create<List<ListItem<Boolean>>>()
    val allAnimeListPositionItems: Observable<List<ListItem<Boolean>>>
        get() = _allAnimeListPositionItems

    private val _allMangaListPositionItems = PublishSubject.create<List<ListItem<Boolean>>>()
    val allMangaListPositionItems: Observable<List<ListItem<Boolean>>>
        get() = _allMangaListPositionItems

    private val _staffNamingItems = PublishSubject.create<List<ListItem<StaffNaming>>>()
    val staffNamingItems: Observable<List<ListItem<StaffNaming>>>
        get() = _staffNamingItems

    private val _mediaNamingItems = PublishSubject.create<Pair<List<ListItem<MediaNaming>>, Country>>()
    val mediaNamingItems: Observable<Pair<List<ListItem<MediaNaming>>, Country>>
        get() = _mediaNamingItems

    private val _pushNotificationsIntervalItems = PublishSubject.create<List<ListItem<Int>>>()
    val pushNotificationsIntervalItems: Observable<List<ListItem<Int>>>
        get() = _pushNotificationsIntervalItems

    private var viewer: User? = null
    private var currentAppSetting: AppSetting? = null

    override fun loadData(param: Unit) {
        loadOnce {
            disposables.add(
                userRepository.getViewer(Source.CACHE)
                    .zipWith(userRepository.getAppSetting()) { user, appSetting ->
                        return@zipWith user to appSetting
                    }
                    .applyScheduler()
                    .subscribe { (user, appSetting) ->
                        viewer = user
                        currentAppSetting = appSetting

                        updateAppTheme(appSetting.appTheme)

                        updateUseCircularAvatarForProfile(appSetting.useCircularAvatarForProfile)
                        updateShowRecentReviewsAtHome(appSetting.showRecentReviewsAtHome)

                        updateIsAllAnimeListPositionAtTop(appSetting.isAllAnimeListPositionAtTop)
                        updateIsAllMangaListPositionAtTop(appSetting.isAllMangaListPositionAtTop)
                        updateUseRelativeDateForNextAiringEpisode(appSetting.useRelativeDateForNextAiringEpisode)

                        updateJapaneseStaffNaming(appSetting.japaneseStaffNaming)
                        updateJapaneseMediaNaming(appSetting.japaneseMediaNaming)
                        updateKoreanMediaNaming(appSetting.koreanMediaNaming)
                        updateChineseMediaNaming(appSetting.chineseMediaNaming)
                        updateTaiwaneseMediaNaming(appSetting.taiwaneseMediaNaming)

                        updateSendAiringPushNotifications(appSetting.sendAiringPushNotifications)
                        updateSendActivityPushNotifications(appSetting.sendActivityPushNotifications)
                        updateSendForumPushNotifications(appSetting.sendForumPushNotifications)
                        updateSendFollowsPushNotifications(appSetting.sendFollowsPushNotifications)
                        updateSendRelationsPushNotifications(appSetting.sendRelationsPushNotifications)
                        updateMergePushNotifications(appSetting.mergePushNotifications)
                        updateShowPushNotificationsInterval(appSetting.showPushNotificationsInterval)

                        updateUseHighestQualityImage(appSetting.useHighestQualityImage)
                        updateEnableSocialFeature(appSetting.enableSocialFeature)
                        updateShowBioAutomatically(appSetting.showBioAutomatically)
                        updateShowStatsChartAutomatically(appSetting.showStatsChartAutomatically)

                        state = State.LOADED
                    }
            )
        }
    }

    fun saveAppSettings() {
        disposables.add(
            userRepository.setAppSetting(currentAppSetting)
                .applyScheduler()
                .subscribe{
                    _success.onNext(R.string.settings_saved)
                }
        )
    }

    fun resetAppSettings() {
        disposables.add(
            userRepository.setAppSetting(null)
                .applyScheduler()
                .subscribe {
                    _success.onNext(R.string.settings_saved)
                }
        )
    }

    fun updateAppTheme(newAppTheme: AppTheme) {
        currentAppSetting?.appTheme = newAppTheme
        _appTheme.onNext(newAppTheme)
    }

    fun updateUseCircularAvatarForProfile(shouldUseCircularAvatarForProfile: Boolean) {
        currentAppSetting?.useCircularAvatarForProfile = shouldUseCircularAvatarForProfile
        _useCircularAvatarForProfile.onNext(shouldUseCircularAvatarForProfile)
    }

    fun updateShowRecentReviewsAtHome(shouldShowRecentReviewsAtHome: Boolean) {
        currentAppSetting?.showRecentReviewsAtHome = shouldShowRecentReviewsAtHome
        _showRecentReviewsAtHome.onNext(shouldShowRecentReviewsAtHome)
    }

    fun updateIsAllAnimeListPositionAtTop(isAtTop: Boolean) {
        currentAppSetting?.isAllAnimeListPositionAtTop = isAtTop
        _isAllAnimeListPositionAtTop.onNext(isAtTop)
    }

    fun updateIsAllMangaListPositionAtTop(isAtTop: Boolean) {
        currentAppSetting?.isAllMangaListPositionAtTop = isAtTop
        _isAllMangaListPositionAtTop.onNext(isAtTop)
    }

    fun updateUseRelativeDateForNextAiringEpisode(shouldUseRelativeDateForNextAiringEpisode: Boolean) {
        currentAppSetting?.useRelativeDateForNextAiringEpisode = shouldUseRelativeDateForNextAiringEpisode
        _useRelativeDateForNextAiringEpisode.onNext(shouldUseRelativeDateForNextAiringEpisode)
    }

    fun updateJapaneseStaffNaming(newJapaneseStaffNaming: StaffNaming) {
        currentAppSetting?.japaneseStaffNaming = newJapaneseStaffNaming
        _japaneseStaffNaming.onNext(newJapaneseStaffNaming)
    }

    fun updateMediaNaming(newMediaNaming: MediaNaming, country: Country) {
        when (country) {
            Country.JAPAN -> updateJapaneseMediaNaming(newMediaNaming)
            Country.SOUTH_KOREA -> updateKoreanMediaNaming(newMediaNaming)
            Country.CHINA -> updateChineseMediaNaming(newMediaNaming)
            Country.TAIWAN -> updateTaiwaneseMediaNaming(newMediaNaming)
        }
    }

    private fun updateJapaneseMediaNaming(newMediaNaming: MediaNaming) {
        currentAppSetting?.japaneseMediaNaming = newMediaNaming
        _japaneseMediaNaming.onNext(newMediaNaming)
    }

    private fun updateKoreanMediaNaming(newMediaNaming: MediaNaming) {
        currentAppSetting?.koreanMediaNaming = newMediaNaming
        _koreanMediaNaming.onNext(newMediaNaming)
    }

    private fun updateChineseMediaNaming(newMediaNaming: MediaNaming) {
        currentAppSetting?.chineseMediaNaming = newMediaNaming
        _chineseMediaNaming.onNext(newMediaNaming)
    }

    private fun updateTaiwaneseMediaNaming(newMediaNaming: MediaNaming) {
        currentAppSetting?.taiwaneseMediaNaming = newMediaNaming
        _taiwaneseMediaNaming.onNext(newMediaNaming)
    }

    fun updateSendAiringPushNotifications(shouldSendAiringPushNotifications: Boolean) {
        currentAppSetting?.sendAiringPushNotifications = shouldSendAiringPushNotifications
        _sendAiringPushNotifications.onNext(shouldSendAiringPushNotifications)
    }

    fun updateSendActivityPushNotifications(shouldSendActivityPushNotifications: Boolean) {
        currentAppSetting?.sendActivityPushNotifications = shouldSendActivityPushNotifications
        _sendActivityPushNotifications.onNext(shouldSendActivityPushNotifications)
    }

    fun updateSendForumPushNotifications(shouldSendForumPushNotifications: Boolean) {
        currentAppSetting?.sendForumPushNotifications = shouldSendForumPushNotifications
        _sendForumPushNotifications.onNext(shouldSendForumPushNotifications)
    }

    fun updateSendFollowsPushNotifications(shouldSendFollowsPushNotifications: Boolean) {
        currentAppSetting?.sendFollowsPushNotifications = shouldSendFollowsPushNotifications
        _sendFollowsPushNotifications.onNext(shouldSendFollowsPushNotifications)
    }

    fun updateSendRelationsPushNotifications(shouldSendRelationsPushNotifications: Boolean) {
        currentAppSetting?.sendRelationsPushNotifications = shouldSendRelationsPushNotifications
        _sendRelationsPushNotifications.onNext(shouldSendRelationsPushNotifications)
    }

    fun updateMergePushNotifications(shouldMergePushNotifications: Boolean) {
        currentAppSetting?.mergePushNotifications = shouldMergePushNotifications
        _mergePushNotifications.onNext(shouldMergePushNotifications)
    }

    fun updateShowPushNotificationsInterval(newInterval: Int) {
        currentAppSetting?.showPushNotificationsInterval = newInterval
        _showPushNotificationsInterval.onNext(newInterval)
    }

    fun updateUseHighestQualityImage(shouldUseHighestQualityImage: Boolean) {
        currentAppSetting?.useHighestQualityImage = shouldUseHighestQualityImage
        _useHighestQualityImage.onNext(shouldUseHighestQualityImage)
    }

    fun updateEnableSocialFeature(shouldEnableSocialFeature: Boolean) {
        currentAppSetting?.enableSocialFeature = shouldEnableSocialFeature
        _enableSocialFeature.onNext(shouldEnableSocialFeature)
    }

    fun updateShowBioAutomatically(shouldShowBioAutomatically: Boolean) {
        currentAppSetting?.showBioAutomatically = shouldShowBioAutomatically
        _showBioAutomatically.onNext(shouldShowBioAutomatically)
    }

    fun updateShowStatsChartAutomatically(shouldShowStatsChartAutomatically: Boolean) {
        currentAppSetting?.showStatsChartAutomatically = shouldShowStatsChartAutomatically
        _showStatsChartAutomatically.onNext(shouldShowStatsChartAutomatically)
    }

    fun loadAppThemeItems() {
        val items = ArrayList<AppThemeItem>()
        var currentHeader = ""
        AppTheme.values().forEach {
            val splitAppThemeName = it.name.split("_")
            val appThemeName = splitAppThemeName
                .take(splitAppThemeName.size - 1)
                .joinToString(" ") {
                    it.lowercase()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                }
            if (currentHeader != appThemeName) {
                currentHeader = appThemeName
                items.add(AppThemeItem(header = currentHeader))
            }
            items.add(AppThemeItem(appTheme = it))
        }
        _appThemeItems.onNext(items)
    }

    fun loadAllListPositionItems(mediaType: MediaType) {
        val items = ArrayList<ListItem<Boolean>>()
        items.add(ListItem(R.string.top_of_the_list, true))
        items.add(ListItem(R.string.bottom_of_the_list, false))
        when (mediaType) {
            MediaType.ANIME -> _allAnimeListPositionItems.onNext(items)
            MediaType.MANGA -> _allMangaListPositionItems.onNext(items)
        }
    }

    fun loadStaffNamingItems() {
        val items = ArrayList<ListItem<StaffNaming>>()
        items.add(ListItem(R.string.follow_anilist_setting, StaffNaming.FOLLOW_ANILIST))
        items.add(ListItem(R.string.use_staff_first_middle_last_name_format, StaffNaming.FIRST_MIDDLE_LAST))
        items.add(ListItem(R.string.use_staff_last_middle_first_name_format, StaffNaming.LAST_MIDDLE_FIRST))
        items.add(ListItem(R.string.native_name, StaffNaming.NATIVE))
        _staffNamingItems.onNext(items)
    }

    fun loadMediaNamingItems(country: Country) {
        val items = ArrayList<ListItem<MediaNaming>>()
        items.add(ListItem(R.string.follow_anilist_setting, MediaNaming.FOLLOW_ANILIST))
        items.add(ListItem(R.string.use_media_english_name_format, MediaNaming.ENGLISH))
        items.add(ListItem(R.string.use_media_romaji_name_format, MediaNaming.ROMAJI))
        items.add(ListItem(R.string.use_media_native_name_format, MediaNaming.NATIVE))
        _mediaNamingItems.onNext(items to country)
    }

    fun loadPushNotificationsIntervalItems() {
        val items = ArrayList<ListItem<Int>>()
        (1..24).forEach {
            if (it == 1) {
                items.add(ListItem("$it {0}", listOf(R.string.hour), it))
            } else {
                items.add(ListItem("$it {0}", listOf(R.string.hours), it))
            }
        }
        _pushNotificationsIntervalItems.onNext(items)
    }
}