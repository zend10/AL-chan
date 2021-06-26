package com.zen.alchan.ui.medialist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.zen.alchan.databinding.FragmentMediaListBinding
import com.zen.alchan.helper.extensions.applyTopPaddingInsets
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.main.SharedMainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaType


private const val MEDIA_TYPE = "mediaType"
private const val USER_ID = "userId"

class MediaListFragment : BaseFragment<FragmentMediaListBinding, MediaListViewModel>() {

    override val viewModel: MediaListViewModel by viewModel()
    private val sharedViewModel by sharedViewModel<SharedMainViewModel>()

    private var adapter: BaseMediaListRvAdapter? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMediaListBinding {
        return FragmentMediaListBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            viewModel.mediaType = MediaType.valueOf(it.getString(MEDIA_TYPE) ?: MediaType.ANIME.name)
            viewModel.userId = it.getInt(USER_ID)
        }
    }

    override fun setUpLayout() {
        binding.apply {
            adapter = MediaListLinearRvAdapter(requireContext(), listOf())
            mediaListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            mediaListRecyclerView.adapter = adapter

            mediaListSwipeRefresh.setOnRefreshListener {
                viewModel.reloadData()
            }
        }
    }

    override fun setUpInsets() {
        binding.mediaListRootLayout.applyTopPaddingInsets()
    }

    override fun setUpObserver() {
        disposables.add(
            viewModel.loading.subscribe {
                binding.loadingLayout.loadingLayout.show(it)
                binding.mediaListSwipeRefresh.isRefreshing = false
            }
        )

        disposables.add(
            viewModel.mediaListAdapterComponent.subscribe {
                adapter?.applyAppSetting(it.appSetting)
                adapter?.applyListStyle(it.listStyle)
                adapter?.applyMediaListOptions(it.mediaListOptions)
                adapter?.updateData(it.mediaListItems)
            }
        )

        sharedDisposables.add(
            sharedViewModel.getScrollToTopObservable(SharedMainViewModel.Page.ANIME).subscribe {
                binding.mediaListRecyclerView.smoothScrollToPosition(0)
            }
        )

        viewModel.loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

    companion object {
        @JvmStatic
        fun newInstance(mediaType: MediaType, userId: Int = 0) =
            MediaListFragment().apply {
                arguments = Bundle().apply {
                    putString(MEDIA_TYPE, mediaType.name)
                    putInt(USER_ID, userId)
                }
            }
    }
}