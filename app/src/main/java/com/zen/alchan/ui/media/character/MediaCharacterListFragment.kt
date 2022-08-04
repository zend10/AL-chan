package com.zen.alchan.ui.media.character

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.Character
import com.zen.alchan.data.response.anilist.Staff
import com.zen.alchan.databinding.LayoutInfiniteScrollingBinding
import com.zen.alchan.helper.extensions.applyBottomPaddingInsets
import com.zen.alchan.helper.extensions.applyTopPaddingInsets
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.utils.GridSpacingItemDecoration
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class MediaCharacterListFragment : BaseFragment<LayoutInfiniteScrollingBinding, MediaCharacterListViewModel>() {

    override val viewModel: MediaCharacterListViewModel by viewModel()

    private var adapter: MediaCharacterListRvAdapter? = null

    private var menuItemChangeVaLanguage: MenuItem? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LayoutInfiniteScrollingBinding {
        return LayoutInfiniteScrollingBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            setUpToolbar(defaultToolbar.defaultToolbar, getString(R.string.character_list))
            defaultToolbar.defaultToolbar.inflateMenu(R.menu.menu_character_list)

            menuItemChangeVaLanguage = defaultToolbar.defaultToolbar.menu.findItem(R.id.itemChangeLanguage)
            menuItemChangeVaLanguage?.setOnMenuItemClickListener {
                viewModel.loadVoiceActorLanguages()
                true
            }

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
                adapter = MediaCharacterListRvAdapter(requireContext(), listOf(), it, getMediaCharacterListListener())
                binding.infiniteScrollingRecyclerView.adapter = adapter
            },
            viewModel.characters.subscribe {
                adapter?.updateData(it, true)
            },
            viewModel.emptyLayoutVisibility.subscribe {
                binding.emptyLayout.emptyLayout.show(it)
            },
            viewModel.voiceActorLanguages.subscribe {
                dialog.showListDialog(it) { data, _ ->
                    viewModel.updateVoiceActorLanguage(data)
                }
            }
        )

        arguments?.getInt(MEDIA_ID)?.let {
            viewModel.loadData(MediaCharacterListParam(it))
        }
    }

    private fun getMediaCharacterListListener(): MediaCharacterListRvAdapter.MediaCharacterListListener {
        return object : MediaCharacterListRvAdapter.MediaCharacterListListener {
            override fun navigateToCharacter(character: Character) {
                navigation.navigateToCharacter(character.id)
            }

            override fun navigateToStaff(staff: Staff) {
                navigation.navigateToStaff(staff.id)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        menuItemChangeVaLanguage = null
    }

    companion object {
        private const val MEDIA_ID = "mediaId"
        @JvmStatic
        fun newInstance(mediaId: Int) =
            MediaCharacterListFragment().apply {
                arguments = Bundle().apply {
                    putInt(MEDIA_ID, mediaId)
                }
            }
    }
}