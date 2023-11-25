package com.zen.alchan.ui.staff.character

import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.repository.BrowseRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.CharacterEdge
import com.zen.alchan.data.response.anilist.MediaEdge
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.helper.pojo.StaffCharacterListAdapterComponent
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import com.zen.alchan.type.CharacterSort
import com.zen.alchan.type.MediaSort

class StaffCharacterListViewModel(
    private val userRepository: UserRepository,
    private val browseRepository: BrowseRepository
) : BaseViewModel<StaffCharacterListParam>() {

    private val _adapterComponent = PublishSubject.create<StaffCharacterListAdapterComponent>()
    val adapterComponent: Observable<StaffCharacterListAdapterComponent>
        get() = _adapterComponent

    private val _characters = BehaviorSubject.createDefault<List<CharacterEdge>>(listOf())
    val characters: Observable<List<CharacterEdge>>
        get() = _characters

    private val _media = BehaviorSubject.createDefault<List<MediaEdge>>(listOf())
    val media: Observable<List<MediaEdge>>
        get() = _media

    private val _emptyLayoutVisibility = BehaviorSubject.createDefault(false)
    val emptyLayoutVisibility: Observable<Boolean>
        get() = _emptyLayoutVisibility

    private val _showCharactersText = BehaviorSubject.createDefault(R.string.show_media)
    val showCharactersText: Observable<Int>
        get() = _showCharactersText

    private val _showCharacters = PublishSubject.create<Boolean>()
    val showCharacters: Observable<Boolean>
        get() = _showCharacters

    private val _mediaSortVisibility = BehaviorSubject.createDefault(false)
    val mediaSortVisibility: Observable<Boolean>
        get() = _mediaSortVisibility

    private val _mediaSortList = PublishSubject.create<List<ListItem<MediaSort>>>()
    val mediaSortList: Observable<List<ListItem<MediaSort>>>
        get() = _mediaSortList

    private val _showHideOnListVisibility = BehaviorSubject.createDefault(false)
    val showHideOnListVisibility: Observable<Boolean>
        get() = _showHideOnListVisibility

    private val _showHideOnListList = PublishSubject.create<List<ListItem<Boolean?>>>()
    val showHideOnListList: Observable<List<ListItem<Boolean?>>>
        get() = _showHideOnListList

    private var appSetting = AppSetting()
    private var shouldShowCharacters = true

    private var staffId = 0
    private var characterSort = CharacterSort.FAVOURITES_DESC
    private var mediaSort = MediaSort.POPULARITY_DESC
    private var onList: Boolean? = null

    private var hasNextPage = false
    private var currentPage = 0

    override fun loadData(param: StaffCharacterListParam) {
        loadOnce {
            staffId = param.staffId

            disposables.add(
                userRepository.getAppSetting()
                    .applyScheduler()
                    .subscribe {
                        appSetting = it
                        _adapterComponent.onNext(StaffCharacterListAdapterComponent(appSetting, mediaSort))
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
            if (shouldShowCharacters) {
                val currentCharacters = ArrayList(_characters.value ?: listOf())
                currentCharacters.add(null)
                _characters.onNext(currentCharacters)
            } else {
                val currentMedia = ArrayList(_media.value ?: listOf())
                currentMedia.add(null)
                _media.onNext(currentMedia)
            }

            loadCharacter(true)
        }
    }

    private fun loadCharacter(isLoadingNextPage: Boolean = false) {
        if (!isLoadingNextPage)
            _loading.onNext(true)

        state = State.LOADING

        disposables.add(
            browseRepository.getStaff(staffId, if (isLoadingNextPage) currentPage + 1 else 1, characterSort = listOf(characterSort), characterMediaSort = listOf(mediaSort), onList = onList)
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
                            if (shouldShowCharacters) {
                                val currentCharacters = ArrayList(_characters.value ?: listOf())
                                currentCharacters.remove(null)
                                currentCharacters.addAll(staff.characters.edges)
                                _characters.onNext(currentCharacters)
                            } else {
                                val currentMedia = ArrayList(_media.value ?: listOf())
                                currentMedia.remove(null)
                                currentMedia.addAll(staff.characterMedia.edges)
                                _media.onNext(currentMedia)
                            }
                        } else {
                            if (shouldShowCharacters) {
                                _characters.onNext(staff.characters.edges)
                            } else {
                                _media.onNext(staff.characterMedia.edges)
                            }
                        }

                        state = State.LOADED
                    },
                    {
                        if (isLoadingNextPage) {
                            if (shouldShowCharacters) {
                                val currentCharacters = ArrayList(_characters.value ?: listOf())
                                currentCharacters.remove(null)
                                _characters.onNext(currentCharacters)
                            } else {
                                val currentMedia = ArrayList(_media.value ?: listOf())
                                currentMedia.remove(null)
                                _media.onNext(currentMedia)
                            }
                        }

                        _error.onNext(it.getStringResource())
                        state  = State.ERROR
                    }
                )
        )
    }

    fun updateShowCharacters() {
        val newShouldShowCharacter = !shouldShowCharacters
        if (newShouldShowCharacter) {
            _showCharactersText.onNext(R.string.show_media)
        } else {
            _showCharactersText.onNext(R.string.show_characters)
        }
        _mediaSortVisibility.onNext(!newShouldShowCharacter)
        _showHideOnListVisibility.onNext(!newShouldShowCharacter)
        shouldShowCharacters = newShouldShowCharacter
        _showCharacters.onNext(newShouldShowCharacter)
        reloadData()
    }

    fun updateMediaSort(newMediaSort: MediaSort) {
        mediaSort = newMediaSort
        _adapterComponent.onNext(StaffCharacterListAdapterComponent(appSetting, mediaSort))
        reloadData()
    }

    fun loadMediaSorts() {
        val mediaSorts = listOf(
            MediaSort.POPULARITY_DESC,
            MediaSort.START_DATE_DESC,
            MediaSort.START_DATE,
            MediaSort.FAVOURITES_DESC,
            MediaSort.SCORE_DESC,
            MediaSort.TITLE_ROMAJI,
            MediaSort.TITLE_ENGLISH,
            MediaSort.TITLE_NATIVE
        )

        _mediaSortList.onNext(
            mediaSorts.map {
                ListItem(it.getStringResource(), it)
            }
        )
    }

    fun updateShowHideOnList(newShowHideOnList: Boolean?) {
        onList = newShowHideOnList
        reloadData()
    }

    fun loadShowHideOnLists() {
        val showHideOnLists = listOf(
            null to R.string.show_all,
            true to R.string.only_show_series_on_my_list,
            false to R.string.hide_series_on_my_list
        )

        _showHideOnListList.onNext(
            showHideOnLists.map {
                ListItem(it.second, it.first)
            }
        )
    }
}