package com.zen.alchan.ui.settings.anilist

import com.zen.alchan.R
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.data.response.anilist.UserOptions
import com.zen.alchan.helper.enums.ActivityMergeTime
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.sendMessage
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import type.UserTitleLanguage

class AniListSettingsViewModel(private val userRepository: UserRepository) : BaseViewModel() {

    private val _mediaTitleLanguage = BehaviorSubject.createDefault(UserTitleLanguage.ROMAJI)
    val mediaTitleLanguage: Observable<UserTitleLanguage>
        get() = _mediaTitleLanguage

    private val _progressActivityMergeTime = BehaviorSubject.createDefault(ActivityMergeTime.NEVER)
    val progressActivityMergeTime: Observable<ActivityMergeTime>
        get() = _progressActivityMergeTime

    private val _showAdultContent = BehaviorSubject.createDefault(false)
    val showAdultContent: Observable<Boolean>
        get() = _showAdultContent

    private val _receiveAiringNotifications = BehaviorSubject.createDefault(false)
    val receiveAiringNotifications: Observable<Boolean>
        get() = _receiveAiringNotifications

    private val _mediaTitleLanguages = PublishSubject.create<List<UserTitleLanguage>>()
    val mediaTitleLanguages: Observable<List<UserTitleLanguage>>
        get() = _mediaTitleLanguages

    private val _activityMergeTimes = PublishSubject.create<List<ActivityMergeTime>>()
    val activityMergeTimes: Observable<List<ActivityMergeTime>>
        get() = _activityMergeTimes

    private var viewer: User? = null
    private var currentAniListSetting: UserOptions? = null

    override fun loadData() {
        disposables.add(
            userRepository.getViewer(Source.CACHE)
                .applyScheduler()
                .subscribe {
                    viewer = it

                    val userOptions = it.options
                    currentAniListSetting = userOptions
                    updateMediaTitleLanguage(userOptions.titleLanguage ?: UserTitleLanguage.ROMAJI)
                    updateProgressActivityMergeTime(userOptions.activityMergeTime)
                    updateShowAdultContent(userOptions.displayAdultContent)
                    updateReceiveAiringNotifications(userOptions.airingNotifications)
                }
        )
    }

    fun updateMediaTitleLanguage(newMediaTitleLanguage: UserTitleLanguage) {
        currentAniListSetting?.titleLanguage = newMediaTitleLanguage
        _mediaTitleLanguage.onNext(newMediaTitleLanguage)
    }

    fun updateProgressActivityMergeTime(newProgressActivityMergeTime: Int) {
        currentAniListSetting?.activityMergeTime = newProgressActivityMergeTime
        val selectedActivityMergeTime = ActivityMergeTime.values().toList().find {
            it.minute == newProgressActivityMergeTime
        } ?: ActivityMergeTime.NEVER
        _progressActivityMergeTime.onNext(selectedActivityMergeTime)
    }

    fun updateShowAdultContent(shouldShowAdultContent: Boolean) {
        currentAniListSetting?.displayAdultContent = shouldShowAdultContent
        _showAdultContent.onNext(shouldShowAdultContent)
    }

    fun updateReceiveAiringNotifications(shouldReceiveAiringNotifications: Boolean) {
        currentAniListSetting?.airingNotifications = shouldReceiveAiringNotifications
        _receiveAiringNotifications.onNext(shouldReceiveAiringNotifications)
    }

    fun getMediaTitleLanguages() {
        val userTitleLanguages = ArrayList<UserTitleLanguage>()
        userTitleLanguages.add(UserTitleLanguage.ROMAJI)
        userTitleLanguages.add(UserTitleLanguage.ENGLISH)
        userTitleLanguages.add(UserTitleLanguage.NATIVE)
        _mediaTitleLanguages.onNext(userTitleLanguages)
    }

    fun getActivityMergeTimes() {
        _activityMergeTimes.onNext(ActivityMergeTime.values().toList())
    }

    fun saveAniListSettings() {
        _loading.onNext(true)

        currentAniListSetting?.apply {
            disposables.add(
                userRepository.updateAniListSettings(
                    titleLanguage ?: UserTitleLanguage.ROMAJI,
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
}