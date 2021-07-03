package com.zen.alchan.ui.settings.anilist

import com.zen.alchan.R
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.data.response.anilist.UserOptions
import com.zen.alchan.helper.enums.ActivityMergeTime
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.enums.getString
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.sendMessage
import com.zen.alchan.helper.extensions.showUnit
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import type.UserStaffNameLanguage
import type.UserTitleLanguage

class AniListSettingsViewModel(private val userRepository: UserRepository) : BaseViewModel() {

    private val _titleLanguage = BehaviorSubject.createDefault(UserTitleLanguage.ROMAJI)
    val titleLanguage: Observable<UserTitleLanguage>
        get() = _titleLanguage

    private val _staffNameLanguage = BehaviorSubject.createDefault(UserStaffNameLanguage.ROMAJI_WESTERN)
    val staffNameLanguage: Observable<UserStaffNameLanguage>
        get() = _staffNameLanguage

    private val _activityMergeTime = BehaviorSubject.createDefault(ActivityMergeTime.NEVER)
    val activityMergeTime: Observable<ActivityMergeTime>
        get() = _activityMergeTime

    private val _displayAdultContent = BehaviorSubject.createDefault(false)
    val displayAdultContent: Observable<Boolean>
        get() = _displayAdultContent

    private val _airingNotifications = BehaviorSubject.createDefault(false)
    val airingNotifications: Observable<Boolean>
        get() = _airingNotifications

    private val _userTitleLanguageItems = PublishSubject.create<List<ListItem<UserTitleLanguage>>>()
    val userTitleLanguageItems: Observable<List<ListItem<UserTitleLanguage>>>
        get() = _userTitleLanguageItems

    private val _userStaffNameLanguageItems = PublishSubject.create<List<ListItem<UserStaffNameLanguage>>>()
    val userStaffNameLanguageItems: Observable<List<ListItem<UserStaffNameLanguage>>>
        get() = _userStaffNameLanguageItems

    private val _activityMergeTimeItems = PublishSubject.create<List<ListItem<ActivityMergeTime>>>()
    val activityMergeTimeItems: Observable<List<ListItem<ActivityMergeTime>>>
        get() = _activityMergeTimeItems

    private var viewer: User? = null
    private var currentAniListSetting: UserOptions? = null

    override fun loadData() {
        loadOnce {
            disposables.add(
                userRepository.getViewer(Source.CACHE)
                    .applyScheduler()
                    .subscribe {
                        viewer = it

                        val userOptions = it.options
                        currentAniListSetting = userOptions
                        updateTitleLanguage(userOptions.titleLanguage ?: UserTitleLanguage.ROMAJI)
                        updateStaffNameLanguage(userOptions.staffNameLanguage ?: UserStaffNameLanguage.ROMAJI_WESTERN)
                        updateActivityMergeTime(userOptions.activityMergeTime)
                        updateDisplayAdultContent(userOptions.displayAdultContent)
                        updateAiringNotifications(userOptions.airingNotifications)

                        state = State.LOADED
                    }
            )
        }
    }

    fun saveAniListSettings() {
        _loading.onNext(true)

        currentAniListSetting?.apply {
            disposables.add(
                userRepository.updateAniListSettings(
                    titleLanguage ?: UserTitleLanguage.ROMAJI,
                    staffNameLanguage ?: UserStaffNameLanguage.ROMAJI_WESTERN,
                    activityMergeTime,
                    displayAdultContent,
                    airingNotifications
                )
                    .applyScheduler()
                    .doFinally { _loading.onNext(false) }
                    .subscribe(
                        {
                            viewer = it
                            _success.onNext(R.string.settings_saved)
                        },
                        {
                            _error.onNext(it.sendMessage())
                        }
                    )
            )
        }
    }

    fun updateTitleLanguage(newTitleLanguage: UserTitleLanguage) {
        currentAniListSetting?.titleLanguage = newTitleLanguage
        _titleLanguage.onNext(newTitleLanguage)
    }

    fun updateStaffNameLanguage(newStaffNameLanguage: UserStaffNameLanguage) {
        currentAniListSetting?.staffNameLanguage = newStaffNameLanguage
        _staffNameLanguage.onNext(newStaffNameLanguage)
    }

    fun updateActivityMergeTime(newActivityMergeTime: Int) {
        currentAniListSetting?.activityMergeTime = newActivityMergeTime
        val selectedActivityMergeTime = ActivityMergeTime.values().toList().find {
            it.minute == newActivityMergeTime
        } ?: ActivityMergeTime.NEVER
        _activityMergeTime.onNext(selectedActivityMergeTime)
    }

    fun updateDisplayAdultContent(shouldDisplayAdultContent: Boolean) {
        currentAniListSetting?.displayAdultContent = shouldDisplayAdultContent
        _displayAdultContent.onNext(shouldDisplayAdultContent)
    }

    fun updateAiringNotifications(shouldReceiveAiringNotifications: Boolean) {
        currentAniListSetting?.airingNotifications = shouldReceiveAiringNotifications
        _airingNotifications.onNext(shouldReceiveAiringNotifications)
    }

    fun loadUserTitleLanguageItems() {
        val items = ArrayList<ListItem<UserTitleLanguage>>()
        items.add(ListItem(R.string.use_media_romaji_name_format, UserTitleLanguage.ROMAJI))
        items.add(ListItem(R.string.use_media_english_name_format, UserTitleLanguage.ENGLISH))
        items.add(ListItem(R.string.use_media_native_name_format, UserTitleLanguage.NATIVE))
        _userTitleLanguageItems.onNext(items)
    }

    fun loadUserStaffNameLanguageItems() {
        val items = ArrayList<ListItem<UserStaffNameLanguage>>()
        items.add(ListItem(R.string.use_staff_character_romaji_western_name_format, UserStaffNameLanguage.ROMAJI_WESTERN))
        items.add(ListItem(R.string.use_staff_character_romaji_name_format, UserStaffNameLanguage.ROMAJI))
        items.add(ListItem(R.string.use_staff_character_native_name_format, UserStaffNameLanguage.NATIVE))
        _userStaffNameLanguageItems.onNext(items)
    }

    fun loadActivityMergeTimeItems() {
        val items = ArrayList<ListItem<ActivityMergeTime>>()

        ActivityMergeTime.values().forEach {
            val (text, stringResource) = when (it) {
                ActivityMergeTime.NEVER -> {
                    "{0}" to R.string.never
                }
                ActivityMergeTime.THIRTY_MINUTES -> {
                    "${it.minute} {0}" to R.string.minutes
                }
                ActivityMergeTime.ONE_HOUR -> {
                    "${it.minute / 60} {0}" to R.string.hour
                }
                ActivityMergeTime.TWO_HOURS,
                ActivityMergeTime.THREE_HOURS,
                ActivityMergeTime.SIX_HOURS,
                ActivityMergeTime.TWELVE_HOURS -> {
                    "${it.minute / 60} {0}" to R.string.hours
                }
                ActivityMergeTime.ONE_DAY -> {
                    "${it.minute / 60 / 24} {0}" to R.string.day
                }
                ActivityMergeTime.TWO_DAYS,
                ActivityMergeTime.THREE_DAYS -> {
                    "${it.minute / 60 / 24} {0}" to R.string.days
                }
                ActivityMergeTime.ONE_WEEK -> {
                    "${it.minute / 60 / 24 / 7} {0}" to R.string.week
                }
                ActivityMergeTime.TWO_WEEKS -> {
                    "${it.minute / 60 / 24 / 7} {0}" to R.string.weeks
                }
                ActivityMergeTime.ALWAYS -> {
                    "{0}" to R.string.always
                }
            }

            items.add(ListItem(text, listOf(stringResource), it))
        }

        _activityMergeTimeItems.onNext(items)
    }
}