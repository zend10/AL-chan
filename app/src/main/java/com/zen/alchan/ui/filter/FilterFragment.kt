package com.zen.alchan.ui.filter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.databinding.FragmentFilterBinding
import com.zen.alchan.helper.enums.*
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.helper.pojo.SliderItem
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.common.BottomSheetMultiSelectRvAdapter
import com.zen.alchan.ui.common.BottomSheetSliderDialog
import com.zen.alchan.ui.common.ChipRvAdapter
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class FilterFragment : BaseFragment<FragmentFilterBinding, FilterViewModel>() {

    override val viewModel: FilterViewModel by viewModel()
    private val sharedViewModel by sharedViewModel<SharedFilterViewModel>()

    private var includedGenresAdapter: ChipRvAdapter? = null
    private var excludedGenresAdapter: ChipRvAdapter? = null
    private var includedTagsAdapter: ChipRvAdapter? = null
    private var excludedTagsAdapter: ChipRvAdapter? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFilterBinding {
        return FragmentFilterBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            viewModel.mediaType = MediaType.valueOf(it.getString(MEDIA_TYPE) ?: MediaType.ANIME.name)
            viewModel.isUserList = it.getBoolean(IS_USER_LIST)
        }
    }

    override fun setUpLayout() {
        binding.apply {
            setUpToolbar(defaultToolbar.defaultToolbar, getString(R.string.filter))

            includedGenresAdapter = ChipRvAdapter(listOf(), object : ChipRvAdapter.ChipListener {
                override fun getSelectedItem(item: String, index: Int) {
                    viewModel.loadIncludedGenres()
                }

                override fun deleteItem(index: Int) {
                    viewModel.removeIncludedGenre(index)
                }
            })
            filterGenresIncludeRecyclerView.adapter = includedGenresAdapter

            excludedGenresAdapter = ChipRvAdapter(listOf(), object : ChipRvAdapter.ChipListener {
                override fun getSelectedItem(item: String, index: Int) {
                    viewModel.loadExcludedGenres()
                }

                override fun deleteItem(index: Int) {
                    viewModel.removeExcludedGenre(index)
                }
            })
            filterGenresExcludeRecyclerView.adapter = excludedGenresAdapter

            filterPersistCheckBox.setOnClickListener {
                viewModel.updatePersistFilter(filterPersistCheckBox.isChecked)
            }

            filterSortByLayout.clicks {
                viewModel.loadSortByList()
            }

            filterOrderByLayout.clicks {
                viewModel.loadOrderByList()
            }

            filterFormatLayout.clicks {
                viewModel.loadMediaFormats()
            }

            filterStatusLayout.clicks {
                viewModel.loadMediaStatuses()
            }

            filterSourceLayout.clicks {
                viewModel.loadMediaSources()
            }

            filterCountryLayout.clicks {
                viewModel.loadCountries()
            }

            filterSeasonLayout.clicks {
                viewModel.loadMediaSeasons()
            }

            filterReleaseYearLayout.clicks {
                viewModel.loadReleaseYearsSliderItem()
            }

            filterEpisodesLayout.clicks {
                viewModel.loadEpisodesSliderItem()
            }

            filterDurationLayout.clicks {
                viewModel.loadDurationsSliderItem()
            }

            filterAverageScoreLayout.clicks {
                viewModel.loadAverageScoresSliderItem()
            }

            filterPopularityLayout.clicks {
                viewModel.loadPopularitySliderItem()
            }

            filterGenresIncludeText.clicks {
                viewModel.loadIncludedGenres()
            }

            filterGenresExcludeText.clicks {
                viewModel.loadExcludedGenres()
            }

            filterTagsIncludeText.clicks {

            }

            filterTagsExcludeText.clicks {

            }

            filterScoreLayout.clicks {
                viewModel.loadUserScoresSliderItem()
            }

            filterStartYearLayout.clicks {
                viewModel.loadUserStartYearsSliderItem()
            }

            filterCompletedYearLayout.clicks {
                viewModel.loadUserCompletedYearsSliderItem()
            }

            filterPriorityLayout.clicks {
                viewModel.loadUserPrioritiesSliderItem()
            }

            filterApplyLayout.positiveButton.text = getString(R.string.apply)
            filterApplyLayout.positiveButton.clicks {

            }

            filterApplyLayout.negativeButton.text = getString(R.string.reset)
            filterApplyLayout.negativeButton.clicks {

            }
        }
    }

    override fun setUpInsets() {
        binding.apply {
            defaultToolbar.defaultToolbar.applyTopPaddingInsets()
            filterLayout.applySidePaddingInsets()
            filterApplyLayout.twoButtonsLayout.applyBottomSidePaddingInsets()
        }
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.persistFilter.subscribe {
                binding.filterPersistCheckBox.isChecked = it
            },
            viewModel.sortBy.subscribe {
                binding.filterSortByText.text = getString(it.getStringResource())
            },
            viewModel.orderByDescending.subscribe {
                binding.filterOrderByText.text = getString(if (it) R.string.descending else R.string.ascending)
            },
            viewModel.mediaFormats.subscribe {
                binding.filterFormatText.text = getJointString(it) { format -> format.getFormatName() }
            },
            viewModel.mediaStatuses.subscribe {
                binding.filterStatusText.text = getJointString(it) { status -> status.getStatusName() }
            },
            viewModel.mediaSources.subscribe {
                binding.filterSourceText.text = getJointString(it) { source -> source.getSourceName() }
            },
            viewModel.countries.subscribe {
                binding.filterCountryText.text = getJointString(it) { country -> country.getCountryName() }
            },
            viewModel.mediaSeasons.subscribe {
                binding.filterSeasonText.text = getJointString(it) { season -> season.getSeasonName() }
            },
            viewModel.releaseYears.subscribe {
                binding.filterReleaseYearText.text = getPairString(it.data)
            },
            viewModel.includedGenres.subscribe {
                includedGenresAdapter?.updateData(it)
            },
            viewModel.excludedGenres.subscribe {
                excludedGenresAdapter?.updateData(it)
            },
            viewModel.episodes.subscribe {
                binding.filterEpisodesText.text = getPairString(it.data)
            },
            viewModel.durations.subscribe {
                binding.filterDurationText.text = getPairString(it.data)
            },
            viewModel.averageScores.subscribe {
                binding.filterAverageScoreText.text = getPairString(it.data)
            },
            viewModel.popularity.subscribe {
                binding.filterPopularityText.text = getPairString(it.data)
            },
            viewModel.scores.subscribe {
                binding.filterScoreText.text = getPairString(it.data)
            },
            viewModel.startYears.subscribe {
                binding.filterStartYearText.text = getPairString(it.data)
            },
            viewModel.completedYears.subscribe {
                binding.filterCompletedYearText.text = getPairString(it.data)
            },
            viewModel.priorities.subscribe {
                binding.filterPriorityText.text = getPairString(it.data)
            },



            viewModel.includedGenresLayoutVisibility.subscribe {
                binding.filterGenresIncludeLayout.show(it)
            },
            viewModel.excludedGenresLayoutVisibility.subscribe {
                binding.filterGenresExcludeLayout.show(it)
            },
            viewModel.noItemGenreLayoutVisibility.subscribe {
                binding.filterGenresNoItemText.show(it)
            },
            viewModel.includedTagsLayoutVisibility.subscribe {
                binding.filterTagsIncludeLayout.show(it)
            },
            viewModel.excludedTagsLayoutVisibility.subscribe {
                binding.filterTagsExcludeLayout.show(it)
            },
            viewModel.noItemTagLayoutVisibility.subscribe {
                binding.filterTagsNoItemText.show(it)
            },



            viewModel.sortByList.subscribe {
                dialog.showListDialog(it) { data, _ ->
                    viewModel.updateSortBy(data)
                }
            },
            viewModel.orderByList.subscribe {
                dialog.showListDialog(it) { data, _ ->
                    viewModel.updateOrderBy(data)
                }
            },
            viewModel.mediaFormatList.subscribe {
                dialog.showMultiSelectDialog(it.first, it.second) { data ->
                    viewModel.updateMediaFormats(data)
                }
            },
            viewModel.mediaStatusList.subscribe {
                dialog.showMultiSelectDialog(it.first, it.second) { data ->
                    viewModel.updateMediaStatuses(data)
                }
            },
            viewModel.mediaSourceList.subscribe {
                dialog.showMultiSelectDialog(it.first, it.second) { data ->
                    viewModel.updateMediaSources(data)
                }
            },
            viewModel.countryList.subscribe {
                dialog.showMultiSelectDialog(it.first, it.second) { data ->
                    viewModel.updateCountries(data)
                }
            },
            viewModel.mediaSeasonList.subscribe {
                dialog.showMultiSelectDialog(it.first, it.second) { data ->
                    viewModel.updateMediaSeasons(data)
                }
            },
            viewModel.releaseYearsSliderItem.subscribe {
                dialog.showSliderDialog(it) { minValue, maxValue ->
                    viewModel.updateReleaseYears(minValue, maxValue)
                }
            },
            viewModel.includedGenreList.subscribe {
                dialog.showMultiSelectDialog(it.first, it.second) { data ->
                    viewModel.updateIncludedGenres(data)
                }
            },
            viewModel.excludedGenreList.subscribe {
                dialog.showMultiSelectDialog(it.first, it.second) { data ->
                    viewModel.updateExcludedGenres(data)
                }
            },
            viewModel.episodesSliderItem.subscribe {
                dialog.showSliderDialog(it) { minValue, maxValue ->
                    viewModel.updateEpisodes(minValue, maxValue)
                }
            },
            viewModel.durationsSliderItem.subscribe {
                dialog.showSliderDialog(it) { minValue, maxValue ->
                    viewModel.updateDurations(minValue, maxValue)
                }
            },
            viewModel.averageScoresSliderItem.subscribe {
                dialog.showSliderDialog(it) { minValue, maxValue ->
                    viewModel.updateAverageScores(minValue, maxValue)
                }
            },
            viewModel.popularitySliderItem.subscribe {
                dialog.showSliderDialog(it) { minValue, maxValue ->
                    viewModel.updatePopularity(minValue, maxValue)
                }
            },
            viewModel.scoresSliderItem.subscribe {
                dialog.showSliderDialog(it) { minValue, maxValue ->
                    viewModel.updateScores(minValue, maxValue)
                }
            },
            viewModel.startYearsSliderItem.subscribe {
                dialog.showSliderDialog(it) { minValue, maxValue ->
                    viewModel.updateStartYears(minValue, maxValue)
                }
            },
            viewModel.completedYearsSliderItem.subscribe {
                dialog.showSliderDialog(it) { minValue, maxValue ->
                    viewModel.updateCompletedYears(minValue, maxValue)
                }
            },
            viewModel.prioritiesYearsSliderItem.subscribe {
                dialog.showSliderDialog(it) { minValue, maxValue ->
                    viewModel.updatePriorities(minValue, maxValue)
                }
            }
        )

        sharedDisposables.addAll(
            sharedViewModel.oldMediaFilter.subscribe {

            }
        )

        viewModel.loadData()
    }

    private fun <T> getJointString(list: List<T>, action: (data: T) -> String): String {
        return if (list.isEmpty())
            "-"
        else
            list.joinToString(", ") { action(it) }
    }

    private fun getPairString(integerPair: Pair<Int, Int>?): String {
        return if (integerPair == null)
            "-"
        else
            "${integerPair.first} - ${integerPair.second}"
    }

    companion object {
        private const val MEDIA_TYPE = "mediaType"
        private const val IS_USER_LIST = "isUserList"

        @JvmStatic
        fun newInstance(mediaType: MediaType, isUserList: Boolean) =
            FilterFragment().apply {
                arguments = Bundle().apply {
                    putString(MEDIA_TYPE, mediaType.name)
                    putBoolean(IS_USER_LIST, isUserList)
                }
            }
    }
}