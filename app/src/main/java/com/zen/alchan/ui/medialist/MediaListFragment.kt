package com.zen.alchan.ui.medialist

import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.zen.alchan.R
import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.databinding.FragmentMediaListBinding
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.data.entitiy.ListStyle
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.MediaList
import com.zen.alchan.data.response.anilist.MediaListOptions
import com.zen.alchan.helper.enums.ListType
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.pojo.MediaListItem
import com.zen.alchan.helper.utils.ImageUtil
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
                navigation.navigateToCustomise(viewModel.mediaType) { customiseResult ->
                    viewModel.updateListStyle(customiseResult)
                }
                true
            }

            menuItemFilter?.setOnMenuItemClickListener {
                navigation.navigateToFilter(viewModel.mediaFilter, viewModel.mediaType, true) { filterResult ->
                    viewModel.updateMediaFilter(filterResult)
                }
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
                modifyLayoutStyle(it.listStyle, it.appSetting, it.mediaListOptions, it.backgroundUri)
            },
            viewModel.mediaListItems.subscribe {
                adapter?.updateData(it)
            },
            viewModel.listSections.subscribe {
                dialog.showListDialog(it) { _, index ->
                    viewModel.showSelectedSectionMediaList(index)
                }
            }
        )

        sharedDisposables.addAll(
            sharedViewModel.getScrollToTopObservable(sharedViewModel.getPageFromMediaType(viewModel.mediaType)).subscribe {
                binding.mediaListRecyclerView.smoothScrollToPosition(0)
            }
        )

        viewModel.loadData()
    }

    private fun modifyLayoutStyle(listStyle: ListStyle, appSetting: AppSetting, mediaListOptions: MediaListOptions, backgroundUri: Uri?) {
        binding.apply {
            assignAdapter(listStyle, appSetting, mediaListOptions)

            val textColor = listStyle.getTextColor(requireContext())
            defaultToolbar.defaultToolbar.setTitleTextColor(textColor)
            defaultToolbar.defaultToolbar.setSubtitleTextColor(textColor)

            val searchDrawable = menuItemSearch?.icon
            searchDrawable?.mutate()
            searchDrawable?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(textColor, BlendModeCompat.SRC_ATOP)

            val textSearch = searchView?.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
            val closeSearch = searchView?.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
            textSearch?.setTextColor(textColor)
            closeSearch?.imageTintList = ColorStateList.valueOf(textColor)

            val overflowDrawable = defaultToolbar.defaultToolbar.overflowIcon
            overflowDrawable?.mutate()
            overflowDrawable?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(textColor, BlendModeCompat.SRC_ATOP)

            val toolbarColor = listStyle.getToolbarColor(requireContext())
            defaultToolbar.defaultToolbar.setBackgroundColor(toolbarColor)

            val backgroundColor = listStyle.getBackgroundColor(requireContext())
            mediaListRootLayout.setBackgroundColor(backgroundColor)

            val floatingButtonColor = listStyle.getFloatingButtonColor(requireContext())
            mediaListSwitchListButton.backgroundTintList = ColorStateList.valueOf(floatingButtonColor)

            val floatingIconColor = listStyle.getFloatingIconColor(requireContext())
            mediaListSwitchListButton.imageTintList = ColorStateList.valueOf(floatingIconColor)

            if (backgroundUri != null) {
                ImageUtil.loadImage(requireContext(), backgroundUri, binding.mediaListBackgroundImage)
                binding.mediaListBackgroundImage.show(true)
            } else {
                ImageUtil.loadImage(requireContext(), 0, binding.mediaListBackgroundImage)
                binding.mediaListBackgroundImage.show(false)
            }
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

    private fun getMediaListListener(): BaseMediaListRvAdapter.MediaListListener {
        return object : BaseMediaListRvAdapter.MediaListListener {
            override fun navigateToMedia(media: Media) {

            }

            override fun navigateToListEditor(mediaList: MediaList) {
                navigation.navigateToEditor(viewModel.mediaType, mediaList.media.idAniList)
            }

            override fun showQuickDetail(mediaList: MediaList) {

            }

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

            override fun showScoreDialog(mediaList: MediaList) {

            }

            override fun showProgressDialog(mediaList: MediaList, isVolumeProgress: Boolean) {

            }

            override fun incrementProgress(mediaList: MediaList, isVolumeProgress: Boolean) {

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