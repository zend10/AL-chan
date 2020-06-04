package com.zen.alchan.ui.browse.user.list


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import com.zen.alchan.R
import com.zen.alchan.data.network.Converter
import com.zen.alchan.data.response.Media
import com.zen.alchan.data.response.MediaList
import com.zen.alchan.data.response.MediaListGroup
import com.zen.alchan.data.response.MediaTitle
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.enums.ListType
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.pojo.MediaFilteredData
import com.zen.alchan.helper.pojo.MediaListTabItem
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.common.MediaListDetailDialog
import com.zen.alchan.ui.common.filter.MediaFilterBottomSheet
import kotlinx.android.synthetic.main.fragment_user_media_list.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_loading.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaListStatus
import type.MediaType

/**
 * A simple [Fragment] subclass.
 */
class UserMediaListFragment : BaseFragment() {

    private val viewModel by viewModel<UserMediaListViewModel>()

    private lateinit var adapter: RecyclerView.Adapter<*>
    private lateinit var itemSearch: MenuItem
    private lateinit var itemList: MenuItem
    private lateinit var itemCustomiseList: MenuItem
    private lateinit var itemFilter: MenuItem

    private lateinit var searchView: SearchView

    companion object {
        const val USER_ID = "userId"
        const val MEDIA_TYPE = "mediaType"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_media_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.userId = arguments?.getInt(USER_ID)
        viewModel.mediaType = MediaType.valueOf(arguments?.getString(MEDIA_TYPE)!!)

        if (viewModel.listType == null) {
            viewModel.listType = ListType.LINEAR
        }

        toolbarLayout.apply {
            title = if (viewModel.mediaType == MediaType.ANIME) getString(R.string.anime_list) else getString(R.string.manga_list)
            setNavigationOnClickListener { activity?.finish() }
            navigationIcon = ContextCompat.getDrawable(activity!!, R.drawable.ic_delete)

            inflateMenu(R.menu.menu_media_list)
            itemSearch = menu.findItem(R.id.itemSearch)
            itemList = menu.findItem(R.id.itemList)
            itemCustomiseList = menu.findItem(R.id.itemCustomiseList)
            itemFilter = menu.findItem(R.id.itemFilter)
        }

        searchView = itemSearch.actionView as SearchView
        searchView.queryHint = getString(R.string.search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.currentList.clear()
                viewModel.getSelectedList().forEach { filtered ->
                    if (filtered?.media?.title?.userPreferred?.toLowerCase()?.contains(newText ?: "") == true) {
                        viewModel.currentList.add(filtered)
                    }
                }
                adapter.notifyDataSetChanged()

                if (viewModel.currentList.isNullOrEmpty()) {
                    emptyLayout.visibility = View.VISIBLE
                } else {
                    emptyLayout.visibility = View.GONE
                }
                return true
            }
        })

        itemList.setOnMenuItemClickListener {
            MaterialAlertDialogBuilder(activity)
                .setItems(viewModel.getTabItemArray()) { _, which ->
                    viewModel.selectedTab = which

                    val currentTab = viewModel.tabItemList[viewModel.selectedTab]
                    toolbarLayout.subtitle = "${currentTab.status} (${currentTab.count})"

                    viewModel.currentList = ArrayList(viewModel.sortedGroup.find { it?.name == currentTab.status }?.entries ?: listOf())

                    adapter = assignAdapter()
                    mediaListRecyclerView.adapter = adapter

                    if (viewModel.currentList.isNullOrEmpty()) {
                        emptyLayout.visibility = View.VISIBLE
                    } else {
                        emptyLayout.visibility = View.GONE
                    }
                }
                .show()
            true
        }

        itemFilter.setOnMenuItemClickListener {
            val filterDialog = MediaFilterBottomSheet()
            filterDialog.setListener(object : MediaFilterBottomSheet.MediaFilterListener {
                override fun passFilterData(filterData: MediaFilteredData?) {
                    viewModel.filterData = filterData
                    viewModel.setSortedGroup()
                    handleList()
                }
            })
            val bundle = Bundle()
            bundle.putString(MediaFilterBottomSheet.BUNDLE_MEDIA_TYPE, MediaType.ANIME.name)
            bundle.putString(MediaFilterBottomSheet.BUNDLE_FILTERED_DATA, viewModel.gson.toJson(viewModel.filterData))
            filterDialog.arguments = bundle
            filterDialog.show(childFragmentManager, null)
            true
        }

        itemCustomiseList.title = getString(R.string.change_list_type)
        itemCustomiseList.setOnMenuItemClickListener {
            if (viewModel.listType == ListType.LINEAR) {
                viewModel.listType = ListType.GRID
            } else {
                viewModel.listType = ListType.LINEAR
            }
            adapter = assignAdapter()
            mediaListRecyclerView.adapter = adapter
            true
        }

        initLayout()
        setupObserver()
    }

    private fun setupObserver() {
        viewModel.userMediaListCollection.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE

                    viewModel.unfilteredData = it.data
                    viewModel.userData = it.data?.mediaListCollection?.user
                    if (viewModel.mediaType == MediaType.ANIME) {
                        toolbarLayout.title = "${viewModel.userData?.name} ${getString(R.string.anime_list)}"
                    } else {
                        toolbarLayout.title = "${viewModel.userData?.name} ${getString(R.string.manga_list)}"
                    }

                    viewModel.setSortedGroup()
                    handleList()
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)

                    if (viewModel.currentList.isNullOrEmpty()) {
                        emptyLayout.visibility = View.VISIBLE
                        mediaListRecyclerView.visibility = View.GONE
                    } else {
                        emptyLayout.visibility = View.GONE
                        mediaListRecyclerView.visibility = View.VISIBLE
                    }
                }
            }
        })

        if (!viewModel.sortedGroup.isNullOrEmpty()) {
            if (viewModel.mediaType == MediaType.ANIME) {
                toolbarLayout.title = "${viewModel.userData?.name} ${getString(R.string.anime_list)}"
            } else {
                toolbarLayout.title = "${viewModel.userData?.name} ${getString(R.string.manga_list)}"
            }
            handleList()
        } else {
            viewModel.retrieveMediaListCollection()
        }
    }

    private fun handleList() {
        val currentTab = viewModel.tabItemList[viewModel.selectedTab]
        toolbarLayout.subtitle = "${currentTab.status} (${currentTab.count})"

        viewModel.currentList.clear()
        viewModel.currentList.addAll(ArrayList(viewModel.sortedGroup.find { status -> status?.name == currentTab.status }?.entries ?: listOf()))

        adapter = assignAdapter()
        mediaListRecyclerView.adapter = adapter

        if (viewModel.currentList.isNullOrEmpty()) {
            emptyLayout.visibility = View.VISIBLE
            mediaListRecyclerView.visibility = View.GONE
        } else {
            emptyLayout.visibility = View.GONE
            mediaListRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun initLayout() {
        mediaListRefreshLayout.setOnRefreshListener {
            mediaListRefreshLayout.isRefreshing = false
            viewModel.retrieveMediaListCollection()
        }
    }

    private fun assignAdapter(): RecyclerView.Adapter<*> {
        val scoreFormat = viewModel.userData?.mediaListOptions?.scoreFormat!!
        return when {
            viewModel.mediaType == MediaType.ANIME && viewModel.listType == ListType.LINEAR -> {
                mediaListRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                UserAnimeListRvAdapter(activity!!, viewModel.currentList, scoreFormat, handleListAction())
            }
            viewModel.mediaType == MediaType.ANIME && viewModel.listType == ListType.GRID -> {
                mediaListRecyclerView.layoutManager = GridLayoutManager(activity, 3, GridLayoutManager.VERTICAL, false)
                UserAnimeListGridRvAdapter(activity!!, viewModel.currentList, scoreFormat, handleListAction())
            }
            viewModel.mediaType == MediaType.MANGA && viewModel.listType == ListType.LINEAR -> {
                mediaListRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                UserMangaListRvAdapter(activity!!, viewModel.currentList, scoreFormat, handleListAction())
            }
            viewModel.mediaType == MediaType.MANGA && viewModel.listType == ListType.GRID -> {
                mediaListRecyclerView.layoutManager = GridLayoutManager(activity, 3, GridLayoutManager.VERTICAL, false)
                UserMangaListGridRvAdapter(activity!!, viewModel.currentList, scoreFormat, handleListAction())
            }
            else -> {
                mediaListRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                UserAnimeListRvAdapter(activity!!, viewModel.currentList, scoreFormat, handleListAction())
            }
        }
    }

    private fun handleListAction() = object : UserMediaListener {
        override fun openSelectedMedia(mediaId: Int, mediaType: MediaType) {
            listener?.changeFragment(BrowsePage.valueOf(mediaType.name), mediaId)
        }

        override fun viewMediaListDetail(mediaListId: Int) {
            val entry = viewModel.currentList.find { it?.id == mediaListId } ?: return
            val mediaList = MediaList(
                id = entry.id,
                status = entry.status,
                score = entry.score,
                progress = entry.progress,
                progressVolumes = entry.progressVolumes,
                repeat = entry.repeat,
                priority = entry.priority,
                private = entry.private_,
                notes = entry.notes,
                hiddenFromStatusList = entry.hiddenFromStatusLists,
                customLists = entry.customLists,
                advancedScores = entry.customLists,
                startedAt = if (entry.startedAt != null) Converter.convertFuzzyDate(entry.startedAt) else null,
                completedAt = if (entry.completedAt != null) Converter.convertFuzzyDate(entry.completedAt) else null,
                updatedAt = entry.updatedAt,
                createdAt = entry.createdAt,
                media = Media(
                    id = entry.media?.id!!,
                    title = MediaTitle(userPreferred = entry.media.title?.userPreferred!!),
                    type = entry.media.type,
                    format = entry.media.format
                )
            )

            val dialog = MediaListDetailDialog()
            val bundle = Bundle()
            bundle.putString(MediaListDetailDialog.MEDIA_LIST_ITEM, viewModel.gson.toJson(mediaList))
            bundle.putString(MediaListDetailDialog.SCORE_FORMAT, viewModel.userData?.mediaListOptions?.scoreFormat?.name)
            // advancedScoringEnabled is the same in animeList and mangaList
            bundle.putBoolean(MediaListDetailDialog.USE_ADVANCED_SCORES, viewModel.userData?.mediaListOptions?.animeList?.advancedScoringEnabled == true)
            dialog.arguments = bundle
            dialog.show(childFragmentManager, null)
        }
    }
}
