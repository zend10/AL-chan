package com.zen.alchan.ui.editor

import android.text.InputType
import com.apollographql.apollo.api.CustomTypeValue
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
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import type.MediaListStatus
import type.ScoreFormat

class EditorViewModel(
    private val mediaListRepository: MediaListRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    private var _title = BehaviorSubject.createDefault("")
    val title: Observable<String>
        get() = _title

    private var _isFavorite = BehaviorSubject.createDefault(false)
    val isFavorite: Observable<Boolean>
        get() = _isFavorite

    private var _status = BehaviorSubject.createDefault(MediaListStatus.PLANNING)
    val status: Observable<MediaListStatus>
        get() = _status

    private var _score = BehaviorSubject.createDefault(0.0)
    val score: Observable<Double>
        get() = _score

    private var _advancedScores = BehaviorSubject.createDefault(NullableItem<LinkedHashMap<String, Double>>(null))
    val advancedScores: Observable<NullableItem<LinkedHashMap<String, Double>>>
        get() = _advancedScores

    private var _progress = BehaviorSubject.createDefault(0)
    val progress: Observable<Int>
        get() = _progress

    private var _progressVolume = BehaviorSubject.createDefault(0)
    val progressVolume: Observable<Int>
        get() = _progressVolume

    private var _startDate = BehaviorSubject.createDefault(NullableItem<FuzzyDate>(null))
    val startDate: Observable<NullableItem<FuzzyDate>>
        get() = _startDate

    private var _finishDate = BehaviorSubject.createDefault(NullableItem<FuzzyDate>(null))
    val finishDate: Observable<NullableItem<FuzzyDate>>
        get() = _finishDate

    private var _totalRewatches = BehaviorSubject.createDefault(0)
    val totalRewatches: Observable<Int>
        get() = _totalRewatches

    private var _notes = BehaviorSubject.createDefault("")
    val notes: Observable<String>
        get() = _notes

    private var _priority = BehaviorSubject.createDefault(0)
    val priority: Observable<Int>
        get() = _priority

    private var _hideFromStatusLists = BehaviorSubject.createDefault(false)
    val hideFromStatusList: Observable<Boolean>
        get() = _hideFromStatusLists

    private var _isPrivate = BehaviorSubject.createDefault(false)
    val isPrivate: Observable<Boolean>
        get() = _isPrivate



    private var _progressVolumeVisibility = BehaviorSubject.createDefault(false)
    val progressVolumeVisibility: Observable<Boolean>
        get() = _progressVolumeVisibility

    private var _scoreTextVisibility = BehaviorSubject.createDefault(true)
    val scoreTextVisibility: Observable<Boolean>
        get() = _scoreTextVisibility

    private var _scoreSmileyVisibility = BehaviorSubject.createDefault(false)
    val scoreSmileyVisibility: Observable<Boolean>
        get() = _scoreSmileyVisibility


    private var _mediaListStatuses = PublishSubject.create<List<ListItem<MediaListStatus>>>()
    val mediaListStatuses: Observable<List<ListItem<MediaListStatus>>>
        get() = _mediaListStatuses

    private var _scoreValues = PublishSubject.create<Triple<ScoreFormat, Double, LinkedHashMap<String, Double>?>>()
    val scoreValues: Observable<Triple<ScoreFormat, Double, LinkedHashMap<String, Double>?>> // scoreFormat, score, advancedScores
        get() = _scoreValues

    private var _progressValues = PublishSubject.create<Triple<Int, Int?, Boolean>>()
    val progressValues: Observable<Triple<Int, Int?, Boolean>> // progress, maxProgress, isProgressVolume
        get() = _progressValues

    private var _rewatchesTextInputSetting = PublishSubject.create<Pair<Int, TextInputSetting>>()
    val rewatchesTextInputSetting: Observable<Pair<Int, TextInputSetting>>
        get() = _rewatchesTextInputSetting

    private var _notesTextInputSetting = PublishSubject.create<Pair<String, TextInputSetting>>()
    val notesTextInputSetting: Observable<Pair<String, TextInputSetting>>
        get() = _notesTextInputSetting

    private var _prioritySliderItem = PublishSubject.create<SliderItem>()
    val prioritySliderItem: Observable<SliderItem>
        get() = _prioritySliderItem

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
                            updateIsFavorite(media.isFavourite)

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
                            }
                        },
                        {
                            _error.onNext(it.getStringResource())
                        }
                    )
            )
        }
    }

    fun updateIsFavorite(isFavorite: Boolean) {
        _isFavorite.onNext(isFavorite)
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
    }

    fun updateFinishDate(newFinishDate: FuzzyDate?) {
        _finishDate.onNext(NullableItem(newFinishDate))
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