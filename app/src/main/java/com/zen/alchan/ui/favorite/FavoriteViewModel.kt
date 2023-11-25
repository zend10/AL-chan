package com.zen.alchan.ui.favorite

import com.zen.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.Favourites
import com.zen.alchan.helper.enums.Favorite
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.pojo.FavoriteItem
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

class FavoriteViewModel(private val userRepository: UserRepository) : BaseViewModel<FavoriteParam>() {

    private val _toolbarTitle = BehaviorSubject.createDefault(R.string.favorites)
    val toolbarTitle: Observable<Int>
        get() = _toolbarTitle

    private val _favoriteAdapterComponent = PublishSubject.create<AppSetting>()
    val favoriteAdapterComponent: Observable<AppSetting>
        get() = _favoriteAdapterComponent

    private val _favorites = BehaviorSubject.createDefault<List<FavoriteItem?>>(listOf())
    val favorites: Observable<List<FavoriteItem?>>
        get() = _favorites

    private val _emptyLayoutVisibility = BehaviorSubject.createDefault(false)
    val emptyLayoutVisibility: Observable<Boolean>
        get() = _emptyLayoutVisibility

    private val _reorderVisibility = BehaviorSubject.createDefault(false)
    val reorderVisibility: Observable<Boolean>
        get() = _reorderVisibility

    private val _reorderItems = PublishSubject.create<List<String>>()
    val reorderItems: Observable<List<String>>
        get() = _reorderItems

    private var userId = 0
    private var favorite = Favorite.ANIME
    private var appSetting = AppSetting()
    private var hasNextPage = false
    private var currentPage = 0

    override fun loadData(param: FavoriteParam) {
        userId = param.userId
        favorite = param.favorite

        _toolbarTitle.onNext(
            when (favorite) {
                Favorite.ANIME -> R.string.favorite_anime
                Favorite.MANGA -> R.string.favorite_manga
                Favorite.CHARACTERS -> R.string.favorite_characters
                Favorite.STAFF -> R.string.favorite_staff
                Favorite.STUDIOS -> R.string.favorite_studios
            }
        )

        _reorderVisibility.onNext(userId == 0)

        disposables.add(
            userRepository.getAppSetting()
                .zipWith(userRepository.getViewer(Source.CACHE)) { appSetting, user ->
                    return@zipWith appSetting to user
                }
                .applyScheduler()
                .subscribe { (appSetting, user) ->
                    this.appSetting = appSetting
                    _favoriteAdapterComponent.onNext(appSetting)
                    if (userId == 0)
                        this.userId = user.id
                    loadFavoriteList()
                }
        )
    }

    fun reloadData() {
        loadFavoriteList()
    }

    fun loadNextPage() {
        if ((state == State.LOADED || state == State.ERROR) && hasNextPage) {
            val currentFavorites = ArrayList(_favorites.value ?: listOf())
            currentFavorites.add(null)
            _favorites.onNext(currentFavorites)

            loadFavoriteList(true)
        }
    }

    fun loadAll() {
        while (hasNextPage) {
            loadNextPage()
        }

        if (!hasNextPage) {
            _reorderItems.onNext(
                when (favorite) {
                    Favorite.ANIME -> _favorites.value?.map { it?.anime?.getTitle(appSetting) ?: "" }
                    Favorite.MANGA -> _favorites.value?.map { it?.manga?.getTitle(appSetting) ?: "" }
                    Favorite.CHARACTERS -> _favorites.value?.map { it?.character?.name?.userPreferred ?: "" }
                    Favorite.STAFF -> _favorites.value?.map { it?.staff?.name?.userPreferred ?: "" }
                    Favorite.STUDIOS -> _favorites.value?.map { it?.studio?.name ?: "" }
                } ?: listOf()
            )
        }
    }

    fun updateOrder(newOrder: List<String>) {
        _loading.onNext(true)

        val ids = when (favorite) {
            Favorite.ANIME -> _favorites.value?.sortedBy { newOrder.indexOf(it?.anime?.getTitle(appSetting) ?: "") }?.map { it?.anime?.getId() }?.filterNotNull()
            Favorite.MANGA -> _favorites.value?.sortedBy { newOrder.indexOf(it?.manga?.getTitle(appSetting) ?: "") }?.map { it?.manga?.getId() }?.filterNotNull()
            Favorite.CHARACTERS -> _favorites.value?.sortedBy { newOrder.indexOf(it?.character?.name?.userPreferred ?: "") }?.map { it?.character?.id }?.filterNotNull()
            Favorite.STAFF -> _favorites.value?.sortedBy { newOrder.indexOf(it?.staff?.name?.userPreferred ?: "") }?.map { it?.staff?.id }?.filterNotNull()
            Favorite.STUDIOS -> _favorites.value?.sortedBy { newOrder.indexOf(it?.studio?.name ?: "") }?.map { it?.studio?.id }?.filterNotNull()
        } ?: listOf()

        disposables.add(
            userRepository.updateFavoriteOrder(ids, favorite)
                .applyScheduler()
                .doFinally {
                    _loading.onNext(false)
                }
                .subscribe(
                    {
                        val pageInfo = it.getPageInfo(favorite)
                        hasNextPage = pageInfo.hasNextPage
                        currentPage = pageInfo.currentPage

                        val favoriteItems = getFavoriteItems(it)
                        _favorites.onNext(favoriteItems)
                    },
                    {
                        _error.onNext(it.getStringResource())
                    }
                )
        )
    }

    private fun loadFavoriteList(isLoadingNextPage: Boolean = false) {
        if (!isLoadingNextPage)
            _loading.onNext(true)

        state = State.LOADING

        disposables.add(
            userRepository.getFavorites(userId, if (isLoadingNextPage) currentPage + 1 else 1)
                .applyScheduler()
                .doFinally {
                    if (!isLoadingNextPage) {
                        _loading.onNext(false)
                        _emptyLayoutVisibility.onNext(_favorites.value.isNullOrEmpty())
                    }
                }
                .subscribe(
                    {
                        val pageInfo = it.getPageInfo(favorite)
                        hasNextPage = pageInfo.hasNextPage
                        currentPage = pageInfo.currentPage

                        val favoriteItems = getFavoriteItems(it)

                        if (isLoadingNextPage) {
                            val currentFavorites = ArrayList(_favorites.value ?: listOf())
                            currentFavorites.remove(null)
                            currentFavorites.addAll(favoriteItems)
                            _favorites.onNext(currentFavorites)
                        } else {
                            _favorites.onNext(favoriteItems)
                        }

                        state = State.LOADED
                    },
                    {
                        if (isLoadingNextPage) {
                            val currentFavorites = ArrayList(_favorites.value ?: listOf())
                            currentFavorites.remove(null)
                            _favorites.onNext(currentFavorites)
                        }

                        _error.onNext(it.getStringResource())
                        state = State.ERROR
                    }
                )
        )
    }

    private fun getFavoriteItems(favorites: Favourites): List<FavoriteItem> {
        return when (favorite) {
            Favorite.ANIME -> favorites.anime.nodes.map { FavoriteItem(anime = it, favorite = favorite) }
            Favorite.MANGA -> favorites.manga.nodes.map { FavoriteItem(manga = it, favorite = favorite) }
            Favorite.CHARACTERS -> favorites.characters.nodes.map { FavoriteItem(character = it, favorite = favorite) }
            Favorite.STAFF -> favorites.staff.nodes.map { FavoriteItem(staff = it, favorite = favorite) }
            Favorite.STUDIOS -> favorites.studios.nodes.map { FavoriteItem(studio = it, favorite = favorite) }
        }
    }
}