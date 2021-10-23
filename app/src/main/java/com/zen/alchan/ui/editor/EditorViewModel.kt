package com.zen.alchan.ui.editor

import android.text.InputType
import com.apollographql.apollo.api.CustomTypeValue
import com.zen.alchan.R
import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.data.repository.MediaListRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.FuzzyDate
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.enums.getAniListMediaType
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getString
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.helper.pojo.NullableItem
import com.zen.alchan.helper.pojo.SliderItem
import com.zen.alchan.helper.pojo.TextInputSetting
import com.zen.alchan.helper.utils.TimeUtil
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import type.MediaListStatus
import type.ScoreFormat
import java.util.*
import kotlin.collections.LinkedHashMap

class EditorViewModel(
    private val mediaListRepository: MediaListRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val _title = BehaviorSubject.createDefault("")
    val title: Observable<String>
        get() = _title

    private val _isFavorite = BehaviorSubject.createDefault(false)
    val isFavorite: Observable<Boolean>
        get() = _isFavorite

    private val _status = BehaviorSubject.createDefault(MediaListStatus.PLANNING)
    val status: Observable<MediaListStatus>
        get() = _status

    private val _score = BehaviorSubject.createDefault(0.0)
    val score: Observable<Double>
        get() = _score

    private val _advancedScores = BehaviorSubject.createDefault(NullableItem<LinkedHashMap<String, Double>>(null))
    val advancedScores: Observable<NullableItem<LinkedHashMap<String, Double>>>
        get() = _advancedScores

    private val _progress = BehaviorSubject.createDefault(0)
    val progress: Observable<Int>
        get() = _progress

    private val _progressVolume = BehaviorSubject.createDefault(0)
    val progressVolume: Observable<Int>
        get() = _progressVolume

    private val _startDate = BehaviorSubject.createDefault(NullableItem<FuzzyDate>(null))
    val startDate: Observable<NullableItem<FuzzyDate>>
        get() = _startDate

    private val _finishDate = BehaviorSubject.createDefault(NullableItem<FuzzyDate>(null))
    val finishDate: Observable<NullableItem<FuzzyDate>>
        get() = _finishDate

    private val _totalRewatches = BehaviorSubject.createDefault(0)
    val totalRewatches: Observable<Int>
        get() = _totalRewatches

    private val _notes = BehaviorSubject.createDefault("")
    val notes: Observable<String>
        get() = _notes

    private val _priority = BehaviorSubject.createDefault(0)
    val priority: Observable<Int>
        get() = _priority

    private val _customLists = BehaviorSubject.createDefault(NullableItem<LinkedHashMap<String, Boolean>>(null))
    val customLists: Observable<NullableItem<LinkedHashMap<String, Boolean>>>
        get() = _customLists

    private val _hideFromStatusLists = BehaviorSubject.createDefault(false)
    val hideFromStatusList: Observable<Boolean>
        get() = _hideFromStatusLists

    private val _isPrivate = BehaviorSubject.createDefault(false)
    val isPrivate: Observable<Boolean>
        get() = _isPrivate

    private val _customListsVisibility = BehaviorSubject.createDefault(false)
    val customListsVisibility: Observable<Boolean>
        get() = _customListsVisibility

    private val _progressVolumeVisibility = BehaviorSubject.createDefault(false)
    val progressVolumeVisibility: Observable<Boolean>
        get() = _progressVolumeVisibility

    private val _scoreTextVisibility = BehaviorSubject.createDefault(true)
    val scoreTextVisibility: Observable<Boolean>
        get() = _scoreTextVisibility

    private val _scoreSmileyVisibility = BehaviorSubject.createDefault(false)
    val scoreSmileyVisibility: Observable<Boolean>
        get() = _scoreSmileyVisibility

    private val _startDateRemoveIconVisibility = BehaviorSubject.createDefault(false)
    val startDateRemoveIconVisibility: Observable<Boolean>
        get() = _startDateRemoveIconVisibility

    private val _finishDateRemoveIconVisibility = BehaviorSubject.createDefault(false)
    val finishDateRemoveIconVisibility: Observable<Boolean>
        get() = _finishDateRemoveIconVisibility

    private val _mediaListStatuses = PublishSubject.create<List<ListItem<MediaListStatus>>>()
    val mediaListStatuses: Observable<List<ListItem<MediaListStatus>>>
        get() = _mediaListStatuses

    private val _scoreValues = PublishSubject.create<Triple<ScoreFormat, Double, LinkedHashMap<String, Double>?>>()
    val scoreValues: Observable<Triple<ScoreFormat, Double, LinkedHashMap<String, Double>?>> // scoreFormat, score, advancedScores
        get() = _scoreValues

    private val _progressValues = PublishSubject.create<Triple<Int, Int?, Boolean>>()
    val progressValues: Observable<Triple<Int, Int?, Boolean>> // progress, maxProgress, isProgressVolume
        get() = _progressValues

    private val _calendarStartDate = PublishSubject.create<Calendar>()
    val calendarStartDate: Observable<Calendar>
        get() = _calendarStartDate

    private val _calendarFinishDate = PublishSubject.create<Calendar>()
    val calendarFinishDate: Observable<Calendar>
        get() = _calendarFinishDate

    private val _rewatchesTextInputSetting = PublishSubject.create<Pair<Int, TextInputSetting>>()
    val rewatchesTextInputSetting: Observable<Pair<Int, TextInputSetting>>
        get() = _rewatchesTextInputSetting

    private val _notesTextInputSetting = PublishSubject.create<Pair<String, TextInputSetting>>()
    val notesTextInputSetting: Observable<Pair<String, TextInputSetting>>
        get() = _notesTextInputSetting

    private val _prioritySliderItem = PublishSubject.create<SliderItem>()
    val prioritySliderItem: Observable<SliderItem>
        get() = _prioritySliderItem

    private val _deleteSuccess = PublishSubject.create<Int>()
    val deleteSuccess: Observable<Int>
        get() = _deleteSuccess

    var user = User()
    var mediaType = MediaType.ANIME
    var mediaId = 0

    private var media = Media()
    private var appSetting = AppSetting()

    override fun loadData() {
        loadOnce {
            _loading.onNext(true)

            disposables.add(
                mediaListRepository.getMediaWithMediaList(mediaId, mediaType.getAniListMediaType())
                    .zipWith(userRepository.getViewer(Source.CACHE)) { media, user ->
                        return@zipWith media to user
                    }
                    .zipWith(userRepository.getAppSetting()) { mediaAndUser, appSetting ->
                        return@zipWith Triple(mediaAndUser.first, mediaAndUser.second, appSetting)
                    }
                    .applyScheduler()
                    .doFinally { _loading.onNext(false) }
                    .subscribe(
                        { (media, user, appSetting) ->
                            this.media = media
                            this.user = user
                            this.appSetting = appSetting

                            _title.onNext(media.getTitle(appSetting))
                            _isFavorite.onNext(media.isFavourite)

                            _progressVolumeVisibility.onNext(mediaType == MediaType.MANGA)
                            _scoreTextVisibility.onNext(user.mediaListOptions.scoreFormat != ScoreFormat.POINT_3)
                            _scoreSmileyVisibility.onNext(user.mediaListOptions.scoreFormat == ScoreFormat.POINT_3)

                            val mediaList = media.mediaListEntry
                            mediaList?.let {
                                updateStatus(it.status ?: MediaListStatus.PLANNING)
                                updateScore(it.score)
                                updateAdvancedScores((it.advancedScores as? CustomTypeValue<LinkedHashMap<String, Double>>)?.value)
                                updateProgress(it.progress)
                                updateProgressVolume(it.progressVolumes ?: 0)
                                updateStartDate(it.startedAt)
                                updateFinishDate(it.completedAt)
                                updateTotalRewatches(it.repeat)
                                updateNotes(it.notes)
                                updatePriority(it.priority)
                                updateShouldHideFromStatusLists(it.hiddenFromStatusLists)
                                updateIsPrivate(it.private)

                                // sort Custom Lists
                                val sectionOrder = when (mediaType) {
                                    MediaType.ANIME -> user.mediaListOptions.animeList.sectionOrder
                                    MediaType.MANGA -> user.mediaListOptions.mangaList.sectionOrder
                                }
                                val customLists = (it.customLists as? CustomTypeValue<LinkedHashMap<String, Boolean>>)?.value
                                val sortedCustomLists = LinkedHashMap<String, Boolean>()
                                sectionOrder.forEach { section ->
                                    if (customLists?.containsKey(section) == true)
                                        sortedCustomLists[section] = customLists[section]!!
                                }
                                customLists?.forEach { (key, value) ->
                                    if (!sortedCustomLists.containsKey(key))
                                        sortedCustomLists[key] = value
                                }
                                updateCustomLists(sortedCustomLists)
                                _customListsVisibility.onNext(!customLists.isNullOrEmpty())
                            }
                        },
                        {
                            _error.onNext(it.getStringResource())
                        }
                    )
            )
        }
    }

    fun saveMediaList() {
        _loading.onNext(true)

        disposables.add(
            mediaListRepository.updateMediaListEntry(
                mediaType,
                media.mediaListEntry?.id,
                media.idAniList,
                _status.value ?: MediaListStatus.PLANNING,
                _score.value ?: 0.0,
                _progress.value ?: 0,
                _progressVolume.value,
                _totalRewatches.value ?: 0,
                _priority.value ?: 0,
                _isPrivate.value ?: false,
                _notes.value ?: "",
                _hideFromStatusLists.value ?: false,
                _customLists.value?.data?.filter { it.value }?.map { it.key },
                _advancedScores.value?.data?.map { it.value },
                _startDate.value?.data,
                _finishDate.value?.data
            )
                .applyScheduler()
                .doAfterNext { _loading.onNext(false) }
                .subscribe(
                    {
                        _success.onNext(R.string.save_changes)
                    },
                    {
                        _error.onNext(it.getStringResource())
                    }
                )
        )
    }

    fun updateIsFavorite() {
        _loading.onNext(true)
        disposables.add(
            mediaListRepository.toggleFavorite(
                animeId = if (mediaType == MediaType.ANIME) mediaId else null,
                mangaId = if (mediaType == MediaType.MANGA) mediaId else null
            )
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

    fun deleteMediaList() {
        media.mediaListEntry?.id?.let { id ->
            _loading.onNext(true)

            disposables.add(
                mediaListRepository.deleteMediaListEntry(mediaType, id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally {
                        _loading.onNext(false)
                    }
                    .subscribe(
                        {
                            _deleteSuccess.onNext(R.string.entry_removed)
                        },
                        {
                            _error.onNext(it.getStringResource())
                        }
                    )
            )
        }

    }

    fun updateStatus(newStatus: MediaListStatus) {
        _status.onNext(newStatus)
    }

    fun updateScore(newScore: Double) {
        _score.onNext(newScore)
    }

    fun updateAdvancedScores(newAdvancedScores: LinkedHashMap<String, Double>?) {
        _advancedScores.onNext(NullableItem(newAdvancedScores))
    }

    fun updateProgress(newProgress: Int) {
        _progress.onNext(newProgress)
    }

    fun updateProgressVolume(newProgressVolume: Int) {
        _progressVolume.onNext(newProgressVolume)
    }

    fun updateStartDate(newStartDate: FuzzyDate?) {
        _startDate.onNext(NullableItem(newStartDate))
        _startDateRemoveIconVisibility.onNext(newStartDate?.isNull() == false)
    }

    fun updateFinishDate(newFinishDate: FuzzyDate?) {
        _finishDate.onNext(NullableItem(newFinishDate))
        _finishDateRemoveIconVisibility.onNext(newFinishDate?.isNull() == false)
    }

    fun updateTotalRewatches(newTotalRewatches: Int) {
        _totalRewatches.onNext(newTotalRewatches)
    }

    fun updateNotes(newNotes: String) {
        _notes.onNext(newNotes)
    }

    fun updatePriority(newPriority: Int) {
        _priority.onNext(newPriority)
    }

    fun updateCustomLists(newCustomLists: LinkedHashMap<String, Boolean>?) {
        _customLists.onNext(NullableItem(newCustomLists))
    }

    fun updateCustomList(newCustomList: Pair<String, Boolean>) {
        val currentCustomLists = _customLists.value?.data
        currentCustomLists?.let {
            it[newCustomList.first] = newCustomList.second
        }
        _customLists.onNext(NullableItem(currentCustomLists))
    }

    fun updateShouldHideFromStatusLists(shouldHideFromStatusLists: Boolean) {
        _hideFromStatusLists.onNext(shouldHideFromStatusLists)
    }

    fun updateIsPrivate(shouldPrivate: Boolean) {
        _isPrivate.onNext(shouldPrivate)
    }

    fun loadMediaListStatuses() {
        val mediaListStatusList = listOf(
            MediaListStatus.CURRENT,
            MediaListStatus.REPEATING,
            MediaListStatus.COMPLETED,
            MediaListStatus.PAUSED,
            MediaListStatus.DROPPED,
            MediaListStatus.PLANNING
        )

        _mediaListStatuses.onNext(
            mediaListStatusList.map { ListItem(it.getString(mediaType), it) }
        )
    }

    fun loadScoreValues() {
        val scoreFormat = user.mediaListOptions.scoreFormat ?: ScoreFormat.POINT_100
        val currentScore = _score.value ?: 0.0
        val advancedScores = _advancedScores.value?.data
        _scoreValues.onNext(Triple(scoreFormat, currentScore, advancedScores))
    }

    fun loadProgressValues(isProgressVolume: Boolean) {
        val currentProgress = if (isProgressVolume)
            _progressVolume.value ?: 0
        else
            _progress.value ?: 0

        val maxProgress = when (mediaType) {
            MediaType.ANIME -> media.episodes
            MediaType.MANGA -> if (isProgressVolume) media.volumes else media.chapters
        }

        _progressValues.onNext(Triple(currentProgress, maxProgress, isProgressVolume))
    }

    fun loadCalendarStartDate() {
        val startDate = _startDate.value?.data
        val calendar = Calendar.getInstance()

        if (startDate?.year != null)
            calendar.set(Calendar.YEAR, startDate.year)

        if (startDate?.month != null)
            calendar.set(Calendar.MONTH, startDate.month - 1)

        if (startDate?.day != null)
            calendar.set(Calendar.DAY_OF_MONTH, startDate.day)

        _calendarStartDate.onNext(calendar)
    }

    fun loadCalendarFinishDate() {
        val finishDate = _finishDate.value?.data
        val calendar = Calendar.getInstance()

        if (finishDate?.year != null)
            calendar.set(Calendar.YEAR, finishDate.year)

        if (finishDate?.month != null)
            calendar.set(Calendar.MONTH, finishDate.month - 1)

        if (finishDate?.day != null)
            calendar.set(Calendar.DAY_OF_MONTH, finishDate.day)

        _calendarFinishDate.onNext(calendar)
    }

    fun loadRewatchesTextInputSetting() {
        val currentRewatches = _totalRewatches.value ?: 0
        val textInputSetting = TextInputSetting(
            inputType = InputType.TYPE_CLASS_NUMBER,
            singleLine = true,
            characterLimit = 5
        )
        _rewatchesTextInputSetting.onNext(currentRewatches to textInputSetting)
    }

    fun loadNotesTextInputSetting() {
        val currentNotes = _notes.value ?: ""
        val textInputSetting = TextInputSetting(
            inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES,
            singleLine = false,
            characterLimit = 6000
        )
        _notesTextInputSetting.onNext(currentNotes to textInputSetting)
    }

    fun loadPrioritySliderItem() {
        _prioritySliderItem.onNext(
            SliderItem(
                0,
                5,
                _priority.value,
                null
            )
        )
    }
}