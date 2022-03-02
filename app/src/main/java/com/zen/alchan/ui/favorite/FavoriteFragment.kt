package com.zen.alchan.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.databinding.LayoutInfiniteScrollingBinding
import com.zen.alchan.helper.enums.Favorite
import com.zen.alchan.helper.extensions.applyTopPaddingInsets
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.utils.GridSpacingItemDecoration
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteFragment : BaseFragment<LayoutInfiniteScrollingBinding, FavoriteViewModel>() {

    override val viewModel: FavoriteViewModel by viewModel()

    private var adapter: FavoriteAdapter? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LayoutInfiniteScrollingBinding {
        return LayoutInfiniteScrollingBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            setUpToolbar(defaultToolbar.defaultToolbar, getString(R.string.favorites))

            adapter = FavoriteAdapter(requireContext(), listOf(), AppSetting(), getFavoriteListener())

            infiniteScrollingRecyclerView.layoutManager = if (arguments?.getString(FAVORITE) == Favorite.STUDIOS.name)
                FlexboxLayoutManager(requireContext())
            else
                GridLayoutManager(requireContext(), 3)

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
            viewModel.toolbarTitle.subscribe {
                binding.defaultToolbar.defaultToolbar.title = getString(it)
            },
            viewModel.loading.subscribe {
                binding.infiniteScrollingSwipeRefresh.isRefreshing = it
            },
            viewModel.error.subscribe {
                dialog.showToast(it)
            },
            viewModel.favoriteAdapterComponent.subscribe {
                adapter = FavoriteAdapter(requireContext(), listOf(), it, getFavoriteListener())
                binding.infiniteScrollingRecyclerView.adapter = adapter
            },
            viewModel.favorites.subscribe {
                adapter?.updateData(it, true)
            },
            viewModel.emptyLayoutVisibility.subscribe {
                binding.emptyLayout.emptyLayout.show(it)
            }
        )

        viewModel.loadData(arguments?.getInt(USER_ID) ?: 0, Favorite.valueOf(arguments?.getString(FAVORITE) ?: Favorite.ANIME.name))
    }

    private fun getFavoriteListener(): FavoriteAdapter.FavoriteListener {
        return object : FavoriteAdapter.FavoriteListener {
            override fun navigateToAnime(id: Int) {
                navigation.navigateToMedia(id)
            }

            override fun navigateToManga(id: Int) {
                navigation.navigateToMedia(id)
            }

            override fun navigateToCharacter(id: Int) {
                navigation.navigateToCharacter(id)
            }

            override fun navigateToStaff(id: Int) {
                navigation.navigateToStaff(id)
            }

            override fun navigateToStudio(id: Int) {
                // TODO: navigate to studio
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

    companion object {
        private const val USER_ID = "userId"
        private const val FAVORITE = "favorite"

        @JvmStatic
        fun newInstance(userId: Int, favorite: Favorite) =
            FavoriteFragment().apply {
                arguments = Bundle().apply {
                    putInt(USER_ID, userId)
                    putString(FAVORITE, favorite.name)
                }
            }
    }
}