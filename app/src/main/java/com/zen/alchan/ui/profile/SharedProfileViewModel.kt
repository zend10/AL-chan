package com.zen.alchan.ui.profile

import com.zen.alchan.data.repository.AuthenticationRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.ProfileData
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.data.response.anilist.UserStatistics
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.formatTwoDecimal
import com.zen.alchan.helper.extensions.sendMessage
import com.zen.alchan.helper.pojo.BioItem
import com.zen.alchan.helper.pojo.Tendency
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import type.MediaListStatus

class SharedProfileViewModel(
    private val authenticationRepository: AuthenticationRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val isViewerProfileSubject = BehaviorSubject.createDefault(false)
    private val userSubject = BehaviorSubject.createDefault(User.EMPTY_USER)
    private val profileDataSubject = BehaviorSubject.createDefault(ProfileData.EMPTY_PROFILE_DATA)

    private val animeCountSubject = BehaviorSubject.createDefault(0)
    private val mangaCountSubject = BehaviorSubject.createDefault(0)
    private val followingCountSubject = BehaviorSubject.createDefault(0)
    private val followersCountSubject = BehaviorSubject.createDefault(0)

    val isViewerProfile: Observable<Boolean>
        get() = isViewerProfileSubject

    val user: Observable<User>
        get() = userSubject

    val profileData: Observable<ProfileData>
        get() = profileDataSubject

    val animeCount: Observable<Int>
        get() = animeCountSubject

    val mangaCount: Observable<Int>
        get() = mangaCountSubject

    val followingCount: Observable<Int>
        get() = followingCountSubject

    val followersCount: Observable<Int>
        get() = followersCountSubject

    var userId = 0

    fun checkIsViewerProfile() {
        isViewerProfileSubject.onNext(userId == 0)
    }

    fun checkIsAuthenticated() {
        disposables.add(
            authenticationRepository.getIsAuthenticated()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    loadUserData()
                    isAuthenticatedSubject.onNext(it)
                }
        )
    }

    private fun loadUserData(isReloading: Boolean = false) {
        if (!isReloading && state == State.LOADED) return

        disposables.add(
            authenticationRepository.viewer
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    userId = it.id
                    loadProfileData(if (isReloading) Source.NETWORK else null)

                    userSubject.onNext(it)

                    state = State.LOADED
                }
        )

        if (userId == 0)
            authenticationRepository.getViewerData()
        else {
            // TODO: update this to be able to get user data of other user as well
        }
    }

    private fun loadProfileData(source: Source?) {
        disposables.add(
            userRepository.getProfileData(userId, source = source)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        profileDataSubject.onNext(it)

                        animeCountSubject.onNext(
                            it.user.statistics.anime.statuses.find { anime ->
                                anime.status == MediaListStatus.COMPLETED
                            }?.count ?: 0
                        )
                        mangaCountSubject.onNext(
                            it.user.statistics.manga.statuses.find { manga ->
                                manga.status == MediaListStatus.COMPLETED
                            }?.count ?: 0
                        )
                        followingCountSubject.onNext(it.following.pageInfo.total)
                        followersCountSubject.onNext(it.followers.pageInfo.total)
                    },
                    {
                        errorSubject.onNext(it.sendMessage())
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