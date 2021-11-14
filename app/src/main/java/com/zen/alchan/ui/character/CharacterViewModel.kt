package com.zen.alchan.ui.character

import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.data.repository.BrowseRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.Character
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.pojo.CharacterItem
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class CharacterViewModel(
    private val browseRepository: BrowseRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val _characterAdapterComponent = PublishSubject.create<AppSetting>()
    val characterAdapterComponent: Observable<AppSetting>
        get() = _characterAdapterComponent

    private val _characterImage = BehaviorSubject.createDefault("")
    val characterImage: Observable<String>
        get() = _characterImage

    private val _characterName = BehaviorSubject.createDefault("")
    val characterName: Observable<String>
        get() = _characterName

    private val _characterNativeName = BehaviorSubject.createDefault("")
    val characterNativeName: Observable<String>
        get() = _characterNativeName

    private val _mediaCount = BehaviorSubject.createDefault(0)
    val mediaCount: Observable<Int>
        get() = _mediaCount

    private val _favoritesCount = BehaviorSubject.createDefault(0)
    val favoritesCount: Observable<Int>
        get() = _favoritesCount

    private val _characterItemList = BehaviorSubject.createDefault(listOf<CharacterItem>())
    val characterItemList: Observable<List<CharacterItem>>
        get() = _characterItemList

    private var characterId = 0

    private var character: Character = Character()
    private var appSetting: AppSetting = AppSetting()

    override fun loadData() {
        // do nothing
    }

    fun loadData(characterId: Int) {
        loadOnce {
            this.characterId = characterId

            disposables.add(
                userRepository.getIsAuthenticated().zipWith(userRepository.getAppSetting()) { isAuthenticated, appSetting ->
                    return@zipWith isAuthenticated to appSetting
                }
                    .applyScheduler()
                    .subscribe { (isAuthenticated, appSetting) ->
                        this.appSetting = appSetting
                        _characterAdapterComponent.onNext(appSetting)
                        loadCharacter()
                    }
            )
        }
    }

    private fun loadCharacter(isReloading: Boolean = false) {
        _loading.onNext(true)

        disposables.add(
            browseRepository.getCharacter(characterId)
                .applyScheduler()
                .doFinally { _loading.onNext(false) }
                .subscribe(
                    { character ->
                        this.character = character

                        _characterImage.onNext(character.getImage(appSetting))
                        _characterName.onNext(character.name.userPreferred)
                        _characterNativeName.onNext(character.name.native)
                        _mediaCount.onNext(character.media.pageInfo.total)
                        _favoritesCount.onNext(character.favourites)

                        val itemList = ArrayList<CharacterItem>()

                        if (character.description.isNotBlank())
                            itemList.add(CharacterItem(character = character, viewType = CharacterItem.VIEW_TYPE_BIO))

                        _characterItemList.onNext(itemList)
                    },
                    {
                        _error.onNext(it.getStringResource())
                    }
                )
        )
    }
}