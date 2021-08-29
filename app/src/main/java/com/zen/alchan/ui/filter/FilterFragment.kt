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
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class FilterFragment : BaseFragment<FragmentFilterBinding, FilterViewModel>() {

    override val viewModel: FilterViewModel by viewModel()
    private val sharedViewModel by sharedViewModel<SharedFilterViewModel>()

    private var multiSelectAdapter: BottomSheetMultiSelectRvAdapter<*>? = null
    private var sliderDialog: BottomSheetSliderDialog? = null

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

            filterGenresIncludeLayout.clicks {

            }

            filterGenresExcludeLayout.clicks {

            }

            filterTagsIncludeLayout.clicks {

            }

            filterTagsExcludeLayout.clicks {

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






            viewModel.sortByList.subscribe {
                showListDialog(it) { data, _ ->
                    viewModel.updateSortBy(data)
                }
            },
            viewModel.orderByList.subscribe {
                showListDialog(it) { data, _ ->
                    viewModel.updateOrderBy(data)
                }
            },
            viewModel.mediaFormatList.subscribe {
                showMultiSelectDialog(it.first, it.second) { data ->
                    viewModel.updateMediaFormats(data)
                }
            },
            viewModel.mediaStatusList.subscribe {
                showMultiSelectDialog(it.first, it.second) { data ->
                    viewModel.updateMediaStatuses(data)
                }
            },
            viewModel.mediaSourceList.subscribe {
                showMultiSelectDialog(it.first, it.second) { data ->
                    viewModel.updateMediaSources(data)
                }
            },
            viewModel.countryList.subscribe {
                showMultiSelectDialog(it.first, it.second) { data ->
                    viewModel.updateCountries(data)
                }
            },
            viewModel.mediaSeasonList.subscribe {
                showMultiSelectDialog(it.first, it.second) { data ->
                    viewModel.updateMediaSeasons(data)
                }
            },
            viewModel.releaseYearsSliderItem.subscribe {
                showSliderDialog(it) { minValue, maxValue ->
                    viewModel.updateReleaseYears(minValue, maxValue)
                }
            },
            viewModel.episodesSliderItem.subscribe {
                showSliderDialog(it) { minValue, maxValue ->
                    viewModel.updateEpisodes(minValue, maxValue)
                }
            },
            viewModel.durationsSliderItem.subscribe {
                showSliderDialog(it) { minValue, maxValue ->
                    viewModel.updateDurations(minValue, maxValue)
                }
            },
            viewModel.averageScoresSliderItem.subscribe {
                showSliderDialog(it) { minValue, maxValue ->
                    viewModel.updateAverageScores(minValue, maxValue)
                }
            },
            viewModel.popularitySliderItem.subscribe {
                showSliderDialog(it) { minValue, maxValue ->
                    viewModel.updatePopularity(minValue, maxValue)
                }
            },
            viewModel.scoresSliderItem.subscribe {
                showSliderDialog(it) { minValue, maxValue ->
                    viewModel.updateScores(minValue, maxValue)
                }
            },
            viewModel.startYearsSliderItem.subscribe {
                showSliderDialog(it) { minValue, maxValue ->
                    viewModel.updateStartYears(minValue, maxValue)
                }
            },
            viewModel.completedYearsSliderItem.subscribe {
                showSliderDialog(it) { minValue, maxValue ->
                    viewModel.updateCompletedYears(minValue, maxValue)
                }
            },
            viewModel.prioritiesYearsSliderItem.subscribe {
                showSliderDialog(it) { minValue, maxValue ->
                    viewModel.updatePriorities(minValue, maxValue)
                }
            }
        )

        sharedDisposables.addAll(
            sharedViewModel.oldMediaFilter.subscribe {

            }
        )
    }

    private fun <T> showMultiSelectDialog(
        list: List<ListItem<T>>,
        selectedIndex: ArrayList<Int>,
        action: (data: List<T>) -> Unit
    ) {
        multiSelectAdapter =  BottomSheetMultiSelectRvAdapter(
            requireContext(),
            list,
            selectedIndex,
            object : BottomSheetMultiSelectRvAdapter.BottomSheetMultiSelectListener<T> {
                override fun getSelectedItems(data: List<T>, index: List<Int>) {
                    action(data)
                }
            })
            .also { adapter ->
                showListDialog(adapter)
            }
    }

    private fun showSliderDialog(
        sliderItem: SliderItem,
        action: (minValue: Int?, maxValue: Int?) -> Unit
    ) {
        sliderDialog = BottomSheetSliderDialog.newInstance(
            sliderItem,
            object : BottomSheetSliderDialog.BottomSheetSliderListener {
                override fun getNewValues(minValue: Int?, maxValue: Int?) {
                    action(minValue, maxValue)
                }
            })
        sliderDialog?.dialog?.setOnCancelListener {
            sliderDialog = null
        }
        sliderDialog?.show(childFragmentManager, null)
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

    override fun onDestroyView() {
        super.onDestroyView()
        multiSelectAdapter = null
        sliderDialog = null
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