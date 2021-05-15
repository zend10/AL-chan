package com.zen.alchan.ui.settings.app

import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.enums.AppTheme
import com.zen.alchan.helper.enums.CharacterNaming
import com.zen.alchan.helper.enums.MediaNaming
import com.zen.alchan.helper.enums.StaffNaming
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.sendMessage
import com.zen.alchan.helper.pojo.AppThemeItem
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.selects.select
import java.util.*
import kotlin.collections.ArrayList

class AppSettingsViewModel(private val userRepository: UserRepository) : BaseViewModel() {

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

    private val appThemeItemsSubject = BehaviorSubject.createDefault<List<AppThemeItem>>(listOf())

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

    val appThemeItems: Observable<List<AppThemeItem>>
        get() = appThemeItemsSubject

    private var currentAppSetting: AppSetting? = null

    override fun loadData() {
        getAppSetting()
        getAppThemeItems()
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
        allAnimeListPositionSubject.onNext(newPosition)
    }

    fun updateAllMangaListPosition(newPosition: Int) {
        currentAppSetting?.allMangaListPosition = newPosition
        allMangaListPositionSubject.onNext(newPosition)
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

    fun updateJapaneseMediaNaming(newMediaNaming: MediaNaming) {
        currentAppSetting?.japaneseMediaNaming = newMediaNaming
        japaneseMediaNamingSubject.onNext(newMediaNaming)
    }

    fun updateKoreanMediaNaming(newMediaNaming: MediaNaming) {
        currentAppSetting?.koreanMediaNaming = newMediaNaming
        koreanMediaNamingSubject.onNext(newMediaNaming)
    }

    fun updateChineseMediaNaming(newMediaNaming: MediaNaming) {
        currentAppSetting?.chineseMediaNaming = newMediaNaming
        chineseMediaNamingSubject.onNext(newMediaNaming)
    }

    fun updateTaiwaneseMediaNaming(newMediaNaming: MediaNaming) {
        currentAppSetting?.taiwaneseMediaNaming = newMediaNaming
        taiwaneseMediaNamingSubject.onNext(newMediaNaming)
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
                }
        )
    }

    private fun getAppThemeItems() {
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
}