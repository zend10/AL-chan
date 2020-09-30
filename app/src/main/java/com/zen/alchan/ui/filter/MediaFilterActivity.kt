package com.zen.alchan.ui.filter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zen.alchan.R
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.pojo.FilterRange
import com.zen.alchan.helper.pojo.MediaFilterData
import com.zen.alchan.helper.replaceUnderscore
import com.zen.alchan.ui.base.BaseActivity
import com.zen.alchan.ui.common.filter.MediaFilterRvAdapter
import kotlinx.android.synthetic.main.activity_media_filter.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaSort
import type.MediaType
import java.util.*

class MediaFilterActivity : BaseActivity() {

    private val viewModel by viewModel<MediaFilterViewModel>()

    companion object {
        const val MEDIA_TYPE = "mediaType"
        const val IS_EXPLORE = "isExplore"
        const val FILTER_DATA = "filterData"

        const val ACTIVITY_FILTER = 100

        private const val LIST_INCLUDE_GENRE = 1
        private const val LIST_EXCLUDE_GENRE = 2
        private const val LIST_INCLUDE_TAG = 3
        private const val LIST_EXCLUDE_TAG = 4
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_filter)

        viewModel.mediaType = MediaType.valueOf(intent.getStringExtra(MEDIA_TYPE)!!)
        viewModel.isExplore = intent.getBooleanExtra(IS_EXPLORE, false)
        viewModel.filterData = viewModel.gson.fromJson(intent.getStringExtra(FILTER_DATA), MediaFilterData::class.java)

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
            sortByText.text = if (viewModel.mediaSortMap.containsKey(viewModel.currentData.selectedMediaSort)) {
                getString(viewModel.mediaSortMap[viewModel.currentData.selectedMediaSort]!!).toUpperCase(Locale.US)
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
                val stringArray = viewModel.mediaSortMap.map { sort -> getString(sort.value).toUpperCase(Locale.US) }.toTypedArray()
                MaterialAlertDialogBuilder(this)
                    .setItems(stringArray) { _, which ->
                        viewModel.currentData.selectedMediaSort = viewModel.mediaSortMap.toList()[which].first
                        sortByText.text = stringArray[which]
                    }
                    .show()
            } else {
                val stringArray = viewModel.mediaListSortList.map { sort -> sort.name.replaceUnderscore().toUpperCase(Locale.US) }.toTypedArray()
                MaterialAlertDialogBuilder(this)
                    .setItems(stringArray) { _, which ->
                        viewModel.currentData.selectedMediaListSort = viewModel.mediaListSortList[which]
                        sortByText.text = stringArray[which]
                    }
                    .show()
            }
        }

        orderByLayout.setOnClickListener {
            val stringArray = viewModel.orderByList.map { order -> getString(order).toUpperCase(Locale.US) }.toTypedArray()
            MaterialAlertDialogBuilder(this)
                .setItems(stringArray) { _, which ->
                    // 0 is index for ascending
                    viewModel.currentData.selectedMediaListOrderByDescending = which != 0
                    orderByText.text = stringArray[which]
                }
                .show()
        }
    }

    private fun handleFilterFormatLayout() {
        filterFormatText.text = viewModel.currentData.selectedFormats?.joinToString { it.name.replaceUnderscore() } ?: "-"

        filterFormatLayout.setOnClickListener {
            val stringBooleanPair = viewModel.getMediaFormatArrayPair()
            MaterialAlertDialogBuilder(this)
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
        filterStatusText.text = viewModel.currentData.selectedStatuses?.joinToString { it.name.replaceUnderscore() } ?: "-"

        filterStatusLayout.setOnClickListener {
            val stringBooleanPair = viewModel.getMediaStatusArrayPair()
            MaterialAlertDialogBuilder(this)
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
        filterSourceText.text = viewModel.currentData.selectedSources?.joinToString { it.name.replaceUnderscore() } ?: "-"

        filterSourceLayout.setOnClickListener {
            val stringBooleanPair = viewModel.getMediaSourceArrayPair()
            MaterialAlertDialogBuilder(this)
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
            MaterialAlertDialogBuilder(this)
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
            MaterialAlertDialogBuilder(this)
                .setItems(stringArray) { _, which ->
                    viewModel.currentData.selectedSeason = viewModel.mediaSeasonList[which]
                    filterSeasonText.text = stringArray[which]
                }
                .show()
        }
    }

    private fun handleFilterYearLayout() {
        filterYearRangeSeekBar.setMinValue(0F)
        filterYearRangeSeekBar.setMaxValue(viewModel.yearSeekBarMaxValue)
        filterYearRangeSeekBar.apply()

        val minYear = viewModel.currentData.selectedYear?.greaterThan?.toString()?.substring(0, 4)?.toInt()
        val maxYear = viewModel.currentData.selectedYear?.lesserThan?.toString()?.substring(0, 4)?.toInt()

        if (minYear != null && maxYear != null) {
            filterYearRangeSeekBar.setMinStartValue((minYear - Constant.FILTER_EARLIEST_YEAR).toFloat())
            filterYearRangeSeekBar.setMaxStartValue((maxYear - Constant.FILTER_EARLIEST_YEAR).toFloat())
            filterYearRangeSeekBar.apply()
            filterStartYearText.text = minYear.toString()
            filterEndYearText.text = maxYear.toString()
        } else {
            filterYearRangeSeekBar.setMinStartValue(0F)
            filterYearRangeSeekBar.setMaxStartValue(viewModel.yearSeekBarMaxValue)
            filterYearRangeSeekBar.apply()
            filterStartYearText.text = "-"
            filterEndYearText.text = "-"
        }

        filterYearRangeSeekBar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            if (filterYearRangeSeekBar.isVisible) {
                if (minValue.toInt() == 0 && maxValue.toInt() == viewModel.yearSeekBarMaxValue.toInt()) {
                    filterStartYearText.text = "-"
                    filterEndYearText.text = "-"
                    viewModel.currentData.selectedYear = null
                } else {
                    val newMinYear = minValue.toInt() + Constant.FILTER_EARLIEST_YEAR
                    val newMaxYear = maxValue.toInt() + Constant.FILTER_EARLIEST_YEAR
                    filterStartYearText.text = newMinYear.toString()
                    filterEndYearText.text = newMaxYear.toString()
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
            MaterialAlertDialogBuilder(this)
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
            MaterialAlertDialogBuilder(this)
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

        filterLicensedText.text = viewModel.currentData.selectedLicensed?.joinToString { viewModel.mediaLicensedList.find { licensed -> licensed.first == it }?.second.toString() } ?: "-"

        filterLicensedLayout.setOnClickListener {
            val stringBooleanPair = viewModel.getMediaLicensedArrayPair()
            MaterialAlertDialogBuilder(this)
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

        filterEpisodesSeekBar.setMinValue(0F)
        filterEpisodesSeekBar.setMaxValue(viewModel.episodeSeekBarMaxValue)
        filterEpisodesSeekBar.apply()

        filterEpisodesSeekBar.setMinStartValue(0F)
        filterEpisodesSeekBar.setMaxStartValue(viewModel.episodeSeekBarMaxValue)
        filterEpisodesSeekBar.apply()
        filterStartEpisodeText.text = "-"
        filterEndEpisodeText.text = "-"

        if (viewModel.mediaType == MediaType.ANIME) {
            filterEpisodesLabel.text = getString(R.string.episodes)

            if (viewModel.currentData.selectedEpisodes != null) {
                filterEpisodesSeekBar.setMinStartValue(viewModel.currentData.selectedEpisodes?.greaterThan?.toFloat() ?: 0F)
                filterEpisodesSeekBar.setMaxStartValue(viewModel.currentData.selectedEpisodes?.lesserThan?.toFloat() ?: viewModel.episodeSeekBarMaxValue)
                filterEpisodesSeekBar.apply()
                filterStartEpisodeText.text = viewModel.currentData.selectedEpisodes?.greaterThan.toString()
                filterEndEpisodeText.text = viewModel.currentData.selectedEpisodes?.lesserThan.toString()
            }
        } else {
            filterEpisodesLabel.text = getString(R.string.chapters)

            if (viewModel.currentData.selectedChapters != null) {
                filterEpisodesSeekBar.setMinStartValue(viewModel.currentData.selectedChapters?.greaterThan?.toFloat() ?: 0F)
                filterEpisodesSeekBar.setMaxStartValue(viewModel.currentData.selectedChapters?.lesserThan?.toFloat() ?: viewModel.episodeSeekBarMaxValue)
                filterEpisodesSeekBar.apply()
                filterStartEpisodeText.text = viewModel.currentData.selectedChapters?.greaterThan.toString()
                filterEndEpisodeText.text = viewModel.currentData.selectedChapters?.lesserThan.toString()
            }
        }

        filterEpisodesSeekBar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            if (minValue.toInt() == 0 && maxValue.toInt() == viewModel.episodeSeekBarMaxValue.toInt()) {
                filterStartEpisodeText.text = "-"
                filterEndEpisodeText.text = "-"
                if (viewModel.mediaType == MediaType.ANIME) {
                    viewModel.currentData.selectedEpisodes = null
                } else {
                    viewModel.currentData.selectedChapters = null
                }
            } else {
                filterStartEpisodeText.text = minValue.toString()
                filterEndEpisodeText.text = maxValue.toString()
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

        filterDurationSeekBar.setMinValue(0F)
        filterDurationSeekBar.setMaxValue(viewModel.durationSeekBarMaxValue)
        filterDurationSeekBar.apply()

        filterDurationSeekBar.setMinStartValue(0F)
        filterDurationSeekBar.setMaxStartValue(viewModel.durationSeekBarMaxValue)
        filterDurationSeekBar.apply()
        filterStartDurationText.text = "-"
        filterEndDurationText.text = "-"

        if (viewModel.mediaType == MediaType.ANIME) {
            filterDurationLabel.text = getString(R.string.duration)

            if (viewModel.currentData.selectedDuration != null) {
                filterDurationSeekBar.setMinStartValue(viewModel.currentData.selectedDuration?.greaterThan?.toFloat() ?: 0F)
                filterDurationSeekBar.setMaxStartValue(viewModel.currentData.selectedDuration?.lesserThan?.toFloat() ?: viewModel.durationSeekBarMaxValue)
                filterDurationSeekBar.apply()
                filterStartDurationText.text = viewModel.currentData.selectedDuration?.greaterThan?.toString()
                filterEndDurationText.text = viewModel.currentData.selectedDuration?.lesserThan?.toString()
            }
        } else {
            filterDurationLabel.text = getString(R.string.volumes)

            if (viewModel.currentData.selectedVolumes != null) {
                filterDurationSeekBar.setMinStartValue(viewModel.currentData.selectedVolumes?.greaterThan?.toFloat() ?: 0F)
                filterDurationSeekBar.setMaxStartValue(viewModel.currentData.selectedVolumes?.lesserThan?.toFloat() ?: viewModel.durationSeekBarMaxValue)
                filterDurationSeekBar.apply()
                filterStartDurationText.text = viewModel.currentData.selectedVolumes?.greaterThan?.toString()
                filterEndDurationText.text = viewModel.currentData.selectedVolumes?.lesserThan?.toString()
            }
        }

        filterDurationSeekBar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            if (minValue.toInt() == 0 && maxValue.toInt() == viewModel.durationSeekBarMaxValue.toInt()) {
                filterStartDurationText.text = "-"
                filterEndDurationText.text = "-"
                if (viewModel.mediaType == MediaType.ANIME) {
                    viewModel.currentData.selectedDuration = null
                } else {
                    viewModel.currentData.selectedVolumes = null
                }
            } else {
                filterStartDurationText.text = minValue.toString()
                filterEndDurationText.text = maxValue.toString()
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
            filterAverageScoreSeekBar.setMinStartValue(viewModel.currentData.selectedAverageScore?.greaterThan?.toFloat() ?: 0F)
            filterAverageScoreSeekBar.setMaxStartValue(viewModel.currentData.selectedAverageScore?.lesserThan?.toFloat() ?: 100F)
            filterAverageScoreSeekBar.apply()
            filterStartAverageScoreText.text = viewModel.currentData.selectedAverageScore?.greaterThan?.toString()
            filterEndAverageScoreText.text = viewModel.currentData.selectedAverageScore?.lesserThan?.toString()
        } else {
            filterAverageScoreSeekBar.setMinStartValue(0F)
            filterAverageScoreSeekBar.setMaxStartValue(100F)
            filterAverageScoreSeekBar.apply()
            filterStartAverageScoreText.text = "-"
            filterEndAverageScoreText.text = "-"
        }

        filterAverageScoreSeekBar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            if (minValue.toInt() == 0 && maxValue.toInt() == 100) {
                filterStartAverageScoreText.text = "-"
                filterEndAverageScoreText.text = "-"
                viewModel.currentData.selectedAverageScore = null
            } else {
                filterStartAverageScoreText.text = minValue.toString()
                filterEndAverageScoreText.text = maxValue.toString()
                viewModel.currentData.selectedAverageScore = FilterRange(minValue.toInt(), maxValue.toInt())
            }
        }
    }

    private fun handleFilterPopularityLayout() {
        val maxPopularity = 300000F

        if (viewModel.currentData.selectedPopularity != null) {
            filterPopularitySeekBar.setMinStartValue(viewModel.currentData.selectedPopularity?.greaterThan?.toFloat() ?: 0F)
            filterPopularitySeekBar.setMaxStartValue(viewModel.currentData.selectedPopularity?.lesserThan?.toFloat() ?: maxPopularity)
            filterPopularitySeekBar.apply()
            filterStartPopularityText.text = viewModel.currentData.selectedPopularity?.greaterThan?.toString()
            filterEndPopularityText.text = viewModel.currentData.selectedPopularity?.lesserThan?.toString()
        } else {
            filterPopularitySeekBar.setMinStartValue(0F)
            filterPopularitySeekBar.setMaxStartValue(maxPopularity)
            filterPopularitySeekBar.apply()
            filterStartPopularityText.text = "-"
            filterEndPopularityText.text = "-"
        }

        filterPopularitySeekBar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            if (minValue.toInt() == 0 && maxValue.toInt() == maxPopularity.toInt()) {
                filterStartPopularityText.text = "-"
                filterEndPopularityText.text = "-"
                viewModel.currentData.selectedPopularity = null
            } else {
                filterStartPopularityText.text = minValue.toString()
                filterEndPopularityText.text = maxValue.toString()
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