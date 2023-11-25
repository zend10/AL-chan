package com.zen.alchan.ui.staff.character

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zen.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.Character
import com.zen.alchan.data.response.anilist.Media
import com.zen.databinding.LayoutInfiniteScrollingBinding
import com.zen.alchan.helper.extensions.applyBottomPaddingInsets
import com.zen.alchan.helper.extensions.applyTopPaddingInsets
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.pojo.StaffCharacterListAdapterComponent
import com.zen.alchan.helper.utils.GridSpacingItemDecoration
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class StaffCharacterListFragment : BaseFragment<LayoutInfiniteScrollingBinding, StaffCharacterListViewModel>() {

    override val viewModel: StaffCharacterListViewModel by viewModel()

    private var adapter: StaffCharacterListRvAdapter? = null
    private var mediaAdapter: StaffCharacterMediaListRvAdapter? = null
    private var adapterComponent = StaffCharacterListAdapterComponent()
    private var useMediaAdapter = false

    private var menuShowCharacters: MenuItem? = null
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
            setUpToolbar(defaultToolbar.defaultToolbar, getString(R.string.character_list))
            defaultToolbar.defaultToolbar.inflateMenu(R.menu.menu_staff_character_list)
            handleSubtitle()

            menuShowCharacters = defaultToolbar.defaultToolbar.menu.findItem(R.id.itemShowCharacters)
            menuSortBy = defaultToolbar.defaultToolbar.menu.findItem(R.id.itemSortBy)
            menuShowHideOnList = defaultToolbar.defaultToolbar.menu.findItem(R.id.itemShowHideOnList)

            menuShowCharacters?.setOnMenuItemClickListener {
                viewModel.updateShowCharacters()
                true
            }

            menuSortBy?.setOnMenuItemClickListener {
                viewModel.loadMediaSorts()
                true
            }

            menuShowHideOnList?.setOnMenuItemClickListener {
                viewModel.loadShowHideOnLists()
                true
            }

            adapter = StaffCharacterListRvAdapter(requireContext(), listOf(), adapterComponent.appSetting, getStaffCharacterListListener())
            infiniteScrollingRecyclerView.layoutManager = GridLayoutManager(requireContext(), resources.getInteger(R.integer.gridSpan))
            infiniteScrollingRecyclerView.addItemDecoration(GridSpacingItemDecoration(resources.getInteger(R.integer.gridSpan), resources.getDimensionPixelSize(R.dimen.marginNormal), false))

            mediaAdapter = StaffCharacterMediaListRvAdapter(requireContext(), listOf(), adapterComponent.appSetting, adapterComponent.mediaSort, getStaffCharacterListListener())

            infiniteScrollingRecyclerView.adapter = if (useMediaAdapter) mediaAdapter else adapter

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
                adapter = StaffCharacterListRvAdapter(requireContext(), listOf(), it.appSetting, getStaffCharacterListListener())
                mediaAdapter = StaffCharacterMediaListRvAdapter(requireContext(), listOf(), it.appSetting, it.mediaSort, getStaffCharacterListListener())
                binding.infiniteScrollingRecyclerView.adapter = if (useMediaAdapter) mediaAdapter else adapter
                handleSubtitle()
            },
            viewModel.characters.subscribe {
                adapter?.updateData(it, true)
            },
            viewModel.media.subscribe {
                mediaAdapter?.updateData(it, true)
            },
            viewModel.emptyLayoutVisibility.subscribe {
                binding.emptyLayout.emptyLayout.show(it)
            },
            viewModel.showCharactersText.subscribe {
                menuShowCharacters?.title = getString(it)
            },
            viewModel.showCharacters.subscribe {
                useMediaAdapter = !it
                binding.infiniteScrollingRecyclerView.adapter = if (useMediaAdapter) mediaAdapter else adapter
                handleSubtitle()
            },
            viewModel.mediaSortVisibility.subscribe {
                menuSortBy?.isVisible = it
            },
            viewModel.mediaSortList.subscribe {
                dialog.showListDialog(it) { data, _ ->
                    viewModel.updateMediaSort(data)
                }
            },
            viewModel.showHideOnListVisibility.subscribe {
                menuShowHideOnList?.isVisible = it
            },
            viewModel.showHideOnListList.subscribe {
                dialog.showListDialog(it) { data, _ ->
                    viewModel.updateShowHideOnList(data)
                }
            }
        )

        arguments?.getInt(STAFF_ID)?.let {
            viewModel.loadData(StaffCharacterListParam(it))
        }
    }

    private fun getStaffCharacterListListener(): StaffCharacterListListener {
        return object : StaffCharacterListListener {
            override fun navigateToCharacter(character: Character) {
                navigation.navigateToCharacter(character.id)
            }

            override fun navigateToMedia(media: Media) {
                navigation.navigateToMedia(media.getId())
            }
        }
    }

    private fun handleSubtitle() {
        binding.defaultToolbar.defaultToolbar.subtitle = if (useMediaAdapter)
            getString(R.string.sorted_by_x, getString(adapterComponent.mediaSort.getStringResource()))
        else
            ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        mediaAdapter = null
        menuShowCharacters = null
        menuSortBy = null
        menuShowHideOnList = null
    }

    companion object {
        const val STAFF_ID = "staffId"
        @JvmStatic
        fun newInstance(staffId: Int) =
            StaffCharacterListFragment().apply {
                arguments = Bundle().apply {
                    putInt(STAFF_ID, staffId)
                }
            }
    }
}