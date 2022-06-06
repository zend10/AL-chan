package com.zen.alchan.ui.staff.media

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.databinding.LayoutInfiniteScrollingBinding
import com.zen.alchan.helper.extensions.applyBottomPaddingInsets
import com.zen.alchan.helper.extensions.applyTopPaddingInsets
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.utils.GridSpacingItemDecoration
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.staff.character.StaffCharacterListFragment
import com.zen.alchan.ui.staff.character.StaffCharacterListRvAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel


class StaffMediaListFragment : BaseFragment<LayoutInfiniteScrollingBinding, StaffMediaListViewModel>() {

    override val viewModel: StaffMediaListViewModel by viewModel()

    private var adapter: StaffMediaListRvAdapter? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LayoutInfiniteScrollingBinding {
        return LayoutInfiniteScrollingBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            setUpToolbar(defaultToolbar.defaultToolbar, getString(R.string.media_list))

            adapter = StaffMediaListRvAdapter(requireContext(), listOf(), AppSetting(), getStaffMediaListListener())
            infiniteScrollingRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
            infiniteScrollingRecyclerView.addItemDecoration(GridSpacingItemDecoration(3, resources.getDimensionPixelSize(R.dimen.marginNormal), false))
            infiniteScrollingRecyclerView.adapter = adapter

            infiniteScrollingSwipeRefresh.setOnRefreshListener {
                viewModel.reloadData()
            }

            infiniteScrollingRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.canScrollVertically(1)) {
                        viewModel.loadNextPage()
                    }
                }
            })
        }
    }

    override fun setUpInsets() {
        binding.defaultToolbar.defaultToolbar.applyTopPaddingInsets()
        binding.infiniteScrollingRecyclerView.applyBottomPaddingInsets()
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.loading.subscribe {
                binding.infiniteScrollingSwipeRefresh.isRefreshing = it
            },
            viewModel.error.subscribe {
                dialog.showToast(it)
            },
            viewModel.appSetting.subscribe {
                adapter = StaffMediaListRvAdapter(requireContext(), listOf(), it, getStaffMediaListListener())
                binding.infiniteScrollingRecyclerView.adapter = adapter
            },
            viewModel.media.subscribe {
                adapter?.updateData(it, true)
            },
            viewModel.emptyLayoutVisibility.subscribe {
                binding.emptyLayout.emptyLayout.show(it)
            }
        )

        arguments?.let {
            viewModel.loadData(it.getInt(StaffCharacterListFragment.STAFF_ID))
        }
    }

    private fun getStaffMediaListListener(): StaffMediaListRvAdapter.StaffMediaListListener {
        return object : StaffMediaListRvAdapter.StaffMediaListListener {
            override fun navigateToMedia(media: Media) {
                navigation.navigateToMedia(media.getId())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

    companion object {
        const val STAFF_ID = "staffId"
        @JvmStatic
        fun newInstance(staffId: Int) =
            StaffMediaListFragment().apply {
                arguments = Bundle().apply {
                    putInt(STAFF_ID, staffId)
                }
            }
    }
}