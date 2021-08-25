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
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.common.BottomSheetMultiSelectRvAdapter
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class FilterFragment : BaseFragment<FragmentFilterBinding, FilterViewModel>() {

    override val viewModel: FilterViewModel by viewModel()
    private val sharedViewModel by sharedViewModel<SharedFilterViewModel>()

    private var multiSelectAdapter: BottomSheetMultiSelectRvAdapter<*>? = null

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

            }

            filterEpisodesLayout.clicks {

            }

            filterDurationLayout.clicks {

            }

            filterAverageScoreLayout.clicks {

            }

            filterPopularityLayout.clicks {

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

            }

            filterStartYearLayout.clicks {

            }

            filterCompletedYearLayout.clicks {

            }

            filterPriorityLayout.clicks {

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

    private fun <T> getJointString(list: List<T>, action: (data: T) -> String): String {
        return if (list.isEmpty())
            "-"
        else
            list.joinToString(", ") { action(it) }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        multiSelectAdapter = null
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