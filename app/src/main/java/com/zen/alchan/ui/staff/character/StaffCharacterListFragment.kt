package com.zen.alchan.ui.staff.character

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.Character
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.databinding.LayoutInfiniteScrollingBinding
import com.zen.alchan.helper.extensions.applyBottomPaddingInsets
import com.zen.alchan.helper.extensions.applyTopPaddingInsets
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.utils.GridSpacingItemDecoration
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.character.CharacterMediaRvAdapter
import com.zen.alchan.ui.character.media.CharacterMediaListFragment
import com.zen.alchan.ui.staff.StaffListener
import org.koin.androidx.viewmodel.ext.android.viewModel


class StaffCharacterListFragment : BaseFragment<LayoutInfiniteScrollingBinding, StaffCharacterListViewModel>() {

    override val viewModel: StaffCharacterListViewModel by viewModel()

    private var adapter: StaffCharacterListRvAdapter? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LayoutInfiniteScrollingBinding {
        return LayoutInfiniteScrollingBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            setUpToolbar(defaultToolbar.defaultToolbar, getString(R.string.character_list))

            adapter = StaffCharacterListRvAdapter(requireContext(), listOf(), AppSetting(), getStaffCharacterListListener())
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
                adapter = StaffCharacterListRvAdapter(requireContext(), listOf(), it, getStaffCharacterListListener())
                binding.infiniteScrollingRecyclerView.adapter = adapter
            },
            viewModel.characters.subscribe {
                adapter?.updateData(it, true)
            },
            viewModel.emptyLayoutVisibility.subscribe {
                binding.emptyLayout.emptyLayout.show(it)
            }
        )

        arguments?.let {
            viewModel.loadData(it.getInt(STAFF_ID))
        }
    }

    private fun getStaffCharacterListListener(): StaffCharacterListRvAdapter.StaffCharacterListListener {
        return object : StaffCharacterListRvAdapter.StaffCharacterListListener {
            override fun navigateToCharacter(character: Character) {
                navigation.navigateToCharacter(character.id)
            }

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
            StaffCharacterListFragment().apply {
                arguments = Bundle().apply {
                    putInt(STAFF_ID, staffId)
                }
            }
    }
}