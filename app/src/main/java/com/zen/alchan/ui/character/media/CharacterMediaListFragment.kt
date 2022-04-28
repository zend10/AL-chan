package com.zen.alchan.ui.character.media

import android.os.Bundle
import android.view.LayoutInflater
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
import com.zen.alchan.ui.character.CharacterListener
import com.zen.alchan.ui.character.CharacterMediaRvAdapter
import com.zen.alchan.ui.media.MediaCharacterRvAdapter
import com.zen.alchan.ui.media.character.MediaCharacterListRvAdapter

import org.koin.androidx.viewmodel.ext.android.viewModel

class CharacterMediaListFragment : BaseFragment<LayoutInfiniteScrollingBinding, CharacterMediaListViewModel>() {

    override val viewModel: CharacterMediaListViewModel by viewModel()

    private var adapter: CharacterMediaRvAdapter? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LayoutInfiniteScrollingBinding {
        return LayoutInfiniteScrollingBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            setUpToolbar(defaultToolbar.defaultToolbar, getString(R.string.media_list))

            adapter = CharacterMediaRvAdapter(requireContext(), listOf(), AppSetting(), getCharacterMediaListener())
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
                adapter = CharacterMediaRvAdapter(requireContext(), listOf(), it, getCharacterMediaListener())
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
            viewModel.loadData(it.getInt(CHARACTER_ID))
        }
    }

    private fun getCharacterMediaListener(): CharacterListener.CharacterMediaListener {
        return object : CharacterListener.CharacterMediaListener {
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
        private const val CHARACTER_ID = "characterId"
        @JvmStatic
        fun newInstance(characterId: Int) =
            CharacterMediaListFragment().apply {
                arguments = Bundle().apply {
                    putInt(CHARACTER_ID, characterId)
                }
            }
    }
}