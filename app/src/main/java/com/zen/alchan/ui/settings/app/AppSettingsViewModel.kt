package com.zen.alchan.ui.settings.app

import androidx.lifecycle.viewModelScope
import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.helper.enums.*
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.pojo.AppThemeItem
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.*
import kotlin.collections.ArrayList

class AppSettingsViewModel(
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val _appTheme = BehaviorSubject.createDefault(AppTheme.DEFAULT_THEME_YELLOW)
    val appTheme: Observable<AppTheme>
        get() = _appTheme

    private val _useCircularAvatarForProfile = BehaviorSubject.createDefault(true)
    val useCircularAvatarForProfile: Observable<Boolean>
        get() = _useCircularAvatarForProfile

    private val _showRecentReviewsAtHome = BehaviorSubject.createDefault(true)
    val showRecentReviewsAtHome: Observable<Boolean>
        get() = _showRecentReviewsAtHome

    private val _allAnimeListPosition = BehaviorSubject.createDefault(0)
    val allAnimeListPosition: Observable<Int>
        get() = _allAnimeListPosition

    private val _allMangaListPosition = BehaviorSubject.createDefault(0)
    val allMangaListPosition: Observable<Int>
        get() = _allMangaListPosition

    private val _useRelativeDateForNextAiringEpisode = BehaviorSubject.createDefault(false)
    val useRelativeDateForNextAiringEpisode: Observable<Boolean>
        get() = _useRelativeDateForNextAiringEpisode

    private val _characterNaming = BehaviorSubject.createDefault(CharacterNaming.FOLLOW_ANILIST)
    val characterNaming: Observable<CharacterNaming>
        get() = _characterNaming

    private val _staffNaming = BehaviorSubject.createDefault(StaffNaming.FOLLOW_ANILIST)
    val staffNaming: Observable<StaffNaming>
        get() = _staffNaming

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

    private val _appSettingsSaved = PublishSubject.create<Unit>()
    val appSettingsSaved: Observable<Unit>
        get() = _appSettingsSaved

    private val _appThemeItems = PublishSubject.create<List<AppThemeItem>>()
    val appThemeItems: Observable<List<AppThemeItem>>
        get() = _appThemeItems

    private val _allAnimeListItems = PublishSubject.create<List<String>>()
    val allAnimeListItems: Observable<List<String>>
        get() = _allAnimeListItems

    private val _allMangaListItems = PublishSubject.create<List<String>>()
    val allMangaListItems: Observable<List<String>>
        get() = _allMangaListItems

    private val _characterNamings = PublishSubject.create<List<CharacterNaming>>()
    val characterNamings: Observable<List<CharacterNaming>>
        get() = _characterNamings

    private val _staffNamings = PublishSubject.create<List<StaffNaming>>()
    val staffNamings: Observable<List<StaffNaming>>
        get() = _staffNamings

    private val _mediaNamings = PublishSubject.create<Pair<List<MediaNaming>, Country>>()
    val mediaNamings: Observable<Pair<List<MediaNaming>, Country>>
        get() = _mediaNamings

    private val _pushNotificationsIntervals = PublishSubject.create<List<Int>>()
    val pushNotificationsIntervals: Observable<List<Int>>
        get() = _pushNotificationsIntervals

    private var viewer: User? = null
    private var currentAppSetting: AppSetting? = null

    override fun loadData() {
        disposables.add(
            userRepository.viewerAndAppSetting
                .applyScheduler()
                .subscribe { (user, appSetting) ->
                    viewer = user
                    currentAppSetting = appSetting

                    updateAppTheme(appSetting.appTheme)

                    updateUseCircularAvatarForProfile(appSetting.useCircularAvatarForProfile)
                    updateShowRecentReviewsAtHome(appSetting.showRecentReviewsAtHome)

                    updateAllAnimeListPosition(appSetting.allAnimeListPosition)
                    updateAllMangaListPosition(appSetting.allMangaListPosition)
                    updateUseRelativeDateForNextAiringEpisode(appSetting.useRelativeDateForNextAiringEpisode)

                    updateCharacterNaming(appSetting.characterNaming)
                    updateStaffNaming(appSetting.staffNaming)
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
                }
        )
    }

    fun saveAppSettings() {
        userRepository.setAppSetting(currentAppSetting)
        _appSettingsSaved.onNext(Unit)
    }

    fun resetAppSettings() {
        userRepository.setAppSetting(null)
        _appSettingsSaved.onNext(Unit)
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

    fun updateAllAnimeListPosition(newPosition: Int) {
        currentAppSetting?.allAnimeListPosition = newPosition
        _allAnimeListPosition.onNext(newPosition + 1)
    }

    fun updateAllMangaListPosition(newPosition: Int) {
        currentAppSetting?.allMangaListPosition = newPosition
        _allMangaListPosition.onNext(newPosition + 1)
    }

    fun updateUseRelativeDateForNextAiringEpisode(shouldUseRelativeDateForNextAiringEpisode: Boolean) {
        currentAppSetting?.useRelativeDateForNextAiringEpisode = shouldUseRelativeDateForNextAiringEpisode
        _useRelativeDateForNextAiringEpisode.onNext(shouldUseRelativeDateForNextAiringEpisode)
    }

    fun updateCharacterNaming(newCharacterNaming: CharacterNaming) {
        currentAppSetting?.characterNaming = newCharacterNaming
        _characterNaming.onNext(newCharacterNaming)
    }

    fun updateStaffNaming(newStaffNaming: StaffNaming) {
        currentAppSetting?.staffNaming = newStaffNaming
        _staffNaming.onNext(newStaffNaming)
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

    fun getAppThemeItems() {
        val items = ArrayList<AppThemeItem>()
        var currentHeader = ""
        AppTheme.values().forEach {
            val splitAppThemeName = it.name.split("_")
            val appThemeName = splitAppThemeName
                .take(splitAppThemeName.size - 1)
                .joinToString(" ") {
                    it.toLowerCase(Locale.getDefault()).capitalize(Locale.getDefault())
                }
            if (currentHeader != appThemeName) {
                currentHeader = appThemeName
                items.add(AppThemeItem(header = currentHeader))
            }
            items.add(AppThemeItem(appTheme = it))
        }
        _appThemeItems.onNext(items)
    }

    fun getAllAnimeListNumbers() {
        val allAnimeListItems = ArrayList<String>()
        allAnimeListItems.add("")
        allAnimeListItems.addAll(viewer?.mediaListOptions?.animeList?.sectionOrder ?: listOf())
        _allAnimeListItems.onNext(allAnimeListItems)
    }

    fun getAllMangaListNumbers() {
        val allMangaListItems = ArrayList<String>()
        allMangaListItems.add("")
        allMangaListItems.addAll(viewer?.mediaListOptions?.mangaList?.sectionOrder ?: listOf())
        _allMangaListItems.onNext(allMangaListItems)
    }

    fun getCharacterNamings() {
        _characterNamings.onNext(CharacterNaming.values().toList())
    }

    fun getStaffNamings() {
        _staffNamings.onNext(StaffNaming.values().toList())
    }

    fun getMediaNamings(country: Country) {
        _mediaNamings.onNext(MediaNaming.values().toList() to country)
    }

    fun getPushNotificationsIntervals() {
        _pushNotificationsIntervals.onNext((1..24).toList())
    }
}