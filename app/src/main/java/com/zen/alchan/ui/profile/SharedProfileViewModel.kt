package com.zen.alchan.ui.profile

import android.util.Log
import com.zen.alchan.data.repository.AuthenticationRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.ProfileData
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.data.response.anilist.UserStatistics
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.formatTwoDecimal
import com.zen.alchan.helper.extensions.sendMessage
import com.zen.alchan.helper.pojo.BioItem
import com.zen.alchan.helper.pojo.Tendency
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import type.MediaListStatus

class SharedProfileViewModel(
    private val authenticationRepository: AuthenticationRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val _isViewerProfile = PublishSubject.create<Boolean>()
    private val _user = BehaviorSubject.createDefault(User.EMPTY_USER)
    private val _profileData = BehaviorSubject.createDefault(ProfileData.EMPTY_PROFILE_DATA)

    private val _animeCount = BehaviorSubject.createDefault(0)
    private val _mangaCount = BehaviorSubject.createDefault(0)
    private val _followingCount = BehaviorSubject.createDefault(0)
    private val _followersCount = BehaviorSubject.createDefault(0)

    val isViewerProfile: Observable<Boolean>
        get() = _isViewerProfile

    val user: Observable<User>
        get() = _user

    val profileData: Observable<ProfileData>
        get() = _profileData

    val animeCount: Observable<Int>
        get() = _animeCount

    val mangaCount: Observable<Int>
        get() = _mangaCount

    val followingCount: Observable<Int>
        get() = _followingCount

    val followersCount: Observable<Int>
        get() = _followersCount

    var userId = 0

    override fun loadData() {
        checkIsAuthenticated()
        checkIsViewerProfile()
    }

    private fun checkIsViewerProfile() {
        _isViewerProfile.onNext(userId == 0)
    }

    private fun checkIsAuthenticated() {
        disposables.add(
            authenticationRepository.getIsAuthenticated()
                .applyScheduler()
                .subscribe {
                    loadUserData()
                    _isAuthenticated.onNext(it)
                }
        )
    }

    private fun loadUserData(isReloading: Boolean = false) {
        if (!isReloading && state == State.LOADED) return

        disposables.add(
            authenticationRepository.viewer
                .applyScheduler()
                .subscribe {
                    loadProfileData(it.id, if (isReloading) Source.NETWORK else null)
                    _user.onNext(it)
                    state = State.LOADED
                }
        )

        if (userId == 0)
            authenticationRepository.getViewerData()
        else {
            // TODO: update this to be able to get user data of other user as well
        }
    }

    private fun loadProfileData(userId: Int, source: Source?) {
        disposables.add(
            userRepository.getProfileData(userId, source = source)
                .applyScheduler()
                .subscribe(
                    {
                        _profileData.onNext(it)

                        _animeCount.onNext(
                            it.user.statistics.anime.statuses.find { anime ->
                                anime.status == MediaListStatus.COMPLETED
                            }?.count ?: 0
                        )
                        _mangaCount.onNext(
                            it.user.statistics.manga.statuses.find { manga ->
                                manga.status == MediaListStatus.COMPLETED
                            }?.count ?: 0
                        )
                        _followingCount.onNext(it.following.pageInfo.total)
                        _followersCount.onNext(it.followers.pageInfo.total)
                    },
                    {
                        _error.onNext(it.sendMessage())
                    }
                )
        )
    }

    enum class Page {
        BIO,
        FAVORITE,
        STATS,
        REVIEW
    }
}