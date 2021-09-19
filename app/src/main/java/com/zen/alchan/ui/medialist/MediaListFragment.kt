package com.zen.alchan.ui.medialist

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.zen.alchan.R
import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.databinding.FragmentMediaListBinding
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.data.entitiy.ListStyle
import com.zen.alchan.data.response.anilist.MediaList
import com.zen.alchan.data.response.anilist.MediaListOptions
import com.zen.alchan.helper.enums.ListType
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.pojo.MediaListItem
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.customise.SharedCustomiseViewModel
import com.zen.alchan.ui.filter.SharedFilterViewModel
import com.zen.alchan.ui.main.SharedMainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaListFragment : BaseFragment<FragmentMediaListBinding, MediaListViewModel>() {

    override val viewModel: MediaListViewModel by viewModel()
    private val sharedViewModel by sharedViewModel<SharedMainViewModel>()
    private val sharedCustomiseViewModel by sharedViewModel<SharedCustomiseViewModel>()
    private val sharedFilterViewModel by sharedViewModel<SharedFilterViewModel>()

    private var adapter: BaseMediaListRvAdapter? = null

    private var menuItemSearch: MenuItem? = null
    private var menuItemCustomiseList: MenuItem? = null
    private var menuItemFilter: MenuItem? = null

    private var searchView: SearchView? = null

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

            searchView = menuItemSearch?.actionView as? SearchView
            searchView?.queryHint = getString(R.string.search)
            searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    viewModel.filterByText(newText)
                    return true
                }
            })

            assignAdapter(viewModel.listStyle, viewModel.appSetting, viewModel.user.mediaListOptions)

            mediaListSwipeRefresh.setOnRefreshListener {
                viewModel.reloadData()
            }

            menuItemCustomiseList?.setOnMenuItemClickListener {
                viewModel.loadListStyle()
                true
            }

            menuItemFilter?.setOnMenuItemClickListener {
                viewModel.loadMediaFilter()
                true
            }

            mediaListSwitchListButton.clicks {
                viewModel.loadListSections()
            }
        }
    }

    override fun setUpInsets() {
        binding.defaultToolbar.defaultToolbar.applyTopPaddingInsets()
        binding.mediaListRootLayout.applySidePaddingInsets()
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
            viewModel.toolbarSubtitle.subscribe {
                binding.defaultToolbar.defaultToolbar.subtitle = it
            },
            viewModel.mediaListAdapterComponent.subscribe {
                modifyLayoutStyle(it.listStyle, it.appSetting, it.mediaListOptions)
            },
            viewModel.mediaListItems.subscribe {
                adapter?.updateData(it)
            },
            viewModel.listSections.subscribe {
                dialog.showListDialog(it) { _, index ->
                    viewModel.showSelectedSectionMediaList(index)
                }
            },
            viewModel.listStyleAndCustomisedList.subscribe {
                sharedCustomiseViewModel.updateListStyle(it.first, it.second)
                navigation.navigateToCustomise(viewModel.mediaType)
            },
            viewModel.mediaFilterAndFilteredList.subscribe {
                sharedFilterViewModel.updateMediaFilter(it.first, it.second)
                navigation.navigateToFilter(viewModel.mediaType, true)
            }
        )

        sharedDisposables.addAll(
            sharedViewModel.getScrollToTopObservable(sharedViewModel.getPageFromMediaType(viewModel.mediaType)).subscribe {
                binding.mediaListRecyclerView.smoothScrollToPosition(0)
            },
            sharedCustomiseViewModel.newListStyle.subscribe {
                if ((viewModel.mediaType == MediaType.ANIME && it.second == SharedCustomiseViewModel.CustomisedList.ANIME_LIST) ||
                    (viewModel.mediaType == MediaType.MANGA && it.second == SharedCustomiseViewModel.CustomisedList.MANGA_LIST)
                ) {
                    viewModel.updateListStyle(it.first)
                }
            },
            sharedFilterViewModel.newMediaFilter.subscribe {
                if ((viewModel.mediaType == MediaType.ANIME && it.second == SharedFilterViewModel.FilteredList.ANIME_MEDIA_LIST) ||
                    (viewModel.mediaType == MediaType.MANGA && it.second == SharedFilterViewModel.FilteredList.MANGA_MEDIA_LIST)
                ) {
                    viewModel.updateMediaFilter(it.first)
                }
            }
        )

        viewModel.loadData()
    }

    private fun modifyLayoutStyle(listStyle: ListStyle, appSetting: AppSetting, mediaListOptions: MediaListOptions) {
        binding.apply {
            assignAdapter(listStyle, appSetting, mediaListOptions)

            val primaryColor = getColorFromHex(listStyle.primaryColor, requireContext().getThemePrimaryColor())
            val secondaryColor = getColorFromHex(listStyle.secondaryColor, requireContext().getThemeSecondaryColor())
            val negativeColor = getColorFromHex(listStyle.negativeColor, requireContext().getThemeNegativeColor())
            val textColor = getColorFromHex(listStyle.textColor, requireContext().getThemeTextColor())
            val cardColor = getColorFromHex(listStyle.cardColor, requireContext().getThemeCardColor())
            val toolbarColor = getColorFromHex(listStyle.toolbarColor, requireContext().getThemeToolbarColor())
            val backgroundColor = getColorFromHex(listStyle.backgroundColor, requireContext().getThemeBackgroundColor())
            val floatingButtonColor = getColorFromHex(listStyle.floatingButtonColor, requireContext().getThemeFloatingButtonColor())
            val floatingIconColor = getColorFromHex(listStyle.floatingIconColor, requireContext().getThemeFloatingIconColor())

            defaultToolbar.defaultToolbar.setBackgroundColor(toolbarColor)
            defaultToolbar.defaultToolbar.backgroundTintList = ColorStateList.valueOf(toolbarColor)

            defaultToolbar.defaultToolbar.setTitleTextColor(textColor)
            defaultToolbar.defaultToolbar.setSubtitleTextColor(textColor)

            mediaListRootLayout.setBackgroundColor(backgroundColor)

            mediaListSwitchListButton.backgroundTintList = ColorStateList.valueOf(floatingButtonColor)
            mediaListSwitchListButton.imageTintList = ColorStateList.valueOf(floatingIconColor)
        }
    }

    private fun assignAdapter(listStyle: ListStyle, appSetting: AppSetting, mediaListOptions: MediaListOptions) {
        when (listStyle.listType) {
            ListType.LINEAR -> {
                adapter = MediaListLinearRvAdapter(requireContext(), listOf(), appSetting, listStyle, mediaListOptions, getMediaListListener())
                binding.mediaListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            }
            ListType.GRID -> {
                adapter = MediaListGridRvAdapter(requireContext(), listOf(), appSetting, listStyle, mediaListOptions, getMediaListListener())
                binding.mediaListRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
            }
            ListType.SIMPLIFIED -> {
                adapter = MediaListSimplifiedRvAdapter(requireContext(), listOf(), appSetting, listStyle, mediaListOptions, getMediaListListener())
                binding.mediaListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            }
            ListType.ALBUM -> {
                adapter = MediaListAlbumRvAdapter(requireContext(), listOf(), appSetting, listStyle, mediaListOptions, getMediaListListener())
                binding.mediaListRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
            }
        }
        (binding.mediaListRecyclerView.layoutManager as? GridLayoutManager)?.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (adapter?.getItemViewType(position) == MediaListItem.VIEW_TYPE_TITLE) 3 else 1
            }
        }
        binding.mediaListRecyclerView.adapter = adapter
    }

    @ColorInt
    private fun getColorFromHex(hexColor: String?, @ColorInt defaultColor: Int): Int {
        return if (hexColor != null)
            Color.parseColor(hexColor)
        else
            defaultColor
    }

    private fun getMediaListListener(): BaseMediaListRvAdapter.MediaListListener {
        return object : BaseMediaListRvAdapter.MediaListListener {
            override fun showAiringText(airingText: String) {
                dialog.showToast(airingText)
            }

            override fun showNotes(mediaList: MediaList) {
                dialog.showMessageDialog(
                    mediaList.media.getTitle(viewModel.appSetting),
                    mediaList.notes,
                    R.string.ok
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        menuItemSearch = null
        menuItemCustomiseList = null
        menuItemFilter = null
        searchView = null
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