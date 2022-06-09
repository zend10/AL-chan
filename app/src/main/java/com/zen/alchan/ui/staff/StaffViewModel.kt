package com.zen.alchan.ui.staff

import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.repository.BrowseRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.MediaEdge
import com.zen.alchan.data.response.anilist.Staff
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.pojo.StaffItem
import com.zen.alchan.helper.service.clipboard.ClipboardService
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class StaffViewModel(
    private val browseRepository: BrowseRepository,
    private val userRepository: UserRepository,
    private val clipboardService: ClipboardService
) : BaseViewModel() {

    private val _staffAdapterComponent = PublishSubject.create<AppSetting>()
    val staffAdapterComponent: Observable<AppSetting>
        get() = _staffAdapterComponent

    private val _staffImage = BehaviorSubject.createDefault("")
    val staffImage: Observable<String>
        get() = _staffImage

    private val _staffName = BehaviorSubject.createDefault("")
    val staffName: Observable<String>
        get() = _staffName

    private val _mediaOrCharacterCount = BehaviorSubject.createDefault(0)
    val mediaOrCharacterCount: Observable<Int>
        get() = _mediaOrCharacterCount

    private val _mediaOrCharacterText = BehaviorSubject.createDefault(R.string.media)
    val mediaOrCharacterText: Observable<Int>
        get() = _mediaOrCharacterText

    private val _mediaOrCharacterCountVisibility = BehaviorSubject.createDefault(false)
    val mediaOrCharacterCountVisibility: Observable<Boolean>
        get() = _mediaOrCharacterCountVisibility

    private val _favoritesCount = BehaviorSubject.createDefault(0)
    val favoritesCount: Observable<Int>
        get() = _favoritesCount

    private val _isFavorite = BehaviorSubject.createDefault(false)
    val isFavorite: Observable<Boolean>
        get() = _isFavorite

    private val _staffItemList = BehaviorSubject.createDefault(listOf<StaffItem>())
    val staffItemList: Observable<List<StaffItem>>
        get() = _staffItemList

    private val _staffLink = PublishSubject.create<String>()
    val staffLink: Observable<String>
        get() = _staffLink

    private val _staffImageForPreview = PublishSubject.create<String>()
    val staffImageForPreview: Observable<String>
        get() = _staffImageForPreview

    private var staffId = 0

    private var staff: Staff = Staff()
    private var appSetting: AppSetting = AppSetting()

    override fun loadData() {
        // do nothing
    }

    fun loadData(staffId: Int) {
        loadOnce {
            this.staffId = staffId

            disposables.add(
                userRepository.getIsAuthenticated().zipWith(userRepository.getAppSetting()) { isAuthenticated, appSetting ->
                    return@zipWith isAuthenticated to appSetting
                }
                    .applyScheduler()
                    .subscribe { (isAuthenticated, appSetting) ->
                        this.appSetting = appSetting
                        _isAuthenticated.onNext(isAuthenticated)
                        _staffAdapterComponent.onNext(appSetting)
                        loadStaff()
                    }
            )
        }

        if (staff.id != 0)
            checkFavorite()
    }

    private fun checkFavorite() {
        if (staff.id == 0)
            return

        if (_isAuthenticated.value != true) {
            _isAuthenticated.onNext(_isAuthenticated.value ?: false)
            return
        }

        disposables.add(
            userRepository.getViewer(Source.CACHE)
                .map {
                    it.favourites.staff.nodes.find { it.id == staff.id } != null
                }
                .applyScheduler()
                .subscribe {
                    _isFavorite.onNext(it)
                }
        )
    }

    private fun loadStaff(isReloading: Boolean = false) {
        _loading.onNext(true)

        disposables.add(
            browseRepository.getStaff(staffId, 1)
                .applyScheduler()
                .doFinally { _loading.onNext(false) }
                .subscribe(
                    { staff ->
                        this.staff = staff

                        _staffImage.onNext(staff.getImage(appSetting))
                        _staffName.onNext(staff.name.userPreferred)
                        _favoritesCount.onNext(staff.favourites)
                        _isFavorite.onNext(staff.isFavourite)

                        val itemList = ArrayList<StaffItem>()

                        if (staff.description.isNotBlank())
                            itemList.add(StaffItem(staff = staff, viewType = StaffItem.VIEW_TYPE_BIO))

                        if (staff.characters.edges.isNotEmpty())
                            itemList.add(StaffItem(staff = staff, viewType = StaffItem.VIEW_TYPE_CHARACTER))

                        if (staff.staffMedia.edges.isNotEmpty()) {
                            val media = ArrayList<MediaEdge>()
                            staff.staffMedia.edges.forEach { mediaEdge ->
                                if (media.size == 9)
                                    return@forEach

                                if (media.find { it.node.getId() == mediaEdge.node.getId() } == null) {
                                    media.add(mediaEdge)
                                }
                            }
                            itemList.add(StaffItem(staff = staff, media = media, viewType = StaffItem.VIEW_TYPE_MEDIA))
                        }

                        val mediaCount = staff.staffMedia.pageInfo.total
                        val characterCount = staff.characters.pageInfo.total
                        if (mediaCount > characterCount) {
                            _mediaOrCharacterCount.onNext(mediaCount)
                            _mediaOrCharacterText.onNext(R.string.media)
                            _mediaOrCharacterCountVisibility.onNext(!staff.staffMedia.pageInfo.hasNextPage)
                        } else {
                            _mediaOrCharacterCount.onNext(characterCount)
                            _mediaOrCharacterText.onNext(R.string.roles)
                            _mediaOrCharacterCountVisibility.onNext(!staff.characters.pageInfo.hasNextPage)
                        }

                        _staffItemList.onNext(itemList)
                    },
                    {
                        _error.onNext(it.getStringResource())
                    }
                )
        )
    }

    fun loadStaffLink() {
        _staffLink.onNext(staff.siteUrl)
    }

    fun copyStaffLink() {
        disposables.add(
            clipboardService.copyPlainText(staff.siteUrl)
                .applyScheduler()
                .subscribe(
                    {
                        _success.onNext(R.string.link_copied)
                    },
                    {
                        it.printStackTrace()
                    }
                )
        )
    }

    fun loadStaffImage() {
        if (staff.image.large.isNotBlank())
            _staffImageForPreview.onNext(staff.image.large)
    }

    fun toggleFavorite() {
        _loading.onNext(true)

        disposables.add(
            userRepository.toggleFavorite(staffId = staff.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally {
                    _loading.onNext(false)
                }
                .subscribe(
                    {
                        val isFavorited = _isFavorite.value ?: false
                        _isFavorite.onNext(!isFavorited)
                    },
                    {
                        _error.onNext(it.getStringResource())
                    }
                )
        )
    }
}