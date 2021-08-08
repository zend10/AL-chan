package com.zen.alchan.ui.medialist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.zen.alchan.R
import com.zen.alchan.databinding.FragmentMediaListBinding
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.extensions.applySidePaddingInsets
import com.zen.alchan.helper.extensions.applyTopPaddingInsets
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.pojo.ListStyle
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.main.SharedMainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaListFragment : BaseFragment<FragmentMediaListBinding, MediaListViewModel>() {

    override val viewModel: MediaListViewModel by viewModel()
    private val sharedViewModel by sharedViewModel<SharedMainViewModel>()

    private var adapter: BaseMediaListRvAdapter? = null

    private var menuItemSearch: MenuItem? = null
    private var menuItemCustomiseList: MenuItem? = null
    private var menuItemFilter: MenuItem? = null

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
            defaultToolbar.defaultToolbar.apply {
                inflateMenu(R.menu.menu_media_list)
                menuItemSearch = menu.findItem(R.id.itemSearch)
                menuItemCustomiseList = menu.findItem(R.id.itemCustomiseList)
                menuItemFilter = menu.findItem(R.id.itemFilter)
            }

            adapter = MediaListLinearRvAdapter(requireContext(), listOf())
            mediaListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            mediaListRecyclerView.adapter = adapter

            mediaListSwipeRefresh.setOnRefreshListener {
                viewModel.reloadData()
            }

            menuItemCustomiseList?.setOnMenuItemClickListener {
                // navigate to customise list
                true
            }

            menuItemFilter?.setOnMenuItemClickListener {
                // navigate to filter
                true
            }

            mediaListSwitchListButton.clicks {
                viewModel.loadListSections()
            }
        }
    }

    override fun setUpInsets() {
        binding.mediaListRootLayout.applyTopPaddingInsets()
        binding.mediaListRecyclerView.applySidePaddingInsets()
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.loading.subscribe {
                binding.loadingLayout.loadingLayout.show(it)
                binding.mediaListSwipeRefresh.isRefreshing = false
            },
            viewModel.toolbarTitle.subscribe {
                binding.defaultToolbar.defaultToolbar.setTitle(it)
            },
            viewModel.mediaListAdapterComponent.subscribe {
                adapter?.applyAppSetting(it.appSetting)
                adapter?.applyListStyle(it.listStyle)
                adapter?.applyMediaListOptions(it.mediaListOptions)
                adapter?.updateData(it.mediaListItems)
            },
            viewModel.listStyle.subscribe {
                modifyLayoutStyle(it)
            },
            viewModel.listSections.subscribe {
                showListDialog(it) { _, index ->
                    viewModel.showSelectedSectionMediaList(index)
                }
            }
        )

        sharedDisposables.add(
            sharedViewModel.getScrollToTopObservable(sharedViewModel.getPageFromMediaType(viewModel.mediaType)).subscribe {
                binding.mediaListRecyclerView.smoothScrollToPosition(0)
            }
        )

        viewModel.loadData()
    }

    private fun modifyLayoutStyle(listStyle: ListStyle) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

    companion object {
        private const val MEDIA_TYPE = "mediaType"
        private const val USER_ID = "userId"

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