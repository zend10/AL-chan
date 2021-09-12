package com.zen.alchan.ui.customise

import com.zen.alchan.data.entitiy.ListStyle
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.enums.AppTheme
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.pojo.NullableItem
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class CustomiseViewModel(private val userRepository: UserRepository) : BaseViewModel() {

    private val _primaryColor = BehaviorSubject.createDefault(NullableItem<String?>())
    val primaryColor: Observable<NullableItem<String?>>
        get() = _primaryColor

    private val _secondaryColor = BehaviorSubject.createDefault(NullableItem<String?>())
    val secondaryColor: Observable<NullableItem<String?>>
        get() = _secondaryColor

    private val _negativeColor = BehaviorSubject.createDefault(NullableItem<String?>())
    val negativeColor: Observable<NullableItem<String?>>
        get() = _negativeColor

    var mediaType: MediaType = MediaType.ANIME

    private var appTheme = AppTheme.DEFAULT_THEME_YELLOW
    private var currentListStyle = ListStyle()

    override fun loadData() {
        loadOnce {
            disposables.add(
                userRepository.getAppSetting()
                    .zipWith(userRepository.getListStyle(mediaType)) { appSetting, listStyle ->
                        return@zipWith appSetting to listStyle
                    }
                    .applyScheduler()
                    .subscribe { (appSetting, listStyle) ->
                        appTheme = appSetting.appTheme
                        currentListStyle = listStyle

                        updatePrimaryColor(listStyle.primaryColor)
                        updateSecondaryColor(listStyle.secondaryColor)
                        updateNegativeColor(listStyle.negativeColor)
                    }
            )
        }
    }

    fun updatePrimaryColor(newPrimaryColor: String?) {
        currentListStyle.primaryColor = newPrimaryColor
        _primaryColor.onNext(NullableItem(newPrimaryColor))
    }

    fun updateSecondaryColor(newSecondaryColor: String?) {
        currentListStyle.secondaryColor = newSecondaryColor
        _secondaryColor.onNext(NullableItem(newSecondaryColor))
    }

    fun updateNegativeColor(newNegativeColor: String?) {
        currentListStyle.negativeColor = newNegativeColor
        _negativeColor.onNext(NullableItem(newNegativeColor))
    }
}