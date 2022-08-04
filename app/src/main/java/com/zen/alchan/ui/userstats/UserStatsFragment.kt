package com.zen.alchan.ui.userstats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.zen.alchan.R
import com.zen.alchan.databinding.FragmentUserStatsBinding
import com.zen.alchan.helper.enums.getStringResource
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class UserStatsFragment : BaseFragment<FragmentUserStatsBinding, UserStatsViewModel>() {

    override val viewModel: UserStatsViewModel by viewModel()

    private var adapter: UserStatsRvAdapter? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUserStatsBinding {
        return FragmentUserStatsBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            setUpToolbar(defaultToolbar.defaultToolbar, getString(R.string.user_stats))

            adapter = UserStatsRvAdapter(requireContext(), listOf(), object : UserStatsRvAdapter.UserStatsListener {
                override fun navigateToStaff(id: Int) {
                    navigation.navigateToStaff(id)
                }

                override fun navigateToStudio(id: Int) {
                    navigation.navigateToStudio(id)
                }
            })
            userStatsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            userStatsRecyclerView.adapter = adapter

            userStatsSwipeRefresh.setOnRefreshListener {
                viewModel.reloadData()
            }

            userStatsStatsText.clicks {
                viewModel.loadStatistics()
            }

            userStatsMediaText.clicks {
                viewModel.loadMediaTypes()
            }

            userStatsSortText.clicks {
                viewModel.loadSorts()
            }
        }
    }

    override fun setUpInsets() {
        binding.defaultToolbar.defaultToolbar.applyTopPaddingInsets()
        binding.userStatsRecyclerView.applySidePaddingInsets()
        binding.userStatsFilterLayout.applyBottomSidePaddingInsets()
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.loading.subscribe {
                binding.userStatsSwipeRefresh.isRefreshing = it
            },
            viewModel.error.subscribe {
                dialog.showToast(it)
            },
            viewModel.statsItems.subscribe {
                adapter?.updateData(it)
            },
            viewModel.statistic.subscribe {
                binding.userStatsStatsText.text = getString(it.getStringResource())
            },
            viewModel.mediaType.subscribe {
                binding.userStatsMediaText.text = getString(it.getStringResource())
            },
            viewModel.sort.subscribe { (sort, mediaType) ->
                binding.userStatsSortText.text = getString(sort.getStringResource(mediaType))
            },
            viewModel.statistics.subscribe {
                dialog.showListDialog(it) { data, _ ->
                    viewModel.updateStatistic(data)
                }
            },
            viewModel.mediaTypes.subscribe {
                dialog.showListDialog(it) { data, _ ->
                    viewModel.updateMediaType(data)
                }
            },
            viewModel.sorts.subscribe {
                dialog.showListDialog(it) { data, _ ->
                    viewModel.updateSort(data)
                }
            },
            viewModel.mediaTypeVisibility.subscribe {
                binding.userStatsMediaLabel.show(it)
                binding.userStatsMediaText.show(it)
            },
            viewModel.sortVisibility.subscribe {
                binding.userStatsSortLabel.show(it)
                binding.userStatsSortText.show(it)
            }
        )

        viewModel.loadData(UserStatsParam(arguments?.getInt(USER_ID) ?: 0))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

    companion object {
        private const val USER_ID = "userId"

        @JvmStatic
        fun newInstance(userId: Int) =
            UserStatsFragment().apply {
                arguments = Bundle().apply {
                    putInt(USER_ID, userId)
                }
            }
    }
}