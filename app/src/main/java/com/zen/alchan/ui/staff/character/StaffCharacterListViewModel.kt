package com.zen.alchan.ui.staff.character

import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.repository.BrowseRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.CharacterEdge
import com.zen.alchan.data.response.anilist.MediaEdge
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import type.CharacterSort
import type.MediaSort

class StaffCharacterListViewModel(
    private val userRepository: UserRepository,
    private val browseRepository: BrowseRepository
) : BaseViewModel() {

    private val _appSetting = PublishSubject.create<AppSetting>()
    val appSetting: Observable<AppSetting>
        get() = _appSetting

    private val _characters = BehaviorSubject.createDefault<List<CharacterEdge>>(listOf())
    val characters: Observable<List<CharacterEdge>>
        get() = _characters

    private val _emptyLayoutVisibility = BehaviorSubject.createDefault(false)
    val emptyLayoutVisibility: Observable<Boolean>
        get() = _emptyLayoutVisibility

    private var staffId = 0
    private var characterSort = CharacterSort.FAVOURITES_DESC

    private var hasNextPage = false
    private var currentPage = 0

    override fun loadData() {
        // do nothing
    }

    fun loadData(staffId: Int) {
        loadOnce {
            this.staffId = staffId

            disposables.add(
                userRepository.getAppSetting()
                    .applyScheduler()
                    .subscribe {
                        _appSetting.onNext(it)
                        loadCharacter()
                    }
            )
        }
    }

    fun reloadData() {
        loadCharacter()
    }

    fun loadNextPage() {
        if ((state == State.LOADED || state == State.ERROR) && hasNextPage) {
            val currentCharacters = ArrayList(_characters.value ?: listOf())
            currentCharacters.add(null)
            _characters.onNext(currentCharacters)

            loadCharacter(true)
        }
    }

    fun loadCharacter(isLoadingNextPage: Boolean = false) {
        if (!isLoadingNextPage)
            _loading.onNext(true)

        state = State.LOADING

        disposables.add(
            browseRepository.getStaff(staffId, if (isLoadingNextPage) currentPage + 1 else 1, characterSort = listOf(characterSort))
                .applyScheduler()
                .doFinally {
                    if (!isLoadingNextPage) {
                        _loading.onNext(false)
                        _emptyLayoutVisibility.onNext(_characters.value.isNullOrEmpty())
                    }
                }
                .subscribe(
                    { staff ->
                        hasNextPage = staff.characters.pageInfo.hasNextPage
                        currentPage = staff.characters.pageInfo.currentPage

                        if (isLoadingNextPage) {
                            val currentCharacters = ArrayList(_characters.value ?: listOf())
                            currentCharacters.remove(null)
                            currentCharacters.addAll(staff.characters.edges)
                            _characters.onNext(currentCharacters)
                        } else {
                            _characters.onNext(staff.characters.edges)
                        }

                        state = State.LOADED
                    },
                    {
                        _error.onNext(it.getStringResource())
                        state  = State.ERROR
                    }
                )
        )
    }
}