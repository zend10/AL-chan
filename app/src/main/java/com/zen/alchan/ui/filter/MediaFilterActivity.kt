package com.zen.alchan.ui.filter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.zen.alchan.R
import com.zen.alchan.helper.*
import com.zen.alchan.helper.pojo.FilterRange
import com.zen.alchan.helper.pojo.MediaFilterData
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.ui.base.BaseActivity
import com.zen.alchan.ui.common.filter.MediaFilterRvAdapter
import kotlinx.android.synthetic.main.activity_media_filter.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaSort
import type.MediaType
import type.ScoreFormat
import java.util.*

class MediaFilterActivity : BaseActivity() {

    private val viewModel by viewModel<MediaFilterViewModel>()

    companion object {
        const val MEDIA_TYPE = "mediaType"
        const val IS_EXPLORE = "isExplore"
        const val FILTER_DATA = "filterData"
        const val SCORE_FORMAT = "scoreFormat"

        // use this as request code if you want to do something with the filter
        const val ACTIVITY_FILTER = 100

        private const val LIST_INCLUDE_GENRE = 1
        private const val LIST_EXCLUDE_GENRE = 2
        private const val LIST_INCLUDE_TAG = 3
        private const val LIST_EXCLUDE_TAG = 4
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_filter)

        changeStatusBarColor(AndroidUtility.getResValueFromRefAttr(this, R.attr.themeCardColor))

        filterLayout.doOnApplyWindowInsets { view, windowInsets, initialPadding ->
            view.updateTopPadding(windowInsets, initialPadding)
            view.updateSidePadding(windowInsets, initialPadding)
        }

        filterFormLayout.doOnApplyWindowInsets { view, windowInsets, initialPadding ->
            view.updateBottomPadding(windowInsets, initialPadding)
        }

        viewModel.mediaType = MediaType.valueOf(intent.getStringExtra(MEDIA_TYPE)!!)
        viewModel.isExplore = intent.getBooleanExtra(IS_EXPLORE, false)
        viewModel.filterData = viewModel.gson.fromJson(intent.getStringExtra(FILTER_DATA), MediaFilterData::class.java)
        viewModel.scoreFormat = ScoreFormat.valueOf(intent.getStringExtra(SCORE_FORMAT) ?: ScoreFormat.POINT_100.name)

        setSupportActionBar(toolbarLayout)
        supportActionBar?.apply {
            title = getString(R.string.filter)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_delete)
        }

        setupObserver()
        initLayout()
    }

    private fun setupObserver() {

    }

    private fun initLayout() {
        // sort will never be null after being initialised for the first time
        // that's why I'm checking current data through selectedMediaListSort
        if (viewModel.currentData.selectedMediaListSort == null) {
            if (viewModel.filterData != null) {
                viewModel.currentData = viewModel.filterData!!
            } else {
                viewModel.currentData.selectedMediaListSort = viewModel.defaultSort
                viewModel.currentData.selectedMediaListOrderByDescending = viewModel.defaultOrderByDescending
                viewModel.currentData.selectedMediaSort = MediaSort.POPULARITY_DESC
            }
        }

        handleSortLayout()
        handleFilterFormatLayout()
        handleFilterStatusLayout()
        handleFilterSourceLayout()
        handleFilterCountryLayout()
        handleFilterSeasonLayout()
        handleFilterYearLayout()
        handleGenreLayout()
        handleTagLayout()
        handleFilterMinimumTagRankLayout()
        handleFilterLicensedLayout()
        handleFilterEpisodeCountLayout()
        handleFilterDurationLayout()
        handleFilterAverageScoreLayout()
        handleFilterPopularityLayout()
        handleExtraOptionsLayout()
        handleUserListFilterLayout()
    }

    private fun handleSortLayout() {
        if (viewModel.isExplore) {
            sortByText.text = if (viewModel.mediaSortList.indexOf(viewModel.currentData.selectedMediaSort) != -1) {
                getString(viewModel.mediaSortArray[viewModel.mediaSortList.indexOf(viewModel.currentData.selectedMediaSort)]).toUpperCase(Locale.US)
            } else {
                "-"
            }
            orderByLayout.visibility = View.GONE
        } else {
            sortByText.text = viewModel.currentData.selectedMediaListSort?.name?.replaceUnderscore() ?: "-"
            orderByText.text = getString(if (viewModel.currentData.selectedMediaListOrderByDescending == true) {
                R.string.descending
            } else {
                R.string.ascending
            }).toUpperCase(Locale.US)
            orderByLayout.visibility = View.VISIBLE
        }

        sortByLayout.setOnClickListener {
            if (viewModel.isExplore) {
                val stringArray = viewModel.mediaSortArray.map { sort -> getString(sort).toUpperCase(Locale.US) }.toTypedArray()
                AlertDialog.Builder(this)
                    .setItems(stringArray) { _, which ->
                        viewModel.currentData.selectedMediaSort = viewModel.mediaSortList[which]
                        sortByText.text = stringArray[which]
                    }
                    .show()
            } else {
                val stringArray = viewModel.mediaListSortList.map { sort -> sort.name.replaceUnderscore().toUpperCase(Locale.US) }.toTypedArray()
                AlertDialog.Builder(this)
                    .setItems(stringArray) { _, which ->
                        viewModel.currentData.selectedMediaListSort = viewModel.mediaListSortList[which]
                        sortByText.text = stringArray[which]
                    }
                    .show()
            }
        }

        orderByLayout.setOnClickListener {
            val stringArray = viewModel.orderByList.map { order -> getString(order).toUpperCase(Locale.US) }.toTypedArray()
            AlertDialog.Builder(this)
                .setItems(stringArray) { _, which ->
                    // 0 is index for ascending
                    viewModel.currentData.selectedMediaListOrderByDescending = which != 0
                    orderByText.text = stringArray[which]
                }
                .show()
        }
    }

    private fun handleFilterFormatLayout() {
        filterFormatText.text = if (viewModel.currentData.selectedFormats.isNullOrEmpty()) {
            "-"
        } else {
            viewModel.currentData.selectedFormats?.joinToString { it.name.replaceUnderscore() }
        }

        filterFormatLayout.setOnClickListener {
            val stringBooleanPair = viewModel.getMediaFormatArrayPair()
            AlertDialog.Builder(this)
                .setMultiChoiceItems(stringBooleanPair.first, stringBooleanPair.second) { _, index, isChecked ->
                    viewModel.passMediaFormatFilterValue(index, isChecked)
                    filterFormatText.text = if (viewModel.currentData.selectedFormats.isNullOrEmpty()) {
                        "-"
                    } else {
                        viewModel.currentData.selectedFormats?.joinToString { it.name.replaceUnderscore() }
                    }
                }
                .setPositiveButton(R.string.close, null)
                .show()
        }
    }

    private fun handleFilterStatusLayout() {
        filterStatusText.text = if (viewModel.currentData.selectedStatuses.isNullOrEmpty()) {
            "-"
        } else {
            viewModel.currentData.selectedStatuses?.joinToString { it.name.replaceUnderscore() } ?: "-"
        }

        filterStatusLayout.setOnClickListener {
            val stringBooleanPair = viewModel.getMediaStatusArrayPair()
            AlertDialog.Builder(this)
                .setMultiChoiceItems(stringBooleanPair.first, stringBooleanPair.second) { _, index, isChecked ->
                    viewModel.passMediaStatusFilterValue(index, isChecked)
                    filterStatusText.text = if (viewModel.currentData.selectedStatuses.isNullOrEmpty()) {
                        "-"
                    } else {
                        viewModel.currentData.selectedStatuses?.joinToString { it.name.replaceUnderscore() }
                    }
                }
                .setPositiveButton(R.string.close, null)
                .show()
        }
    }

    private fun handleFilterSourceLayout() {
        filterSourceText.text = if (viewModel.currentData.selectedSources.isNullOrEmpty()) {
            "-"
        } else {
            viewModel.currentData.selectedSources?.joinToString { it.name.replaceUnderscore() }
        }

        filterSourceLayout.setOnClickListener {
            val stringBooleanPair = viewModel.getMediaSourceArrayPair()
            AlertDialog.Builder(this)
                .setMultiChoiceItems(stringBooleanPair.first, stringBooleanPair.second) { _, index, isChecked ->
                    viewModel.passMediaSourceFilterValue(index, isChecked)
                    filterSourceText.text = if (viewModel.currentData.selectedSources.isNullOrEmpty()) {
                        "-"
                    } else {
                        viewModel.currentData.selectedSources?.joinToString { it.name.replaceUnderscore() }
                    }
                }
                .setPositiveButton(R.string.close, null)
                .show()
        }
    }

    private fun handleFilterCountryLayout() {
        filterCountryText.text = viewModel.currentData.selectedCountry?.value ?: "-"

        filterCountryLayout.setOnClickListener {
            val stringArray = viewModel.getMediaCountryStringArray()
            AlertDialog.Builder(this)
                .setItems(stringArray) { _, which ->
                    viewModel.currentData.selectedCountry = viewModel.mediaCountryList[which]
                    filterCountryText.text = stringArray[which]
                }
                .show()
        }
    }

    private fun handleFilterSeasonLayout() {
        if (viewModel.mediaType == MediaType.ANIME) {
            filterSeasonLayout.visibility = View.VISIBLE
        } else {
            filterSeasonLayout.visibility = View.GONE
            return
        }

        filterSeasonText.text = viewModel.currentData.selectedSeason?.name ?: "-"

        filterSeasonLayout.setOnClickListener {
            val stringArray = viewModel.getMediaSeasonStringArray()
            AlertDialog.Builder(this)
                .setItems(stringArray) { _, which ->
                    viewModel.currentData.selectedSeason = viewModel.mediaSeasonList[which]
                    filterSeasonText.text = stringArray[which]
                }
                .show()
        }
    }

    private fun handleFilterYearLayout() {
        filterYearRangeSeekBar.setMaxValue(viewModel.yearSeekBarMaxValue)
        filterYearRangeSeekBar.setMinValue(0F)
        filterYearRangeSeekBar.apply()

        val maxYear = viewModel.currentData.selectedYear?.maxValue?.toString()?.substring(0, 4)?.toInt()
        val minYear = viewModel.currentData.selectedYear?.minValue?.toString()?.substring(0, 4)?.toInt()

        if (minYear != null && maxYear != null) {
            filterYearRangeSeekBar.setMaxStartValue((maxYear - Constant.FILTER_EARLIEST_YEAR).toFloat())

            // weird bug in this seekbar library, can't have the minimum value and maximum value set to highest value by code
            // the bug will give us 0 for both minimum value and maximum value instead
            // so here is the workaround
            // will be applied to all seekbar below
            if ((minYear - Constant.FILTER_EARLIEST_YEAR).toFloat() == viewModel.yearSeekBarMaxValue) {
                filterYearRangeSeekBar.setMinStartValue((minYear - Constant.FILTER_EARLIEST_YEAR).toFloat() - 0.1F)
            } else {
                filterYearRangeSeekBar.setMinStartValue((minYear - Constant.FILTER_EARLIEST_YEAR).toFloat())
            }
            filterYearRangeSeekBar.apply()
            filterEndYearText.text = maxYear.toString()
            filterStartYearText.text = minYear.toString()
        } else {
            filterYearRangeSeekBar.setMaxStartValue(viewModel.yearSeekBarMaxValue)
            filterYearRangeSeekBar.setMinStartValue(0F)
            filterYearRangeSeekBar.apply()
            filterEndYearText.text = "-"
            filterStartYearText.text = "-"
        }

        filterYearRangeSeekBar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            if (filterYearRangeSeekBar.isVisible) {
                if (minValue.toInt() == 0 && maxValue.toInt() == viewModel.yearSeekBarMaxValue.toInt()) {
                    filterEndYearText.text = "-"
                    filterStartYearText.text = "-"
                    viewModel.currentData.selectedYear = null
                } else {
                    val newMaxYear = maxValue.toInt() + Constant.FILTER_EARLIEST_YEAR
                    val newMinYear = minValue.toInt() + Constant.FILTER_EARLIEST_YEAR
                    filterEndYearText.text = newMaxYear.toString()
                    filterStartYearText.text = newMinYear.toString()
                    viewModel.currentData.selectedYear = viewModel.getFilterRangeForFuzzyDateInt(newMinYear, newMaxYear)
                }
            }
        }
    }

    private fun handleGenreLayout() {
        handleGenreVisibility()
        filterIncludeGenreRecyclerView.adapter = assignGenreAndTagAdapter(viewModel.currentData.selectedGenres, LIST_INCLUDE_GENRE)
        filterExcludeGenreRecyclerView.adapter = assignGenreAndTagAdapter(viewModel.currentData.selectedExcludedGenres, LIST_EXCLUDE_GENRE)

        filterIncludeGenreText.setOnClickListener {
            val stringBooleanPair = viewModel.getMediaGenreArrayPair()
            AlertDialog.Builder(this)
                .setMultiChoiceItems(stringBooleanPair.first, stringBooleanPair.second) { _, index, isChecked ->
                    viewModel.passMediaGenreFilterValue(index, isChecked)
                    handleGenreVisibility()
                    filterIncludeGenreRecyclerView.adapter = assignGenreAndTagAdapter(viewModel.currentData.selectedGenres, LIST_INCLUDE_GENRE)
                    filterExcludeGenreRecyclerView.adapter = assignGenreAndTagAdapter(viewModel.currentData.selectedExcludedGenres, LIST_EXCLUDE_GENRE)
                }
                .setPositiveButton(R.string.close, null)
                .show()
        }

        filterExcludeGenreText.setOnClickListener {
            val stringBooleanPair = viewModel.getMediaExcludedGenreArrayPair()
            AlertDialog.Builder(this)
                .setMultiChoiceItems(stringBooleanPair.first, stringBooleanPair.second) { _, index, isChecked ->
                    viewModel.passMediaExcludedGenreFilterValue(index, isChecked)
                    handleGenreVisibility()
                    filterIncludeGenreRecyclerView.adapter = assignGenreAndTagAdapter(viewModel.currentData.selectedGenres, LIST_INCLUDE_GENRE)
                    filterExcludeGenreRecyclerView.adapter = assignGenreAndTagAdapter(viewModel.currentData.selectedExcludedGenres, LIST_EXCLUDE_GENRE)
                }
                .setPositiveButton(R.string.close, null)
                .show()
        }
    }

    private fun handleGenreVisibility() {
        if (viewModel.currentData.selectedGenres.isNullOrEmpty()) {
            filterIncludeGenreLayout.visibility = View.GONE
        } else {
            filterIncludeGenreLayout.visibility = View.VISIBLE
        }

        if (viewModel.currentData.selectedExcludedGenres.isNullOrEmpty()) {
            filterExcludeGenreLayout.visibility = View.GONE
        } else {
            filterExcludeGenreLayout.visibility = View.VISIBLE
        }

        if (viewModel.currentData.selectedGenres.isNullOrEmpty() && viewModel.currentData.selectedExcludedGenres.isNullOrEmpty()) {
            filterGenreNoItemText.visibility = View.VISIBLE
        } else {
            filterGenreNoItemText.visibility = View.GONE
        }
    }

    private fun handleTagLayout() {
        handleTagVisibility()
        filterIncludeTagRecyclerView.adapter = assignGenreAndTagAdapter(viewModel.currentData.selectedTagNames, LIST_INCLUDE_TAG)
        filterExcludeTagRecyclerView.adapter = assignGenreAndTagAdapter(viewModel.currentData.selectedExcludedTagNames, LIST_EXCLUDE_TAG)

        filterIncludeTagText.setOnClickListener {
            val dialog = MediaFilterTagDialog()
            dialog.setListener(object : MediaFilterTagListener {
                override fun passSelectedTag(name: String) {
                    viewModel.passMediaTagFilterValue(name)
                    handleTagVisibility()
                    filterIncludeTagRecyclerView.adapter = assignGenreAndTagAdapter(viewModel.currentData.selectedTagNames, LIST_INCLUDE_TAG)
                    filterExcludeTagRecyclerView.adapter = assignGenreAndTagAdapter(viewModel.currentData.selectedExcludedTagNames, LIST_EXCLUDE_TAG)
                }
            })
            if (!viewModel.currentData.selectedTagNames.isNullOrEmpty()) {
                val bundle = Bundle()
                bundle.putStringArrayList(MediaFilterTagDialog.SELECTED_TAGS, viewModel.currentData.selectedTagNames)
                dialog.arguments = bundle
            }
            dialog.show(supportFragmentManager, null)
        }

        filterExcludeTagText.setOnClickListener {
            val dialog = MediaFilterTagDialog()
            dialog.setListener(object : MediaFilterTagListener {
                override fun passSelectedTag(name: String) {
                    viewModel.passMediaExcludedTagFilterValue(name)
                    handleTagVisibility()
                    filterIncludeTagRecyclerView.adapter = assignGenreAndTagAdapter(viewModel.currentData.selectedTagNames, LIST_INCLUDE_TAG)
                    filterExcludeTagRecyclerView.adapter = assignGenreAndTagAdapter(viewModel.currentData.selectedExcludedTagNames, LIST_EXCLUDE_TAG)
                }
            })
            if (!viewModel.currentData.selectedExcludedTagNames.isNullOrEmpty()) {
                val bundle = Bundle()
                bundle.putStringArrayList(MediaFilterTagDialog.SELECTED_TAGS, viewModel.currentData.selectedExcludedTagNames)
                dialog.arguments = bundle
            }
            dialog.show(supportFragmentManager, null)
        }
    }

    private fun handleTagVisibility() {
        if (viewModel.currentData.selectedTagNames.isNullOrEmpty()) {
            filterIncludeTagLayout.visibility = View.GONE
        } else {
            filterIncludeTagLayout.visibility = View.VISIBLE
        }

        if (viewModel.currentData.selectedExcludedTagNames.isNullOrEmpty()) {
            filterExcludeTagLayout.visibility = View.GONE
        } else {
            filterExcludeTagLayout.visibility = View.VISIBLE
        }

        if (viewModel.currentData.selectedTagNames.isNullOrEmpty() && viewModel.currentData.selectedExcludedTagNames.isNullOrEmpty()) {
            filterTagNoItemText.visibility = View.VISIBLE
        } else {
            filterTagNoItemText.visibility = View.GONE
        }
    }

    private fun assignGenreAndTagAdapter(list: List<String?>?, code: Int): MediaFilterRvAdapter {
        return MediaFilterRvAdapter(list ?: listOf(), code, object : MediaFilterRvAdapter.MediaFilterListListener {
            override fun deleteItem(position: Int, code: Int) {
                when (code) {
                    LIST_INCLUDE_GENRE -> {
                        viewModel.currentData.selectedGenres?.removeAt(position)
                        filterIncludeGenreRecyclerView.adapter?.notifyDataSetChanged()
                        handleGenreVisibility()
                    }
                    LIST_EXCLUDE_GENRE -> {
                        viewModel.currentData.selectedExcludedGenres?.removeAt(position)
                        filterExcludeGenreRecyclerView.adapter?.notifyDataSetChanged()
                        handleGenreVisibility()
                    }
                    LIST_INCLUDE_TAG -> {
                        viewModel.currentData.selectedTagNames?.removeAt(position)
                        filterIncludeTagRecyclerView.adapter?.notifyDataSetChanged()
                        handleTagVisibility()
                    }
                    LIST_EXCLUDE_TAG -> {
                        viewModel.currentData.selectedExcludedTagNames?.removeAt(position)
                        filterExcludeTagRecyclerView.adapter?.notifyDataSetChanged()
                        handleTagVisibility()
                    }
                }
            }
        })
    }

    private fun handleFilterMinimumTagRankLayout() {
        if (viewModel.currentData.selectedMinimumTagRank == null) {
            viewModel.currentData.selectedMinimumTagRank = Constant.DEFAULT_MINIMUM_TAG_RANK
        }
        filterMinimumTagPercentageText.text = "${viewModel.currentData.selectedMinimumTagRank}%"
        filterMinimumTagPercentageSeekBar.setMinStartValue((viewModel.currentData.selectedMinimumTagRank ?: Constant.DEFAULT_MINIMUM_TAG_RANK).toFloat()).apply()

        filterMinimumTagPercentageSeekBar.setOnSeekbarChangeListener {
            val progress = it.toInt()
            filterMinimumTagPercentageText.text = "${progress}%"
            viewModel.currentData.selectedMinimumTagRank = progress
        }
    }

    private fun handleFilterLicensedLayout() {
        if (viewModel.mediaType == MediaType.ANIME) {
            filterLicensedLabel.text = getString(R.string.streaming_on)
        } else {
            filterLicensedLabel.text = getString(R.string.readeable_on)
        }

        filterLicensedText.text = if (viewModel.currentData.selectedLicensed.isNullOrEmpty()) {
            "-"
        } else {
            viewModel.currentData.selectedLicensed?.joinToString { viewModel.mediaLicensedList.find { licensed -> licensed.first == it }?.second.toString() }
        }

        filterLicensedLayout.setOnClickListener {
            val stringBooleanPair = viewModel.getMediaLicensedArrayPair()
            AlertDialog.Builder(this)
                .setMultiChoiceItems(stringBooleanPair.first, stringBooleanPair.second) { _, index, isChecked ->
                    viewModel.passMediaFormatLicensedValue(index, isChecked)
                    filterLicensedText.text = if (viewModel.currentData.selectedLicensed.isNullOrEmpty()) {
                        "-"
                    } else {
                        viewModel.currentData.selectedLicensed?.joinToString { viewModel.mediaLicensedList.find { licensed -> licensed.first == it }?.second.toString() }
                    }
                }
                .setPositiveButton(R.string.close, null)
                .show()
        }
    }

    private fun handleFilterEpisodeCountLayout() {
        // used for episode filter for anime
        // used for chapter filter for manga

        filterEpisodesSeekBar.setMaxValue(viewModel.episodeSeekBarMaxValue)
        filterEpisodesSeekBar.setMinValue(0F)
        filterEpisodesSeekBar.apply()

        filterEpisodesSeekBar.setMaxStartValue(viewModel.episodeSeekBarMaxValue)
        filterEpisodesSeekBar.setMinStartValue(0F)
        filterEpisodesSeekBar.apply()
        filterEndEpisodeText.text = "-"
        filterStartEpisodeText.text = "-"

        if (viewModel.mediaType == MediaType.ANIME) {
            filterEpisodesLabel.text = getString(R.string.episodes)

            if (viewModel.currentData.selectedEpisodes != null) {
                filterEpisodesSeekBar.setMaxStartValue(viewModel.currentData.selectedEpisodes?.maxValue?.toFloat() ?: viewModel.episodeSeekBarMaxValue)
                if (viewModel.currentData.selectedEpisodes?.minValue?.toFloat() == viewModel.episodeSeekBarMaxValue) {
                    filterEpisodesSeekBar.setMinStartValue(viewModel.episodeSeekBarMaxValue - 0.1F)
                } else {
                    filterEpisodesSeekBar.setMinStartValue(viewModel.currentData.selectedEpisodes?.minValue?.toFloat() ?: 0F)
                }
                filterEpisodesSeekBar.apply()
                filterEndEpisodeText.text = viewModel.currentData.selectedEpisodes?.maxValue.toString()
                filterStartEpisodeText.text = viewModel.currentData.selectedEpisodes?.minValue.toString()
            }
        } else {
            filterEpisodesLabel.text = getString(R.string.chapters)

            if (viewModel.currentData.selectedChapters != null) {
                filterEpisodesSeekBar.setMaxStartValue(viewModel.currentData.selectedChapters?.maxValue?.toFloat() ?: viewModel.episodeSeekBarMaxValue)
                if (viewModel.currentData.selectedChapters?.minValue?.toFloat() == viewModel.episodeSeekBarMaxValue) {
                    filterEpisodesSeekBar.setMinStartValue(viewModel.episodeSeekBarMaxValue - 0.1F)
                } else {
                    filterEpisodesSeekBar.setMinStartValue(viewModel.currentData.selectedChapters?.minValue?.toFloat() ?: 0F)
                }
                filterEpisodesSeekBar.apply()
                filterEndEpisodeText.text = viewModel.currentData.selectedChapters?.maxValue.toString()
                filterStartEpisodeText.text = viewModel.currentData.selectedChapters?.minValue.toString()
            }
        }

        filterEpisodesSeekBar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            if (minValue.toInt() == 0 && maxValue.toInt() == viewModel.episodeSeekBarMaxValue.toInt()) {
                filterEndEpisodeText.text = "-"
                filterStartEpisodeText.text = "-"
                if (viewModel.mediaType == MediaType.ANIME) {
                    viewModel.currentData.selectedEpisodes = null
                } else {
                    viewModel.currentData.selectedChapters = null
                }
            } else {
                filterEndEpisodeText.text = maxValue.toString()
                filterStartEpisodeText.text = minValue.toString()
                if (viewModel.mediaType == MediaType.ANIME) {
                    viewModel.currentData.selectedEpisodes = FilterRange(minValue.toInt(), maxValue.toInt())
                } else {
                    viewModel.currentData.selectedChapters = FilterRange(minValue.toInt(), maxValue.toInt())
                }
            }
        }
    }

    private fun handleFilterDurationLayout() {
        // used for duration filter for anime
        // used for volume filter for manga

        filterDurationSeekBar.setMaxValue(viewModel.durationSeekBarMaxValue)
        filterDurationSeekBar.setMinValue(0F)
        filterDurationSeekBar.apply()

        filterDurationSeekBar.setMaxStartValue(viewModel.durationSeekBarMaxValue)
        filterDurationSeekBar.setMinStartValue(0F)
        filterDurationSeekBar.apply()
        filterStartDurationText.text = "-"
        filterEndDurationText.text = "-"

        if (viewModel.mediaType == MediaType.ANIME) {
            filterDurationLabel.text = getString(R.string.duration)

            if (viewModel.currentData.selectedDuration != null) {
                filterDurationSeekBar.setMaxStartValue(viewModel.currentData.selectedDuration?.maxValue?.toFloat() ?: viewModel.durationSeekBarMaxValue)
                if (viewModel.currentData.selectedDuration?.minValue?.toFloat() == viewModel.durationSeekBarMaxValue) {
                    filterDurationSeekBar.setMinStartValue(viewModel.durationSeekBarMaxValue - 0.1F)
                } else {
                    filterDurationSeekBar.setMinStartValue(viewModel.currentData.selectedDuration?.minValue?.toFloat() ?: 0F)
                }
                filterDurationSeekBar.apply()
                filterEndDurationText.text = viewModel.currentData.selectedDuration?.maxValue?.toString()
                filterStartDurationText.text = viewModel.currentData.selectedDuration?.minValue?.toString()
            }
        } else {
            filterDurationLabel.text = getString(R.string.volumes)

            if (viewModel.currentData.selectedVolumes != null) {
                filterDurationSeekBar.setMaxStartValue(viewModel.currentData.selectedVolumes?.maxValue?.toFloat() ?: viewModel.durationSeekBarMaxValue)
                if (viewModel.currentData.selectedVolumes?.minValue?.toFloat() == viewModel.durationSeekBarMaxValue) {
                    filterDurationSeekBar.setMinStartValue(viewModel.durationSeekBarMaxValue - 0.1F)
                } else {
                    filterDurationSeekBar.setMinStartValue(viewModel.currentData.selectedVolumes?.minValue?.toFloat() ?: 0F)
                }
                filterDurationSeekBar.apply()
                filterEndDurationText.text = viewModel.currentData.selectedVolumes?.maxValue?.toString()
                filterStartDurationText.text = viewModel.currentData.selectedVolumes?.minValue?.toString()
            }
        }

        filterDurationSeekBar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            if (minValue.toInt() == 0 && maxValue.toInt() == viewModel.durationSeekBarMaxValue.toInt()) {
                filterEndDurationText.text = "-"
                filterStartDurationText.text = "-"
                if (viewModel.mediaType == MediaType.ANIME) {
                    viewModel.currentData.selectedDuration = null
                } else {
                    viewModel.currentData.selectedVolumes = null
                }
            } else {
                filterEndDurationText.text = maxValue.toString()
                filterStartDurationText.text = minValue.toString()
                if (viewModel.mediaType == MediaType.ANIME) {
                    viewModel.currentData.selectedDuration = FilterRange(minValue.toInt(), maxValue.toInt())
                } else {
                    viewModel.currentData.selectedVolumes = FilterRange(minValue.toInt(), maxValue.toInt())
                }
            }
        }
    }

    private fun handleFilterAverageScoreLayout() {
        if (viewModel.currentData.selectedAverageScore != null) {
            filterAverageScoreSeekBar.setMaxStartValue(viewModel.currentData.selectedAverageScore?.maxValue?.toFloat() ?: 100F)
            if (viewModel.currentData.selectedAverageScore?.minValue?.toFloat() == 100F) {
                filterAverageScoreSeekBar.setMinStartValue(100F - 0.1F)
            } else {
                filterAverageScoreSeekBar.setMinStartValue(viewModel.currentData.selectedAverageScore?.minValue?.toFloat() ?: 0F)
            }
            filterAverageScoreSeekBar.apply()
            filterEndAverageScoreText.text = viewModel.currentData.selectedAverageScore?.maxValue?.toString()
            filterStartAverageScoreText.text = viewModel.currentData.selectedAverageScore?.minValue?.toString()
        } else {
            filterAverageScoreSeekBar.setMaxStartValue(100F)
            filterAverageScoreSeekBar.setMinStartValue(0F)
            filterAverageScoreSeekBar.apply()
            filterEndAverageScoreText.text = "-"
            filterStartAverageScoreText.text = "-"
        }

        filterAverageScoreSeekBar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            if (minValue.toInt() == 0 && maxValue.toInt() == 100) {
                filterEndAverageScoreText.text = "-"
                filterStartAverageScoreText.text = "-"
                viewModel.currentData.selectedAverageScore = null
            } else {
                filterEndAverageScoreText.text = maxValue.toString()
                filterStartAverageScoreText.text = minValue.toString()
                viewModel.currentData.selectedAverageScore = FilterRange(minValue.toInt(), maxValue.toInt())
            }
        }
    }

    private fun handleFilterPopularityLayout() {
        val maxPopularity = 300000F

        if (viewModel.currentData.selectedPopularity != null) {
            filterPopularitySeekBar.setMaxStartValue(viewModel.currentData.selectedPopularity?.maxValue?.toFloat() ?: maxPopularity)
            if (viewModel.currentData.selectedPopularity?.minValue?.toFloat() == maxPopularity) {
                filterPopularitySeekBar.setMinStartValue(maxPopularity - 0.1F)
            } else {
                filterPopularitySeekBar.setMinStartValue(viewModel.currentData.selectedPopularity?.minValue?.toFloat() ?: 0F)
            }
            filterPopularitySeekBar.apply()
            filterEndPopularityText.text = viewModel.currentData.selectedPopularity?.maxValue?.toString()
            filterStartPopularityText.text = viewModel.currentData.selectedPopularity?.minValue?.toString()
        } else {
            filterPopularitySeekBar.setMaxStartValue(maxPopularity)
            filterPopularitySeekBar.setMinStartValue(0F)
            filterPopularitySeekBar.apply()
            filterEndPopularityText.text = "-"
            filterStartPopularityText.text = "-"
        }

        filterPopularitySeekBar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            if (minValue.toInt() == 0 && maxValue.toInt() == maxPopularity.toInt()) {
                filterEndPopularityText.text = "-"
                filterStartPopularityText.text = "-"
                viewModel.currentData.selectedPopularity = null
            } else {
                filterEndPopularityText.text = maxValue.toString()
                filterStartPopularityText.text = minValue.toString()
                viewModel.currentData.selectedPopularity = FilterRange(minValue.toInt(), maxValue.toInt())
            }
        }
    }

    private fun handleExtraOptionsLayout() {
        if (viewModel.isExplore) {
            filterExtraOptionsLayout.visibility = View.VISIBLE
        } else {
            filterExtraOptionsLayout.visibility = View.GONE
            return
        }

        if (viewModel.mediaType == MediaType.ANIME) {
            filterOnlyShowMediaText.text = getString(R.string.only_show_anime_on_my_list)
            filterHideMediaText.text = getString(R.string.hide_anime_on_my_list)
        } else {
            filterOnlyShowMediaText.text = getString(R.string.only_show_manga_on_my_list)
            filterHideMediaText.text = getString(R.string.hide_manga_on_my_list)
        }

        filterOnlyShowMediaCheckBox.isChecked = viewModel.currentData.selectedOnList == true
        filterHideMediaCheckBox.isChecked = viewModel.currentData.selectedOnList == false

        filterOnlyShowMediaCheckBox.setOnClickListener {
            if (filterOnlyShowMediaCheckBox.isChecked) {
                viewModel.currentData.selectedOnList = true
                filterHideMediaCheckBox.isChecked = false
            } else {
                viewModel.currentData.selectedOnList = null
            }
        }

        filterOnlyShowMediaText.setOnClickListener {
            filterOnlyShowMediaCheckBox.performClick()
        }

        filterHideMediaCheckBox.setOnClickListener {
            if (filterHideMediaCheckBox.isChecked) {
                viewModel.currentData.selectedOnList = false
                filterOnlyShowMediaCheckBox.isChecked = false
            } else {
                viewModel.currentData.selectedOnList = null
            }
        }

        filterHideMediaText.setOnClickListener {
            filterHideMediaCheckBox.performClick()
        }
    }

    private fun handleUserListFilterLayout() {
        if (viewModel.isExplore) {
            userListFilterLayout.visibility = View.GONE
            return
        }

        userListFilterLayout.visibility = View.VISIBLE

        handleFilterUserScoreLayout()
        handleFilterUserStartYear()
        handleFilterUserFinishYear()
        handleFilterUserPriority()
    }

    private fun handleFilterUserScoreLayout() {
        filterUserScoreSeekBar.setMaxValue(viewModel.getUserScoreMaxValue())
        filterUserScoreSeekBar.setMinValue(0F)
        filterUserScoreSeekBar.apply()

        if (viewModel.currentData.selectedUserScore != null) {
            filterUserScoreSeekBar.setMaxStartValue(viewModel.currentData.selectedUserScore?.maxValue?.toFloat() ?: viewModel.getUserScoreMaxValue())
            if (viewModel.currentData.selectedUserScore?.minValue?.toFloat() == viewModel.getUserScoreMaxValue()) {
                filterUserScoreSeekBar.setMinStartValue(viewModel.getUserScoreMaxValue() - 0.1F)
            } else {
                filterUserScoreSeekBar.setMinStartValue(viewModel.currentData.selectedUserScore?.minValue?.toFloat() ?: 0F)
            }
            filterUserScoreSeekBar.apply()
            filterEndUserScoreText.text = viewModel.currentData.selectedUserScore?.maxValue?.toString()
            filterStartUserScoreText.text = viewModel.currentData.selectedUserScore?.minValue?.toString()
        } else {
            filterUserScoreSeekBar.setMaxStartValue(viewModel.getUserScoreMaxValue())
            filterUserScoreSeekBar.setMinStartValue(0F)
            filterUserScoreSeekBar.apply()
            filterEndUserScoreText.text = "-"
            filterStartUserScoreText.text = "-"
        }

        filterUserScoreSeekBar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            if (minValue.toInt() == 0 && maxValue.toInt() == viewModel.getUserScoreMaxValue().toInt()) {
                filterEndUserScoreText.text = "-"
                filterStartUserScoreText.text = "-"
                viewModel.currentData.selectedUserScore = null
            } else {
                filterEndUserScoreText.text = maxValue.toString()
                filterStartUserScoreText.text = minValue.toString()
                viewModel.currentData.selectedUserScore = FilterRange(minValue.toInt(), maxValue.toInt())
            }
        }
    }

    private fun handleFilterUserStartYear() {
        filterUserStartYearSeekBar.setMaxValue(viewModel.yearSeekBarMaxValue)
        filterUserStartYearSeekBar.setMinValue(0F)
        filterUserStartYearSeekBar.apply()

        val maxYear = viewModel.currentData.selectedUserStartYear?.maxValue
        val minYear = viewModel.currentData.selectedUserStartYear?.minValue

        if (minYear != null && maxYear != null) {
            filterUserStartYearSeekBar.setMaxStartValue((maxYear - Constant.FILTER_EARLIEST_YEAR).toFloat())
            if ((minYear - Constant.FILTER_EARLIEST_YEAR).toFloat() == viewModel.yearSeekBarMaxValue) {
                filterUserStartYearSeekBar.setMinStartValue((minYear - Constant.FILTER_EARLIEST_YEAR).toFloat() - 0.1F)
            } else {
                filterUserStartYearSeekBar.setMinStartValue((minYear - Constant.FILTER_EARLIEST_YEAR).toFloat())
            }
            filterUserStartYearSeekBar.apply()
            filterEndUserStartYearText.text = maxYear.toString()
            filterStartUserStartYearText.text = minYear.toString()
        } else {
            filterUserStartYearSeekBar.setMaxStartValue(viewModel.yearSeekBarMaxValue)
            filterUserStartYearSeekBar.setMinStartValue(0F)
            filterUserStartYearSeekBar.apply()
            filterEndUserStartYearText.text = "-"
            filterStartUserStartYearText.text = "-"
        }

        filterUserStartYearSeekBar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            if (minValue.toInt() == 0 && maxValue.toInt() == viewModel.yearSeekBarMaxValue.toInt()) {
                filterEndUserStartYearText.text = "-"
                filterStartUserStartYearText.text = "-"
                viewModel.currentData.selectedUserStartYear = null
            } else {
                val newMaxYear = maxValue.toInt() + Constant.FILTER_EARLIEST_YEAR
                val newMinYear = minValue.toInt() + Constant.FILTER_EARLIEST_YEAR
                filterEndUserStartYearText.text = newMaxYear.toString()
                filterStartUserStartYearText.text = newMinYear.toString()
                viewModel.currentData.selectedUserStartYear = FilterRange(newMinYear, newMaxYear)
            }
        }
    }

    private fun handleFilterUserFinishYear() {
        filterUserFinishYearSeekBar.setMaxValue(viewModel.yearSeekBarMaxValue)
        filterUserFinishYearSeekBar.setMinValue(0F)
        filterUserFinishYearSeekBar.apply()

        val maxYear = viewModel.currentData.selectedUserFinishYear?.maxValue
        val minYear = viewModel.currentData.selectedUserFinishYear?.minValue

        if (minYear != null && maxYear != null) {
            filterUserFinishYearSeekBar.setMaxStartValue((maxYear - Constant.FILTER_EARLIEST_YEAR).toFloat())
            if ((minYear - Constant.FILTER_EARLIEST_YEAR).toFloat() == viewModel.yearSeekBarMaxValue) {
                filterUserFinishYearSeekBar.setMinStartValue((minYear - Constant.FILTER_EARLIEST_YEAR).toFloat() - 0.1F)
            } else {
                filterUserFinishYearSeekBar.setMinStartValue((minYear - Constant.FILTER_EARLIEST_YEAR).toFloat())
            }
            filterUserFinishYearSeekBar.apply()
            filterEndUserFinishYearText.text = maxYear.toString()
            filterStartUserFinishYearText.text = minYear.toString()
        } else {
            filterUserFinishYearSeekBar.setMaxStartValue(viewModel.yearSeekBarMaxValue)
            filterUserFinishYearSeekBar.setMinStartValue(0F)
            filterUserFinishYearSeekBar.apply()
            filterEndUserFinishYearText.text = "-"
            filterStartUserFinishYearText.text = "-"
        }

        filterUserFinishYearSeekBar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            if (minValue.toInt() == 0 && maxValue.toInt() == viewModel.yearSeekBarMaxValue.toInt()) {
                filterEndUserFinishYearText.text = "-"
                filterStartUserFinishYearText.text = "-"
                viewModel.currentData.selectedUserFinishYear = null
            } else {
                val newMaxYear = maxValue.toInt() + Constant.FILTER_EARLIEST_YEAR
                val newMinYear = minValue.toInt() + Constant.FILTER_EARLIEST_YEAR
                filterEndUserFinishYearText.text = newMaxYear.toString()
                filterStartUserFinishYearText.text = newMinYear.toString()
                viewModel.currentData.selectedUserFinishYear = FilterRange(newMinYear, newMaxYear)
            }
        }
    }

    private fun handleFilterUserPriority() {
        if (viewModel.currentData.selectedUserPriority != null) {
            filterUserPrioritySeekBar.setMaxStartValue(viewModel.currentData.selectedUserPriority?.maxValue?.toFloat() ?: 5F)
            if (viewModel.currentData.selectedUserPriority?.minValue?.toFloat() == 5F) {
                filterUserPrioritySeekBar.setMinStartValue(5F - 0.1F)
            } else {
                filterUserPrioritySeekBar.setMinStartValue(viewModel.currentData.selectedUserPriority?.minValue?.toFloat() ?: 0F)
            }
            filterUserPrioritySeekBar.apply()
            filterEndUserPriorityText.text = viewModel.currentData.selectedUserPriority?.maxValue?.toString()
            filterStartUserPriorityText.text = viewModel.currentData.selectedUserPriority?.minValue?.toString()
        } else {
            filterUserPrioritySeekBar.setMaxStartValue(5F)
            filterUserPrioritySeekBar.setMinStartValue(0F)
            filterUserPrioritySeekBar.apply()
            filterEndUserPriorityText.text = "-"
            filterStartUserPriorityText.text = "-"
        }

        filterUserPrioritySeekBar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            if (minValue.toInt() == 0 && maxValue.toInt() == 5) {
                filterEndUserPriorityText.text = "-"
                filterStartUserPriorityText.text = "-"
                viewModel.currentData.selectedUserPriority = null
            } else {
                filterEndUserPriorityText.text = maxValue.toString()
                filterStartUserPriorityText.text = minValue.toString()
                viewModel.currentData.selectedUserPriority = FilterRange(minValue.toInt(), maxValue.toInt())
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_filter_page, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.itemApply -> {
                val intent = Intent()
                intent.putExtra(FILTER_DATA, viewModel.gson.toJson(viewModel.currentData))
                setResult(Activity.RESULT_OK, intent)
                finish()
                return true
            }
            R.id.itemReset -> {
                val intent = Intent()
                setResult(Activity.RESULT_OK, intent)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}