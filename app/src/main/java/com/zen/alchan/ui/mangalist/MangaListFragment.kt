package com.zen.alchan.ui.mangalist


import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.signature.ObjectKey
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.internal.LinkedTreeMap

import com.zen.alchan.R
import com.zen.alchan.data.response.Media
import com.zen.alchan.data.response.MediaList
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.enums.ListType
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.AdvancedScoresItem
import com.zen.alchan.helper.pojo.MediaFilteredData
import com.zen.alchan.helper.pojo.MediaListTabItem
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.helper.utils.Utility
import com.zen.alchan.ui.browse.BrowseActivity
import com.zen.alchan.ui.common.MediaListDetailDialog
import com.zen.alchan.ui.common.SetProgressDialog
import com.zen.alchan.ui.common.SetScoreDialog
import com.zen.alchan.ui.common.customise.CustomiseListActivity
import com.zen.alchan.ui.common.filter.MediaFilterBottomSheet
import com.zen.alchan.ui.mangalist.editor.MangaListEditorActivity
import com.zen.alchan.ui.mangalist.list.MangaListGridRvAdapter
import com.zen.alchan.ui.mangalist.list.MangaListListener
import com.zen.alchan.ui.mangalist.list.MangaListRvAdapter
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_manga_list.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_loading.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaListStatus
import type.MediaType
import type.ScoreFormat

/**
 * A simple [Fragment] subclass.
 */
class MangaListFragment : Fragment() {

    private val viewModel by viewModel<MangaListViewModel>()

    private lateinit var adapter: RecyclerView.Adapter<*>
    private lateinit var itemSearch: MenuItem
    private lateinit var itemList: MenuItem
    private lateinit var itemCustomiseList: MenuItem
    private lateinit var itemFilter: MenuItem

    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manga_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        toolbarLayout.apply {
            title = getString(R.string.manga_list)
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
                    if (filtered.media?.title?.userPreferred?.toLowerCase()?.contains(newText ?: "") == true) {
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

                    viewModel.currentList = ArrayList(viewModel.mangaListData.value?.lists?.find { it.name == currentTab.status }?.entries ?: listOf())

                    adapter = assignAdapter()
                    mangaListRecyclerView.adapter = adapter

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
                    viewModel.setFilteredData(filterData)
                    initLayout()
                }
            })
            val bundle = Bundle()
            bundle.putString(MediaFilterBottomSheet.BUNDLE_MEDIA_TYPE, MediaType.MANGA.name)
            bundle.putString(MediaFilterBottomSheet.BUNDLE_FILTERED_DATA, viewModel.gson.toJson(viewModel.filteredData))
            filterDialog.arguments = bundle
            filterDialog.show(childFragmentManager, null)
            true
        }

        itemCustomiseList.setOnMenuItemClickListener {
            val intent = Intent(activity, CustomiseListActivity::class.java)
            intent.putExtra(CustomiseListActivity.MEDIA_TYPE, MediaType.MANGA.name)
            startActivity(intent)
            true
        }

        adapter = assignAdapter()
        mangaListRecyclerView.adapter = adapter

        initLayout()
        setupObserver()
    }

    private fun setupObserver() {
        viewModel.mangaListDataResponse.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> loadingLayout.visibility = View.GONE
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                }
            }
        })

        viewModel.mangaListData.observe(viewLifecycleOwner, Observer {
            viewModel.tabItemList.clear()
            it.lists?.forEach { list -> viewModel.tabItemList.add(MediaListTabItem(list.name!!, list.entries?.size!!)) }

            if (viewModel.tabItemList.isNullOrEmpty()) {
                Constant.DEFAULT_MANGA_LIST_ORDER.forEach { list ->
                    viewModel.tabItemList.add(MediaListTabItem(list, 0))
                }
            }

            if (viewModel.selectedTab >= viewModel.tabItemList.size) {
                viewModel.selectedTab = viewModel.tabItemList.size - 1
            }

            val currentTab = viewModel.tabItemList[viewModel.selectedTab]
            toolbarLayout.subtitle = "${currentTab.status} (${currentTab.count})"

            viewModel.currentList.clear()
            viewModel.currentList.addAll(ArrayList(it.lists?.find { status -> status.name == currentTab.status }?.entries ?: listOf()))
            adapter.notifyDataSetChanged()

            if (viewModel.currentList.isNullOrEmpty()) {
                emptyLayout.visibility = View.VISIBLE
            } else {
                emptyLayout.visibility = View.GONE
            }
        })

        viewModel.mangaListStyleLiveData.observe(viewLifecycleOwner, Observer {
            adapter = assignAdapter()
            mangaListRecyclerView.adapter = adapter
            initLayout()

            if (viewModel.currentList.isNullOrEmpty()) {
                emptyLayout.visibility = View.VISIBLE
            } else {
                emptyLayout.visibility = View.GONE
            }
        })

        viewModel.updateMangaListEntryResponse.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> loadingLayout.visibility = View.GONE
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                }
            }
        })

        viewModel.initData()
    }

    private fun initLayout() {
        mangaListRefreshLayout.setOnRefreshListener {
            mangaListRefreshLayout.isRefreshing = false
            viewModel.retrieveMangaListData()
        }

        val listStyle = viewModel.mangaListStyleLiveData.value
        if (listStyle?.toolbarColor != null) {
            toolbarLayout.setBackgroundColor(Color.parseColor(listStyle.toolbarColor))
            toolbarLayout.backgroundTintList = ColorStateList.valueOf(Color.parseColor(listStyle.toolbarColor))
        }

        if (listStyle?.backgroundColor != null) {
            mangaListLayout.setBackgroundColor(Color.parseColor(listStyle.backgroundColor))
        }

        if (listStyle?.textColor != null) {
            toolbarLayout.setTitleTextColor(Color.parseColor(listStyle.textColor))
            toolbarLayout.setSubtitleTextColor(Color.parseColor(listStyle.textColor))

            val searchDrawable = itemSearch.icon
            searchDrawable.mutate()
            searchDrawable.setColorFilter(Color.parseColor(listStyle.textColor), PorterDuff.Mode.SRC_ATOP)

            val listDrawable = itemList.icon
            listDrawable.mutate()
            listDrawable.setColorFilter(Color.parseColor(listStyle.textColor), PorterDuff.Mode.SRC_ATOP)

            val overFlowDrawable = toolbarLayout.overflowIcon
            overFlowDrawable?.mutate()
            overFlowDrawable?.setColorFilter(Color.parseColor(listStyle.textColor), PorterDuff.Mode.SRC_ATOP)

            val textSearch = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
            val closeSearch = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
            textSearch.setHintTextColor(Color.parseColor(listStyle.textColor))
            textSearch.setTextColor(Color.parseColor(listStyle.textColor))
            closeSearch.imageTintList = ColorStateList.valueOf(Color.parseColor(listStyle.textColor))

            val collapseDrawable = toolbarLayout.collapseIcon
            collapseDrawable?.mutate()
            collapseDrawable?.setColorFilter(Color.parseColor(listStyle.textColor), PorterDuff.Mode.SRC_ATOP)
        }

        if (listStyle?.backgroundImage == true) {
            GlideApp.with(this)
                .load(AndroidUtility.getImageFileFromFolder(activity, MediaType.MANGA))
                .signature(ObjectKey(Utility.getCurrentTimestamp()))
                .into(mangaListBackgroundImage)
        }
    }

    private fun assignAdapter(): RecyclerView.Adapter<*> {
        return when (viewModel.mangaListStyleLiveData.value?.listType ?: ListType.LINEAR) {
            ListType.LINEAR -> {
                mangaListRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                MangaListRvAdapter(activity!!, viewModel.currentList, viewModel.scoreFormat, viewModel.mangaListStyleLiveData.value, handleListAction())
            }
            ListType.GRID -> {
                mangaListRecyclerView.layoutManager = GridLayoutManager(activity, resources.getInteger(R.integer.gridSpan), GridLayoutManager.VERTICAL, false)
                MangaListGridRvAdapter(activity!!, viewModel.currentList, viewModel.scoreFormat, viewModel.mangaListStyleLiveData.value, handleListAction())
            }
        }
    }

    private fun handleListAction() = object : MangaListListener {
        override fun openEditor(entryId: Int) {
            val intent = Intent(activity, MangaListEditorActivity::class.java)
            intent.putExtra(MangaListEditorActivity.INTENT_ENTRY_ID, entryId)
            startActivity(intent)
        }

        override fun openScoreDialog(mediaList: MediaList) {
            val setScoreDialog = SetScoreDialog()
            val bundle = Bundle()
            bundle.putString(SetScoreDialog.BUNDLE_SCORE_FORMAT, viewModel.scoreFormat.name)
            bundle.putDouble(SetScoreDialog.BUNDLE_CURRENT_SCORE, mediaList.score ?: 0.0)
            bundle.putStringArrayList(SetScoreDialog.BUNDLE_ADVANCED_SCORING, viewModel.advancedScoringList)

            if (viewModel.scoreFormat == ScoreFormat.POINT_10_DECIMAL || viewModel.scoreFormat == ScoreFormat.POINT_100) {
                val advancedScoresMap = (mediaList.advancedScores as Map<*, *>)["value"] as LinkedTreeMap<String, Double>
                val advancedScoresList = ArrayList<AdvancedScoresItem>()
                advancedScoresMap.forEach { (key, value) ->
                    advancedScoresList.add(AdvancedScoresItem(key, value))
                }
                bundle.putString(SetScoreDialog.BUNDLE_ADVANCED_SCORES_LIST, viewModel.gson.toJson(advancedScoresList))
            }

            setScoreDialog.arguments = bundle
            setScoreDialog.setListener(object : SetScoreDialog.SetScoreListener {
                override fun passScore(newScore: Double, newAdvancedScores: List<Double>?) {
                    viewModel.updateMangaScore(mediaList.id, newScore, newAdvancedScores)
                }
            })
            setScoreDialog.show(childFragmentManager, null)
        }

        override fun openProgressDialog(mediaList: MediaList, isVolume: Boolean) {
            val setProgressDialog = SetProgressDialog()
            val bundle = Bundle()
            if (isVolume) {
                bundle.putInt(SetProgressDialog.BUNDLE_CURRENT_PROGRESS, mediaList.progressVolumes ?: 0)
                if (mediaList.media?.volumes != null) {
                    bundle.putInt(SetProgressDialog.BUNDLE_TOTAL_EPISODES, mediaList.media?.volumes!!)
                }
            } else {
                bundle.putInt(SetProgressDialog.BUNDLE_CURRENT_PROGRESS, mediaList.progress ?: 0)
                if (mediaList.media?.chapters != null) {
                    bundle.putInt(SetProgressDialog.BUNDLE_TOTAL_EPISODES, mediaList.media?.chapters!!)
                }
            }
            setProgressDialog.arguments = bundle
            setProgressDialog.setListener(object : SetProgressDialog.SetProgressListener {
                override fun passProgress(newProgress: Int) {
                    handleUpdateProgressBehavior(mediaList, newProgress, isVolume)
                }
            })
            setProgressDialog.show(childFragmentManager, null)
        }

        override fun incrementProgress(mediaList: MediaList, isVolume: Boolean) {
            if (isVolume) {
                handleUpdateProgressBehavior(mediaList, mediaList.progressVolumes!! + 1, isVolume)
            } else {
                handleUpdateProgressBehavior(mediaList, mediaList.progress!! + 1, isVolume)
            }
        }

        override fun openBrowsePage(media: Media) {
            if (media.isAdult == true && !viewModel.allowAdultContent) {
                DialogUtility.showToast(activity, R.string.you_are_not_allowed_to_view_this_content)
                return
            }

            DialogUtility.showOptionDialog(
                activity,
                R.string.open_media_page,
                "Do you want to open ${media.title?.userPreferred} page?",
                R.string.open,
                {
                    val intent = Intent(activity, BrowseActivity::class.java)
                    intent.putExtra(BrowseActivity.TARGET_PAGE, BrowsePage.MANGA.name)
                    intent.putExtra(BrowseActivity.LOAD_ID, media.id)
                    startActivity(intent)
                },
                R.string.cancel,
                { }
            )
        }

        override fun showDetail(entryId: Int) {
            val mediaList = viewModel.currentList.find { it.id == entryId } ?: return
            val dialog = MediaListDetailDialog()
            val bundle = Bundle()
            bundle.putString(MediaListDetailDialog.MEDIA_LIST_ITEM, viewModel.gson.toJson(mediaList))
            bundle.putString(MediaListDetailDialog.SCORE_FORMAT, viewModel.scoreFormat.name)
            bundle.putBoolean(MediaListDetailDialog.USE_ADVANCED_SCORES, viewModel.advancedScoringEnabled)
            dialog.arguments = bundle
            dialog.show(childFragmentManager, null)
        }
    }

    private fun handleUpdateProgressBehavior(mediaList: MediaList, newProgress: Int, isVolume: Boolean) {
        if (mediaList.progress == newProgress) {
            return
        }

        var status = mediaList.status
        var repeat = mediaList.repeat
        val maxLimit = if (isVolume) mediaList.media?.volumes else mediaList.media?.chapters

        if (newProgress == maxLimit) {
            DialogUtility.showOptionDialog(
                activity,
                R.string.move_to_completed,
                R.string.do_you_want_to_set_this_entry_into_completed,
                R.string.move,
                {
                    if (status == MediaListStatus.REPEATING) {
                        repeat = repeat!! + 1
                    }
                    status = MediaListStatus.COMPLETED
                    viewModel.updateMangaProgress(mediaList.id, status!!, repeat!!, newProgress, isVolume)
                },
                R.string.stay,
                {
                    viewModel.updateMangaProgress(mediaList.id, status!!, repeat!!, newProgress, isVolume)
                }
            )
        } else {
            when (mediaList.status) {
                MediaListStatus.PLANNING, MediaListStatus.PAUSED, MediaListStatus.DROPPED -> {
                    DialogUtility.showOptionDialog(
                        activity,
                        R.string.move_to_watching,
                        R.string.do_you_want_to_set_this_entry_into_watching,
                        R.string.move,
                        {
                            status = MediaListStatus.CURRENT
                            viewModel.updateMangaProgress(mediaList.id, status!!, repeat!!, newProgress, isVolume)
                        },
                        R.string.stay,
                        {
                            viewModel.updateMangaProgress(mediaList.id, status!!, repeat!!, newProgress, isVolume)
                        }
                    )
                }
                MediaListStatus.COMPLETED -> {
                    status = MediaListStatus.CURRENT
                    viewModel.updateMangaProgress(mediaList.id, status!!, repeat!!, newProgress, isVolume)
                }
                else -> {
                    viewModel.updateMangaProgress(mediaList.id, status!!, repeat!!, newProgress, isVolume)
                }
            }
        }
    }
}
