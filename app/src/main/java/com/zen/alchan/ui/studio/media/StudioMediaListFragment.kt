package com.zen.alchan.ui.studio.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zen.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.Media
import com.zen.databinding.LayoutInfiniteScrollingBinding
import com.zen.alchan.helper.extensions.applyBottomPaddingInsets
import com.zen.alchan.helper.extensions.applyTopPaddingInsets
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.pojo.StudioMediaListAdapterComponent
import com.zen.alchan.helper.utils.GridSpacingItemDecoration
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class StudioMediaListFragment : BaseFragment<LayoutInfiniteScrollingBinding, StudioMediaListViewModel>() {

    override val viewModel: StudioMediaListViewModel by viewModel()

    private var adapter: StudioMediaListRvAdapter? = null
    private var adapterComponent = StudioMediaListAdapterComponent()

    private var menuSortBy: MenuItem? = null
    private var menuShowHideOnList: MenuItem? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LayoutInfiniteScrollingBinding {
        return LayoutInfiniteScrollingBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            setUpToolbar(defaultToolbar.defaultToolbar, getString(R.string.media_list))
            defaultToolbar.defaultToolbar.inflateMenu(R.menu.menu_studio_media_list)
            defaultToolbar.defaultToolbar.subtitle = getString(R.string.sorted_by_x, getString(adapterComponent.mediaSort.getStringResource()))

            menuSortBy = defaultToolbar.defaultToolbar.menu.findItem(R.id.itemSortBy)
            menuShowHideOnList = defaultToolbar.defaultToolbar.menu.findItem(R.id.itemShowHideOnList)

            menuSortBy?.setOnMenuItemClickListener {
                viewModel.loadMediaSorts()
                true
            }

            menuShowHideOnList?.setOnMenuItemClickListener {
                viewModel.loadShowHideOnLists()
                true
            }

            adapter = StudioMediaListRvAdapter(requireContext(), listOf(), adapterComponent.appSetting, adapterComponent.mediaSort, getStudioMediaListListener())
            infiniteScrollingRecyclerView.layoutManager = GridLayoutManager(requireContext(), resources.getInteger(R.integer.gridSpan))
            infiniteScrollingRecyclerView.addItemDecoration(GridSpacingItemDecoration(resources.getInteger(R.integer.gridSpan), resources.getDimensionPixelSize(R.dimen.marginNormal), false))
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
            viewModel.adapterComponent.subscribe {
                adapterComponent = it
                adapter = StudioMediaListRvAdapter(requireContext(), listOf(), it.appSetting, it.mediaSort, getStudioMediaListListener())
                binding.infiniteScrollingRecyclerView.adapter = adapter
                binding.defaultToolbar.defaultToolbar.subtitle = getString(R.string.sorted_by_x, getString(it.mediaSort.getStringResource()))
            },
            viewModel.media.subscribe {
                adapter?.updateData(it, true)
            },
            viewModel.emptyLayoutVisibility.subscribe {
                binding.emptyLayout.emptyLayout.show(it)
            },
            viewModel.mediaSortList.subscribe {
                dialog.showListDialog(it) { data, _ ->
                    viewModel.updateMediaSort(data)
                }
            },
            viewModel.showHideOnListList.subscribe {
                dialog.showListDialog(it) { data, _ ->
                    viewModel.updateShowHideOnList(data)
                }
            }
        )

        arguments?.getInt(STUDIO_ID)?.let {
            viewModel.loadData(StudioMediaListParam(it))
        }
    }

    private fun getStudioMediaListListener(): StudioMediaListRvAdapter.StudioMediaListListener {
        return object : StudioMediaListRvAdapter.StudioMediaListListener {
            override fun navigateToMedia(media: Media) {
                navigation.navigateToMedia(media.getId())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        menuSortBy = null
        menuShowHideOnList = null
    }

    companion object {
        const val STUDIO_ID = "studioId"
        @JvmStatic
        fun newInstance(studioId: Int) = StudioMediaListFragment().apply {
            arguments = Bundle().apply {
                putInt(STUDIO_ID, studioId)
            }
        }
    }
}