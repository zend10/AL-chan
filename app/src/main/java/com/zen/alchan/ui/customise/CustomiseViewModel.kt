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

    private val _primaryColorAndHasAlpha = PublishSubject.create<Pair<String?, Boolean>>()
    val primaryColorAndHasAlpha: Observable<Pair<String?, Boolean>>
        get() = _primaryColorAndHasAlpha

    private val _secondaryColorAndHasAlpha = PublishSubject.create<Pair<String?, Boolean>>()
    val secondaryColorAndHasAlpha: Observable<Pair<String?, Boolean>>
        get() = _secondaryColorAndHasAlpha

    private val _negativeColorAndHasAlpha = PublishSubject.create<Pair<String?, Boolean>>()
    val negativeColorAndHasAlpha: Observable<Pair<String?, Boolean>>
        get() = _negativeColorAndHasAlpha

    private val _textColorAndHasAlpha = PublishSubject.create<Pair<String?, Boolean>>()
    val textColorAndHasAlpha: Observable<Pair<String?, Boolean>>
        get() = _textColorAndHasAlpha

    private val _cardColorAndHasAlpha = PublishSubject.create<Pair<String?, Boolean>>()
    val cardColorAndHasAlpha: Observable<Pair<String?, Boolean>>
        get() = _cardColorAndHasAlpha

    private val _toolbarColorAndHasAlpha = PublishSubject.create<Pair<String?, Boolean>>()
    val toolbarColorAndHasAlpha: Observable<Pair<String?, Boolean>>
        get() = _toolbarColorAndHasAlpha

    private val _backgroundColorAndHasAlpha = PublishSubject.create<Pair<String?, Boolean>>()
    val backgroundColorAndHasAlpha: Observable<Pair<String?, Boolean>>
        get() = _backgroundColorAndHasAlpha

    private val _floatingButtonColorAndHasAlpha = PublishSubject.create<Pair<String?, Boolean>>()
    val floatingButtonColorAndHasAlpha: Observable<Pair<String?, Boolean>>
        get() = _floatingButtonColorAndHasAlpha

    private val _floatingIconColorAndHasAlpha = PublishSubject.create<Pair<String?, Boolean>>()
    val floatingIconColorAndHasAlpha: Observable<Pair<String?, Boolean>>
        get() = _floatingIconColorAndHasAlpha

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

    fun loadPrimaryColor() {
        val hexColor = _primaryColor.value?.data
        val hasAlpha = false
        _primaryColorAndHasAlpha.onNext(hexColor to hasAlpha)
    }

    fun loadSecondaryColor() {
        val hexColor = _secondaryColor.value?.data
        val hasAlpha = false
        _secondaryColorAndHasAlpha.onNext(hexColor to hasAlpha)
    }

    fun loadNegativeColor() {
        val hexColor = _negativeColor.value?.data
        val hasAlpha = false
        _negativeColorAndHasAlpha.onNext(hexColor to hasAlpha)
    }

    fun loadTextColor() {
        val hexColor = _textColor.value?.data
        val hasAlpha = false
        _textColorAndHasAlpha.onNext(hexColor to hasAlpha)
    }

    fun loadCardColor() {
        val hexColor = _cardColor.value?.data
        val hasAlpha = true
        _cardColorAndHasAlpha.onNext(hexColor to hasAlpha)
    }

    fun loadToolbarColor() {
        val hexColor = _toolbarColor.value?.data
        val hasAlpha = true
        _toolbarColorAndHasAlpha.onNext(hexColor to hasAlpha)
    }

    fun loadBackgroundColor() {
        val hexColor = _backgroundColor.value?.data
        val hasAlpha = false
        _backgroundColorAndHasAlpha.onNext(hexColor to hasAlpha)
    }

    fun loadFloatingButtonColor() {
        val hexColor = _floatingButtonColor.value?.data
        val hasAlpha = false
        _floatingButtonColorAndHasAlpha.onNext(hexColor to hasAlpha)
    }

    fun loadFloatingIconColor() {
        val hexColor = _floatingIconColor.value?.data
        val hasAlpha = false
        _floatingIconColorAndHasAlpha.onNext(hexColor to hasAlpha)
    }
}