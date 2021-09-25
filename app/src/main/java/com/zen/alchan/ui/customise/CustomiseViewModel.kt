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

    private val _longPressViewDetail = BehaviorSubject.createDefault(true)
    val longPressViewDetail: Observable<Boolean>
        get() = _longPressViewDetail

    private val _hideMediaFormat = BehaviorSubject.createDefault(false)
    val hideMediaFormat: Observable<Boolean>
        get() = _hideMediaFormat

    private val _hideScore = BehaviorSubject.createDefault(false)
    val hideScore: Observable<Boolean>
        get() = _hideScore

    private val _hideScoreVolumeProgressForManga = BehaviorSubject.createDefault(false)
    val hideScoreVolumeProgressForManga: Observable<Boolean>
        get() = _hideScoreVolumeProgressForManga

    private val _hideScoreChapterProgressForManga = BehaviorSubject.createDefault(false)
    val hideScoreChapterProgressForManga: Observable<Boolean>
        get() = _hideScoreChapterProgressForManga

    private val _hideScoreVolumeProgressForNovel = BehaviorSubject.createDefault(false)
    val hideScoreVolumeProgressForNovel: Observable<Boolean>
        get() = _hideScoreVolumeProgressForNovel

    private val _hideScoreChapterProgressForNovel = BehaviorSubject.createDefault(false)
    val hideScoreChapterProgressForNovel: Observable<Boolean>
        get() = _hideScoreChapterProgressForNovel

    private val _hideAiring = BehaviorSubject.createDefault(false)
    val hideAiring: Observable<Boolean>
        get() = _hideAiring

    private val _showNotes = BehaviorSubject.createDefault(false)
    val showNotes: Observable<Boolean>
        get() = _showNotes

    private val _showPriority = BehaviorSubject.createDefault(false)
    val showPriority: Observable<Boolean>
        get() = _showPriority

    private val _primaryColor = BehaviorSubject.createDefault(NullableItem<String?>())
    val primaryColor: Observable<NullableItem<String?>>
        get() = _primaryColor

    private val _secondaryColor = BehaviorSubject.createDefault(NullableItem<String?>())
    val secondaryColor: Observable<NullableItem<String?>>
        get() = _secondaryColor

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

    private val _hideMediaFormatVisibility = BehaviorSubject.createDefault(false)
    val hideMediaFormatVisibility: Observable<Boolean>
        get() = _hideMediaFormatVisibility

    private val _progressVisibility = BehaviorSubject.createDefault(false)
    val progressVisibility: Observable<Boolean>
        get() = _progressVisibility

    private val _airingVisibility = BehaviorSubject.createDefault(false)
    val airingVisibility: Observable<Boolean>
        get() = _airingVisibility

    private val _showNotesVisibility = BehaviorSubject.createDefault(false)
    val showNotesVisibility: Observable<Boolean>
        get() = _showNotesVisibility

    private val _listTypes = PublishSubject.create<List<ListItem<ListType>>>()
    val listTypes: Observable<List<ListItem<ListType>>>
        get() = _listTypes

    private val _primaryColorAndHasAlpha = PublishSubject.create<Pair<String?, Boolean>>()
    val primaryColorAndHasAlpha: Observable<Pair<String?, Boolean>>
        get() = _primaryColorAndHasAlpha

    private val _secondaryColorAndHasAlpha = PublishSubject.create<Pair<String?, Boolean>>()
    val secondaryColorAndHasAlpha: Observable<Pair<String?, Boolean>>
        get() = _secondaryColorAndHasAlpha

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

                        _progressVisibility.onNext(mediaType == MediaType.MANGA)
                        _airingVisibility.onNext(mediaType == MediaType.ANIME)

                        listStyle.apply {
                            updateListType(listType)
                            updateLongPressViewDetail(longPressShowDetail)
                            updateHideMediaFormat(hideMediaFormat)
                            updateHideScore(hideScoreWhenNotScored)
                            updateHideVolumeProgressForManga(hideVolumeForManga)
                            updateHideChapterProgressForManga(hideChapterForManga)
                            updateHideVolumeProgressForNovel(hideVolumeForNovel)
                            updateHideChapterProgressForNovel(hideChapterForNovel)
                            updateHideAiring(hideAiring)
                            updateShowNotes(showNotes)
                            updateShowPriority(showPriority)
                            updatePrimaryColor(primaryColor)
                            updateSecondaryColor(secondaryColor)
                            updateTextColor(textColor)
                            updateCardColor(cardColor)
                            updateToolbarColor(toolbarColor)
                            updateBackgroundColor(backgroundColor)
                            updateFloatingButtonColor(floatingButtonColor)
                            updateFloatingIconColor(floatingIconColor)
                        }
                    }
            )
        }
    }

    fun saveCurrentListStyle() {
        userRepository.setListStyle(mediaType, currentListStyle)
        _listStyle.onNext(currentListStyle)
    }

    fun resetCurrentListStyle() {
        currentListStyle = ListStyle(currentListStyle.listType)
        userRepository.setListStyle(mediaType, currentListStyle)
        _listStyle.onNext(currentListStyle)
    }

    fun updateListType(newListType: ListType) {
        currentListStyle.listType = newListType
        _listType.onNext(newListType)

        _hideMediaFormatVisibility.onNext(newListType == ListType.GRID)
        _showNotesVisibility.onNext(newListType != ListType.SIMPLIFIED && newListType != ListType.ALBUM)
    }

    fun updateLongPressViewDetail(shouldLongPressShowDetail: Boolean) {
        currentListStyle.longPressShowDetail = shouldLongPressShowDetail
        _longPressViewDetail.onNext(shouldLongPressShowDetail)
    }

    fun updateHideMediaFormat(shouldHideMediaFormat: Boolean) {
        currentListStyle.hideMediaFormat = shouldHideMediaFormat
        _hideMediaFormat.onNext(shouldHideMediaFormat)
    }

    fun updateHideScore(shouldHideScore: Boolean) {
        currentListStyle.hideScoreWhenNotScored = shouldHideScore
        _hideScore.onNext(shouldHideScore)
    }

    fun updateHideVolumeProgressForManga(shouldHideVolumeProgressForManga: Boolean) {
        currentListStyle.hideVolumeForManga = shouldHideVolumeProgressForManga
        _hideScoreVolumeProgressForManga.onNext(shouldHideVolumeProgressForManga)
    }

    fun updateHideChapterProgressForManga(shouldHideChapterProgressForManga: Boolean) {
        currentListStyle.hideChapterForManga = shouldHideChapterProgressForManga
        _hideScoreChapterProgressForManga.onNext(shouldHideChapterProgressForManga)
    }

    fun updateHideVolumeProgressForNovel(shouldHideVolumeProgressForNovel: Boolean) {
        currentListStyle.hideVolumeForNovel = shouldHideVolumeProgressForNovel
        _hideScoreVolumeProgressForNovel.onNext(shouldHideVolumeProgressForNovel)
    }

    fun updateHideChapterProgressForNovel(shouldHideChapterProgressForNovel: Boolean) {
        currentListStyle.hideChapterForNovel = shouldHideChapterProgressForNovel
        _hideScoreChapterProgressForNovel.onNext(shouldHideChapterProgressForNovel)
    }

    fun updateHideAiring(shouldHideAiring: Boolean) {
        currentListStyle.hideAiring = shouldHideAiring
        _hideAiring.onNext(shouldHideAiring)
    }

    fun updateShowNotes(shouldShowNotes: Boolean) {
        currentListStyle.showNotes = shouldShowNotes
        _showNotes.onNext(shouldShowNotes)
    }

    fun updateShowPriority(shouldShowPriority: Boolean) {
        currentListStyle.showPriority = shouldShowPriority
        _showPriority.onNext(shouldShowPriority)
    }

    fun updatePrimaryColor(newPrimaryColor: String?) {
        currentListStyle.primaryColor = newPrimaryColor
        _primaryColor.onNext(NullableItem(newPrimaryColor))
    }

    fun updateSecondaryColor(newSecondaryColor: String?) {
        currentListStyle.secondaryColor = newSecondaryColor
        _secondaryColor.onNext(NullableItem(newSecondaryColor))
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