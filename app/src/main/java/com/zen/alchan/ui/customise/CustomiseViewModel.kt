package com.zen.alchan.ui.customise

import com.zen.alchan.data.entitiy.ListStyle
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.enums.AppTheme
import com.zen.alchan.helper.enums.ListType
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.enums.getString
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.helper.pojo.NullableItem
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class CustomiseViewModel(private val userRepository: UserRepository) : BaseViewModel() {

    private val _listStyle = PublishSubject.create<ListStyle>()
    val listStyle: Observable<ListStyle>
        get() = _listStyle

    private val _listType = BehaviorSubject.createDefault(ListType.LINEAR)
    val listType: Observable<ListType>
        get() = _listType

    private val _primaryColor = BehaviorSubject.createDefault(NullableItem<String?>())
    val primaryColor: Observable<NullableItem<String?>>
        get() = _primaryColor

    private val _secondaryColor = BehaviorSubject.createDefault(NullableItem<String?>())
    val secondaryColor: Observable<NullableItem<String?>>
        get() = _secondaryColor

    private val _negativeColor = BehaviorSubject.createDefault(NullableItem<String?>())
    val negativeColor: Observable<NullableItem<String?>>
        get() = _negativeColor

    private val _textColor = BehaviorSubject.createDefault(NullableItem<String?>())
    val textColor: Observable<NullableItem<String?>>
        get() = _textColor

    private val _cardColor = BehaviorSubject.createDefault(NullableItem<String?>())
    val cardColor: Observable<NullableItem<String?>>
        get() = _cardColor

    private val _toolbarColor = BehaviorSubject.createDefault(NullableItem<String?>())
    val toolbarColor: Observable<NullableItem<String?>>
        get() = _toolbarColor

    private val _backgroundColor = BehaviorSubject.createDefault(NullableItem<String?>())
    val backgroundColor: Observable<NullableItem<String?>>
        get() = _backgroundColor

    private val _floatingButtonColor = BehaviorSubject.createDefault(NullableItem<String?>())
    val floatingButtonColor: Observable<NullableItem<String?>>
        get() = _floatingButtonColor

    private val _floatingIconColor = BehaviorSubject.createDefault(NullableItem<String?>())
    val floatingIconColor: Observable<NullableItem<String?>>
        get() = _floatingIconColor

    private val _listTypes = PublishSubject.create<List<ListItem<ListType>>>()
    val listTypes: Observable<List<ListItem<ListType>>>
        get() = _listTypes

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

                        updateListType(listStyle.listType)
                        updatePrimaryColor(listStyle.primaryColor)
                        updateSecondaryColor(listStyle.secondaryColor)
                        updateNegativeColor(listStyle.negativeColor)
                        updateTextColor(listStyle.textColor)
                        updateCardColor(listStyle.cardColor)
                        updateToolbarColor(listStyle.toolbarColor)
                        updateBackgroundColor(listStyle.backgroundColor)
                        updateFloatingButtonColor(listStyle.floatingButtonColor)
                        updateFloatingIconColor(listStyle.floatingIconColor)
                    }
            )
        }
    }

    fun saveCurrentListStyle() {
        userRepository.setListStyle(mediaType, currentListStyle)
        _listStyle.onNext(currentListStyle)
    }

    fun resetCurrentListStyle() {
        currentListStyle = ListStyle()
        userRepository.setListStyle(mediaType, currentListStyle)
        _listStyle.onNext(currentListStyle)
    }

    fun updateListType(newListType: ListType) {
        currentListStyle.listType = newListType
        _listType.onNext(newListType)
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

    fun updateTextColor(newTextColor: String?) {
        currentListStyle.textColor = newTextColor
        _textColor.onNext(NullableItem(newTextColor))
    }

    fun updateCardColor(newCardColor: String?) {
        currentListStyle.cardColor = newCardColor
        _cardColor.onNext(NullableItem(newCardColor))
    }

    fun updateToolbarColor(newToolbarColor: String?) {
        currentListStyle.toolbarColor = newToolbarColor
        _toolbarColor.onNext(NullableItem(newToolbarColor))
    }

    fun updateBackgroundColor(newBackgroundColor: String?) {
        currentListStyle.backgroundColor = newBackgroundColor
        _backgroundColor.onNext(NullableItem(newBackgroundColor))
    }

    fun updateFloatingButtonColor(newFloatingButtonColor: String?) {
        currentListStyle.floatingButtonColor = newFloatingButtonColor
        _floatingButtonColor.onNext(NullableItem(newFloatingButtonColor))
    }

    fun updateFloatingIconColor(newFloatingIconColor: String?) {
        currentListStyle.floatingIconColor = newFloatingIconColor
        _floatingIconColor.onNext(NullableItem(newFloatingIconColor))
    }

    fun loadListTypes() {
        _listTypes.onNext(ListType.values().map { ListItem(it.getString(), it) })
    }
}