package com.zen.alchan.ui.settings.app

import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.data.repository.AuthenticationRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.helper.enums.*
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.sendMessage
import com.zen.alchan.helper.pojo.AppThemeItem
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.selects.select
import java.util.*
import kotlin.collections.ArrayList

class AppSettingsViewModel(
    private val userRepository: UserRepository,
    private val authenticationRepository: AuthenticationRepository
) : BaseViewModel() {

    private val appThemeSubject = BehaviorSubject.createDefault(AppTheme.DEFAULT_THEME_YELLOW)
    private val useCircularAvatarForProfileSubject = BehaviorSubject.createDefault(true)
    private val showRecentReviewsAtHomeSubject = BehaviorSubject.createDefault(true)
    private val allAnimeListPositionSubject = BehaviorSubject.createDefault(0)
    private val allMangaListPositionSubject = BehaviorSubject.createDefault(0)
    private val useRelativeDateForNextAiringEpisodeSubject = BehaviorSubject.createDefault(false)
    private val characterNamingSubject = BehaviorSubject.createDefault(CharacterNaming.FOLLOW_ANILIST)
    private val staffNamingSubject = BehaviorSubject.createDefault(StaffNaming.FOLLOW_ANILIST)
    private val japaneseMediaNamingSubject = BehaviorSubject.createDefault(MediaNaming.FOLLOW_ANILIST)
    private val koreanMediaNamingSubject = BehaviorSubject.createDefault(MediaNaming.FOLLOW_ANILIST)
    private val chineseMediaNamingSubject = BehaviorSubject.createDefault(MediaNaming.FOLLOW_ANILIST)
    private val taiwaneseMediaNamingSubject = BehaviorSubject.createDefault(MediaNaming.FOLLOW_ANILIST)
    private val sendAiringPushNotificationsSubject = BehaviorSubject.createDefault(true)
    private val sendActivityPushNotificationsSubject = BehaviorSubject.createDefault(true)
    private val sendForumPushNotificationsSubject = BehaviorSubject.createDefault(true)
    private val sendFollowsPushNotificationsSubject = BehaviorSubject.createDefault(true)
    private val sendRelationsPushNotificationsSubject = BehaviorSubject.createDefault(true)
    private val mergePushNotificationsSubject = BehaviorSubject.createDefault(false)
    private val showPushNotificationsIntervalSubject = BehaviorSubject.createDefault(1)
    private val useHighestQualityImageSubject = BehaviorSubject.createDefault(false)
    private val enableSocialFeatureSubject = BehaviorSubject.createDefault(true)
    private val showBioAutomaticallySubject = BehaviorSubject.createDefault(true)
    private val showStatsChartAutomaticallySubject = BehaviorSubject.createDefault(true)

    private val appThemeItemsSubject = PublishSubject.create<List<AppThemeItem>>()
    private val allAnimeListItemsSubject = PublishSubject.create<List<String>>()
    private val allMangaListItemsSubject = PublishSubject.create<List<String>>()
    private val characterNamingsSubject = PublishSubject.create<List<CharacterNaming>>()
    private val staffNamingsSubject = PublishSubject.create<List<StaffNaming>>()
    private val mediaNamingsSubject = PublishSubject.create<Pair<List<MediaNaming>, Country>>()
    private val pushNotificationsIntervalsSubject = PublishSubject.create<List<Int>>()

    val appTheme: Observable<AppTheme>
        get() = appThemeSubject

    val useCircularAvatarForProfile: Observable<Boolean>
        get() = useCircularAvatarForProfileSubject

    val showRecentReviewsAtHome: Observable<Boolean>
        get() = showRecentReviewsAtHomeSubject

    val allAnimeListPosition: Observable<Int>
        get() = allAnimeListPositionSubject

    val allMangaListPosition: Observable<Int>
        get() = allMangaListPositionSubject

    val useRelativeDateForNextAiringEpisode: Observable<Boolean>
        get() = useRelativeDateForNextAiringEpisodeSubject

    val characterNaming: Observable<CharacterNaming>
        get() = characterNamingSubject

    val staffNaming: Observable<StaffNaming>
        get() = staffNamingSubject

    val japaneseMediaNaming: Observable<MediaNaming>
        get() = japaneseMediaNamingSubject

    val koreanMediaNaming: Observable<MediaNaming>
        get() = koreanMediaNamingSubject

    val chineseMediaNaming: Observable<MediaNaming>
        get() = chineseMediaNamingSubject

    val taiwaneseMediaNaming: Observable<MediaNaming>
        get() = taiwaneseMediaNamingSubject

    val sendAiringPushNotifications: Observable<Boolean>
        get() = sendAiringPushNotificationsSubject

    val sendActivityPushNotifications: Observable<Boolean>
        get() = sendActivityPushNotificationsSubject

    val sendForumPushNotifications: Observable<Boolean>
        get() = sendForumPushNotificationsSubject

    val sendFollowsPushNotifications: Observable<Boolean>
        get() = sendFollowsPushNotificationsSubject

    val sendRelationsPushNotifications: Observable<Boolean>
        get() = sendRelationsPushNotificationsSubject

    val mergePushNotifications: Observable<Boolean>
        get() = mergePushNotificationsSubject

    val showPushNotificationsInterval: Observable<Int>
        get() = showPushNotificationsIntervalSubject

    val useHighestQualityImage: Observable<Boolean>
        get() = useHighestQualityImageSubject

    val enableSocialFeature: Observable<Boolean>
        get() = enableSocialFeatureSubject

    val showBioAutomatically: Observable<Boolean>
        get() = showBioAutomaticallySubject

    val showStatsChartAutomatically: Observable<Boolean>
        get() = showStatsChartAutomaticallySubject

    val appThemeItems: Observable<List<AppThemeItem>>
        get() = appThemeItemsSubject

    val allAnimeListItems: Observable<List<String>>
        get() = allAnimeListItemsSubject

    val allMangaListItems: Observable<List<String>>
        get() = allMangaListItemsSubject

    val characterNamings: Observable<List<CharacterNaming>>
        get() = characterNamingsSubject

    val staffNamings: Observable<List<StaffNaming>>
        get() = staffNamingsSubject

    val mediaNamings: Observable<Pair<List<MediaNaming>, Country>>
        get() = mediaNamingsSubject

    val pushNotificationsIntervals: Observable<List<Int>>
        get() = pushNotificationsIntervalsSubject

    private var viewer: User? = null
    private var currentAppSetting: AppSetting? = null

    override fun loadData() {
        disposables.add(
            authenticationRepository.getViewerDataFromCache()
                .applyScheduler()
                .subscribe {
                    viewer = it
                }
        )

        getAppSetting()
    }

    fun saveAppSettings() {
        userRepository.setAppSetting(currentAppSetting)
    }

    fun resetAppSettings() {
        userRepository.setAppSetting(null)
    }

    fun updateAppTheme(newAppTheme: AppTheme) {
        currentAppSetting?.appTheme = newAppTheme
        appThemeSubject.onNext(newAppTheme)
    }

    fun updateUseCircularAvatarForProfile(shouldUseCircularAvatarForProfile: Boolean) {
        currentAppSetting?.useCircularAvatarForProfile = shouldUseCircularAvatarForProfile
        useCircularAvatarForProfileSubject.onNext(shouldUseCircularAvatarForProfile)
    }

    fun updateShowRecentReviewsAtHome(shouldShowRecentReviewsAtHome: Boolean) {
        currentAppSetting?.showRecentReviewsAtHome = shouldShowRecentReviewsAtHome
        showRecentReviewsAtHomeSubject.onNext(shouldShowRecentReviewsAtHome)
    }

    fun updateAllAnimeListPosition(newPosition: Int) {
        currentAppSetting?.allAnimeListPosition = newPosition
        allAnimeListPositionSubject.onNext(newPosition + 1)
    }

    fun updateAllMangaListPosition(newPosition: Int) {
        currentAppSetting?.allMangaListPosition = newPosition
        allMangaListPositionSubject.onNext(newPosition + 1)
    }

    fun updateUseRelativeDateForNextAiringEpisode(shouldUseRelativeDateForNextAiringEpisode: Boolean) {
        currentAppSetting?.useRelativeDateForNextAiringEpisode = shouldUseRelativeDateForNextAiringEpisode
        useRelativeDateForNextAiringEpisodeSubject.onNext(shouldUseRelativeDateForNextAiringEpisode)
    }

    fun updateCharacterNaming(newCharacterNaming: CharacterNaming) {
        currentAppSetting?.characterNaming = newCharacterNaming
        characterNamingSubject.onNext(newCharacterNaming)
    }

    fun updateStaffNaming(newStaffNaming: StaffNaming) {
        currentAppSetting?.staffNaming = newStaffNaming
        staffNamingSubject.onNext(newStaffNaming)
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
        japaneseMediaNamingSubject.onNext(newMediaNaming)
    }

    private fun updateKoreanMediaNaming(newMediaNaming: MediaNaming) {
        currentAppSetting?.koreanMediaNaming = newMediaNaming
        koreanMediaNamingSubject.onNext(newMediaNaming)
    }

    private fun updateChineseMediaNaming(newMediaNaming: MediaNaming) {
        currentAppSetting?.chineseMediaNaming = newMediaNaming
        chineseMediaNamingSubject.onNext(newMediaNaming)
    }

    private fun updateTaiwaneseMediaNaming(newMediaNaming: MediaNaming) {
        currentAppSetting?.taiwaneseMediaNaming = newMediaNaming
        taiwaneseMediaNamingSubject.onNext(newMediaNaming)
    }

    fun updateSendAiringPushNotifications(shouldSendAiringPushNotifications: Boolean) {
        currentAppSetting?.sendAiringPushNotifications = shouldSendAiringPushNotifications
        sendAiringPushNotificationsSubject.onNext(shouldSendAiringPushNotifications)
    }

    fun updateSendActivityPushNotifications(shouldSendActivityPushNotifications: Boolean) {
        currentAppSetting?.sendActivityPushNotifications = shouldSendActivityPushNotifications
        sendActivityPushNotificationsSubject.onNext(shouldSendActivityPushNotifications)
    }

    fun updateSendForumPushNotifications(shouldSendForumPushNotifications: Boolean) {
        currentAppSetting?.sendForumPushNotifications = shouldSendForumPushNotifications
        sendForumPushNotificationsSubject.onNext(shouldSendForumPushNotifications)
    }

    fun updateSendFollowsPushNotifications(shouldSendFollowsPushNotifications: Boolean) {
        currentAppSetting?.sendFollowsPushNotifications = shouldSendFollowsPushNotifications
        sendFollowsPushNotificationsSubject.onNext(shouldSendFollowsPushNotifications)
    }

    fun updateSendRelationsPushNotifications(shouldSendRelationsPushNotifications: Boolean) {
        currentAppSetting?.sendRelationsPushNotifications = shouldSendRelationsPushNotifications
        sendRelationsPushNotificationsSubject.onNext(shouldSendRelationsPushNotifications)
    }

    fun updateMergePushNotifications(shouldMergePushNotifications: Boolean) {
        currentAppSetting?.mergePushNotifications = shouldMergePushNotifications
        mergePushNotificationsSubject.onNext(shouldMergePushNotifications)
    }

    fun updateShowPushNotificationsInterval(newInterval: Int) {
        currentAppSetting?.showPushNotificationsInterval = newInterval
        showPushNotificationsIntervalSubject.onNext(newInterval)
    }

    fun updateUseHighestQualityImage(shouldUseHighestQualityImage: Boolean) {
        currentAppSetting?.useHighestQualityImage = shouldUseHighestQualityImage
        useHighestQualityImageSubject.onNext(shouldUseHighestQualityImage)
    }

    fun updateEnableSocialFeature(shouldEnableSocialFeature: Boolean) {
        currentAppSetting?.enableSocialFeature = shouldEnableSocialFeature
        enableSocialFeatureSubject.onNext(shouldEnableSocialFeature)
    }

    fun updateShowBioAutomatically(shouldShowBioAutomatically: Boolean) {
        currentAppSetting?.showBioAutomatically = shouldShowBioAutomatically
        showBioAutomaticallySubject.onNext(shouldShowBioAutomatically)
    }

    fun updateShowStatsChartAutomatically(shouldShowStatsChartAutomatically: Boolean) {
        currentAppSetting?.showStatsChartAutomatically = shouldShowStatsChartAutomatically
        showStatsChartAutomaticallySubject.onNext(shouldShowStatsChartAutomatically)
    }

    private fun getAppSetting() {
        disposables.add(
            userRepository.getAppSetting()
                .applyScheduler()
                .subscribe {
                    currentAppSetting = it

                    updateAppTheme(it.appTheme)

                    updateUseCircularAvatarForProfile(it.useCircularAvatarForProfile)
                    updateShowRecentReviewsAtHome(it.showRecentReviewsAtHome)

                    updateAllAnimeListPosition(it.allAnimeListPosition)
                    updateAllMangaListPosition(it.allMangaListPosition)
                    updateUseRelativeDateForNextAiringEpisode(it.useRelativeDateForNextAiringEpisode)

                    updateCharacterNaming(it.characterNaming)
                    updateStaffNaming(it.staffNaming)
                    updateJapaneseMediaNaming(it.japaneseMediaNaming)
                    updateKoreanMediaNaming(it.koreanMediaNaming)
                    updateChineseMediaNaming(it.chineseMediaNaming)
                    updateTaiwaneseMediaNaming(it.taiwaneseMediaNaming)

                    updateSendAiringPushNotifications(it.sendAiringPushNotifications)
                    updateSendActivityPushNotifications(it.sendActivityPushNotifications)
                    updateSendForumPushNotifications(it.sendForumPushNotifications)
                    updateSendFollowsPushNotifications(it.sendFollowsPushNotifications)
                    updateSendRelationsPushNotifications(it.sendRelationsPushNotifications)
                    updateMergePushNotifications(it.mergePushNotifications)
                    updateShowPushNotificationsInterval(it.showPushNotificationsInterval)

                    updateUseHighestQualityImage(it.useHighestQualityImage)
                    updateEnableSocialFeature(it.enableSocialFeature)
                    updateShowBioAutomatically(it.showBioAutomatically)
                    updateShowStatsChartAutomatically(it.showStatsChartAutomatically)
                }
        )
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
        appThemeItemsSubject.onNext(items)
    }

    fun getAllAnimeListNumbers() {
        val allAnimeListItems = ArrayList<String>()
        allAnimeListItems.add("")
        allAnimeListItems.addAll(viewer?.mediaListOptions?.animeList?.sectionOrder ?: listOf())
        allAnimeListItemsSubject.onNext(allAnimeListItems)
    }

    fun getAllMangaListNumbers() {
        val allMangaListItems = ArrayList<String>()
        allMangaListItems.add("")
        allMangaListItems.addAll(viewer?.mediaListOptions?.mangaList?.sectionOrder ?: listOf())
        allMangaListItemsSubject.onNext(allMangaListItems)
    }

    fun getCharacterNamings() {
        characterNamingsSubject.onNext(CharacterNaming.values().toList())
    }

    fun getStaffNamings() {
        staffNamingsSubject.onNext(StaffNaming.values().toList())
    }

    fun getMediaNamings(country: Country) {
        mediaNamingsSubject.onNext(MediaNaming.values().toList() to country)
    }

    fun getPushNotificationsIntervals() {
        pushNotificationsIntervalsSubject.onNext((1..24).toList())
    }
}