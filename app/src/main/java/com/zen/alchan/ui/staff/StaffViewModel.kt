package com.zen.alchan.ui.staff

import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.repository.BrowseRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.Staff
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.pojo.StaffItem
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class StaffViewModel(
    private val browseRepository: BrowseRepository,
    private val userRepository: UserRepository
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

    private val _favoritesCount = BehaviorSubject.createDefault(0)
    val favoritesCount: Observable<Int>
        get() = _favoritesCount

    private val _staffItemList = BehaviorSubject.createDefault(listOf<StaffItem>())
    val staffItemList: Observable<List<StaffItem>>
        get() = _staffItemList

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
                        _staffAdapterComponent.onNext(appSetting)
                        loadStaff()
                    }
            )
        }
    }

    private fun loadStaff(isReloading: Boolean = false) {
        _loading.onNext(true)

        disposables.add(
            browseRepository.getStaff(staffId)
                .applyScheduler()
                .doFinally { _loading.onNext(false) }
                .subscribe(
                    { staff ->
                        this.staff = staff

                        _staffImage.onNext(staff.getImage(appSetting))
                        _staffName.onNext(staff.name.userPreferred)
                        _favoritesCount.onNext(staff.favourites)

                        val itemList = ArrayList<StaffItem>()

                        if (staff.description.isNotBlank())
                            itemList.add(StaffItem(staff = staff, viewType = StaffItem.VIEW_TYPE_BIO))

                        _staffItemList.onNext(itemList)
                    },
                    {
                        _error.onNext(it.getStringResource())
                    }
                )
        )
    }
}