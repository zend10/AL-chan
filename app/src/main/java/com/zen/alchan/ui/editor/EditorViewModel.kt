package com.zen.alchan.ui.editor

import com.zen.alchan.data.response.anilist.FuzzyDate
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.pojo.NullableItem
import com.zen.alchan.helper.pojo.SliderItem
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import type.MediaListStatus

class EditorViewModel : BaseViewModel() {

    private var _isFavorite = BehaviorSubject.createDefault(false)
    val isFavorite: Observable<Boolean>
        get() = _isFavorite

    private var _status = BehaviorSubject.createDefault(MediaListStatus.PLANNING)
    val status: Observable<MediaListStatus>
        get() = _status

    private var _score = BehaviorSubject.createDefault(0.0)
    val score: Observable<Double>
        get() = _score

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


    private var _prioritySliderItem = PublishSubject.create<SliderItem>()
    val prioritySliderItem: Observable<SliderItem>
        get() = _prioritySliderItem

    var mediaType = MediaType.ANIME
    var mediaId = 0

    override fun loadData() {
        loadOnce {

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

    fun updateProgress(newProgress: Int) {
        _progress.onNext(newProgress)
    }

    fun updateProgressVolume(newProgressVolume: Int) {
        _progressVolume.onNext(newProgressVolume)
    }

    fun updateStartDate(newStartDate: FuzzyDate) {
        _startDate.onNext(NullableItem(newStartDate))
    }

    fun updateFinishDate(newFinishDate: FuzzyDate) {
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