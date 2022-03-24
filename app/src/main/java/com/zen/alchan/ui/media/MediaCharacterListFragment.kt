package com.zen.alchan.ui.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.databinding.LayoutInfiniteScrollingBinding
import com.zen.alchan.helper.extensions.applyTopPaddingInsets
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.utils.GridSpacingItemDecoration
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class MediaCharacterListFragment : BaseFragment<LayoutInfiniteScrollingBinding, MediaCharacterListViewModel>() {

    override val viewModel: MediaCharacterListViewModel by viewModel()

    private var adapter: MediaCharacterListRvAdapter? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LayoutInfiniteScrollingBinding {
        return LayoutInfiniteScrollingBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            setUpToolbar(defaultToolbar.defaultToolbar, getString(R.string.character_list))

            adapter = MediaCharacterListRvAdapter(requireContext(), listOf(), AppSetting(), getMediaCharacterListListener())
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
                adapter = MediaCharacterListRvAdapter(requireContext(), listOf(), it, getMediaCharacterListListener())
                binding.infiniteScrollingRecyclerView.adapter = adapter
            },
            viewModel.characters.subscribe {
                adapter?.updateData(it, true)
            },
            viewModel.emptyLayoutVisibility.subscribe {
                binding.emptyLayout.emptyLayout.show(it)
            }
        )

        arguments?.getInt(MEDIA_ID)?.let {
            viewModel.loadData(it)
        }
    }

    private fun getMediaCharacterListListener(): MediaCharacterListRvAdapter.MediaCharacterListListener {
        return object : MediaCharacterListRvAdapter.MediaCharacterListListener {
            override fun navigateToCharacter(id: Int) {
                navigation.navigateToCharacter(id)
            }

            override fun navigateToStaff(id: Int) {
                navigation.navigateToStaff(id)
            }
        }
    }

    companion object {
        private val MEDIA_ID = "mediaId"
        @JvmStatic
        fun newInstance(mediaId: Int) =
            MediaCharacterListFragment().apply {
                arguments = Bundle().apply {
                    putInt(MEDIA_ID, mediaId)
                }
            }
    }
}