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
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.databinding.FragmentMediaListBinding
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.data.entity.ListStyle
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
import type.MediaListStatus
import type.ScoreFormat

class MediaListFragment : BaseFragment<FragmentMediaListBinding, MediaListViewModel>() {

    override val viewModel: MediaListViewModel by viewModel()
    private val sharedViewModel by sharedViewModel<SharedMainViewModel>()

    private var adapter: BaseMediaListRvAdapter? = null

    private var menuItemSearch: MenuItem? = null
    private var menuItemCustomiseList: MenuItem? = null
    private var menuItemChangeListType: MenuItem? = null
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
            if (!viewModel.isViewer) {
                setUpToolbar(defaultToolbar.defaultToolbar, getString(R.string.list))
            }

            defaultToolbar.defaultToolbar.apply {
                inflateMenu(R.menu.menu_media_list)
                menuItemSearch = menu.findItem(R.id.itemSearch)
                menuItemCustomiseList = menu.findItem(R.id.itemCustomiseList)
                menuItemChangeListType = menu.findItem(R.id.itemChangeListType)
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

            assignAdapter(viewModel.isViewer, viewModel.listStyle, viewModel.appSetting, viewModel.user.mediaListOptions)

            mediaListSwipeRefresh.setOnRefreshListener {
                viewModel.reloadData()
            }

            menuItemCustomiseList?.setOnMenuItemClickListener {
                navigation.navigateToCustomise(viewModel.mediaType) { customiseResult ->
                    viewModel.updateListStyle(customiseResult)
                }
                true
            }

            menuItemChangeListType?.setOnMenuItemClickListener {
                viewModel.loadListTypes()
                true
            }

            menuItemFilter?.setOnMenuItemClickListener {
                navigation.navigateToFilter(viewModel.mediaFilter, viewModel.mediaType, viewModel.user.mediaListOptions.scoreFormat ?: ScoreFormat.POINT_100, true, viewModel.isViewer) { filterResult ->
                    viewModel.updateMediaFilter(filterResult)
                }
                true
            }

            mediaListSwitchListButton.clicks {
                viewModel.loadListSections()
            }

            mediaListRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0)
                        mediaListSwitchListButton.hide()
                    else
                        mediaListSwitchListButton.show()
                }
            })
        }
    }

    override fun setUpInsets() {
        binding.defaultToolbar.defaultToolbar.applyTopPaddingInsets()
        binding.mediaListRootLayout.applySidePaddingInsets()
        if (!viewModel.isViewer) {
            binding.mediaListSwitchListContainer.applyBottomPaddingInsets()
            binding.mediaListRecyclerView.applyBottomPaddingInsets()
        }
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.loading.subscribe {
                binding.loadingLayout.loadingLayout.show(it)
                binding.mediaListSwipeRefresh.isRefreshing = false
            },
            viewModel.success.subscribe {
                dialog.showToast(it)
            },
            viewModel.error.subscribe {
                dialog.showToast(it)
            },
            viewModel.toolbarTitle.subscribe {
                binding.defaultToolbar.defaultToolbar.setTitle(it)
            },
            viewModel.toolbarSubtitle.subscribe {
                binding.defaultToolbar.defaultToolbar.subtitle = it
            },
            viewModel.menuItemCustomiseListVisibility.subscribe {
                menuItemCustomiseList?.isVisible = it
            },
            viewModel.menuItemChangeListTypeVisibility.subscribe {
                menuItemChangeListType?.isVisible = it
            },
            viewModel.mediaListAdapterComponent.subscribe {
                modifyLayoutStyle(it.isViewer, it.listStyle, it.appSetting, it.mediaListOptions, it.backgroundUri)
            },
            viewModel.mediaListItems.subscribe {
                adapter?.updateData(it)
            },
            viewModel.listSections.subscribe {
                dialog.showListDialog(it) { _, index ->
                    viewModel.showSelectedSectionMediaList(index)
                }
            },
            viewModel.scoreValues.subscribe { (mediaList: MediaList, scoreFormat: ScoreFormat) ->
                val currentScore = mediaList.score
                val advancedScores = mediaList.advancedScores as? LinkedHashMap<String, Double>
                dialog.showScoreDialog(scoreFormat, currentScore, advancedScores) { newScore, newAdvancedScores ->
                    viewModel.updateScore(mediaList, newScore, newAdvancedScores)
                }
            },
            viewModel.progressValues.subscribe { (mediaList: MediaList, isProgressVolume: Boolean) ->
                val currentProgress = if (isProgressVolume)
                    mediaList.progressVolumes ?: 0
                else
                    mediaList.progress

                val maxProgress = when (viewModel.mediaType) {
                    MediaType.ANIME -> mediaList.media.episodes
                    MediaType.MANGA -> if (isProgressVolume) mediaList.media.volumes else mediaList.media.chapters
                }

                dialog.showProgressDialog(viewModel.mediaType, currentProgress, maxProgress, isProgressVolume) { newProgress ->
                    viewModel.updateProgress(mediaList, newProgress, isProgressVolume)
                }
            },
            viewModel.setToWatchingDialog.subscribe { (mediaList, newProgress, isProgressVolume) ->
                dialog.showConfirmationDialog(
                    R.string.move_to_watching,
                    R.string.do_you_want_to_set_this_entry_into_watching,
                    R.string.move,
                    {
                        viewModel.updateProgress(mediaList, MediaListStatus.CURRENT, newProgress, isProgressVolume)
                    },
                    R.string.stay,
                    {
                        viewModel.updateProgress(mediaList, null, newProgress, isProgressVolume)
                    }
                )
            },
            viewModel.listTypes.subscribe {
                dialog.showListDialog(it) { data, _ ->
                    viewModel.updateListType(data)
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

    private fun modifyLayoutStyle(isViewer: Boolean, listStyle: ListStyle, appSetting: AppSetting, mediaListOptions: MediaListOptions, backgroundUri: Uri?) {
        binding.apply {
            assignAdapter(isViewer, listStyle, appSetting, mediaListOptions)

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

    private fun assignAdapter(isViewer: Boolean, listStyle: ListStyle, appSetting: AppSetting, mediaListOptions: MediaListOptions) {
        when (listStyle.listType) {
            ListType.LINEAR -> {
                adapter = MediaListLinearRvAdapter(requireContext(), listOf(), isViewer, appSetting, listStyle, mediaListOptions, getMediaListListener())
                binding.mediaListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            }
            ListType.GRID -> {
                adapter = MediaListGridRvAdapter(requireContext(), listOf(), isViewer, appSetting, listStyle, mediaListOptions, getMediaListListener())
                binding.mediaListRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
            }
            ListType.SIMPLIFIED -> {
                adapter = MediaListSimplifiedRvAdapter(requireContext(), listOf(), isViewer, appSetting, listStyle, mediaListOptions, getMediaListListener())
                binding.mediaListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            }
            ListType.ALBUM -> {
                adapter = MediaListAlbumRvAdapter(requireContext(), listOf(), isViewer, appSetting, listStyle, mediaListOptions, getMediaListListener())
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
                navigation.navigateToMedia(media.getId())
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
                viewModel.loadScoreValues(mediaList)
            }

            override fun showProgressDialog(mediaList: MediaList, isVolumeProgress: Boolean) {
                viewModel.loadProgressValues(mediaList, isVolumeProgress)
            }

            override fun incrementProgress(mediaList: MediaList, newProgress: Int, isVolumeProgress: Boolean) {
                viewModel.updateProgress(mediaList, newProgress, isVolumeProgress)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        menuItemSearch = null
        menuItemCustomiseList = null
        menuItemChangeListType = null
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