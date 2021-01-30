package com.zen.alchan.ui.animelist


import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.signature.ObjectKey
import com.google.gson.internal.LinkedTreeMap

import com.zen.alchan.R
import com.zen.alchan.data.response.Media
import com.zen.alchan.data.response.MediaList
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.doOnApplyWindowInsets
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.enums.ListType
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.AdvancedScoresItem
import com.zen.alchan.helper.pojo.MediaFilterData
import com.zen.alchan.helper.pojo.MediaListTabItem
import com.zen.alchan.helper.updateTopPadding
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.helper.utils.Utility
import com.zen.alchan.ui.animelist.editor.AnimeListEditorActivity
import com.zen.alchan.ui.animelist.list.*
import com.zen.alchan.ui.browse.BrowseActivity
import com.zen.alchan.ui.common.MediaListDetailDialog
import com.zen.alchan.ui.common.SetProgressDialog
import com.zen.alchan.ui.common.SetScoreDialog
import com.zen.alchan.ui.common.customise.CustomiseListActivity
import com.zen.alchan.ui.filter.MediaFilterActivity
import kotlinx.android.synthetic.main.fragment_anime_list.*
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
class AnimeListFragment : Fragment() {

    private val viewModel by viewModel<AnimeListViewModel>()

    private lateinit var adapter: RecyclerView.Adapter<*>
    private lateinit var itemSearch: MenuItem
    private lateinit var itemCustomiseList: MenuItem
    private lateinit var itemFilter: MenuItem

    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_anime_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        toolbarLayout.doOnApplyWindowInsets { view, windowInsets, initialPadding ->
            view.updateTopPadding(windowInsets, initialPadding)
        }

        toolbarLayout.apply {
            title = getString(R.string.anime_list)
            inflateMenu(R.menu.menu_media_list)
            itemSearch = menu.findItem(R.id.itemSearch)
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
                handleSearch(newText)
                return true
            }
        })

        itemFilter.setOnMenuItemClickListener {
            val intent = Intent(activity, MediaFilterActivity::class.java)
            intent.putExtra(MediaFilterActivity.MEDIA_TYPE, MediaType.ANIME.name)
            intent.putExtra(MediaFilterActivity.FILTER_DATA, viewModel.gson.toJson(viewModel.filterData))
            intent.putExtra(MediaFilterActivity.SCORE_FORMAT, viewModel.scoreFormat.name)
            startActivityForResult(intent, MediaFilterActivity.ACTIVITY_FILTER)
            true
        }

        itemCustomiseList.setOnMenuItemClickListener {
            val intent = Intent(activity, CustomiseListActivity::class.java)
            intent.putExtra(CustomiseListActivity.MEDIA_TYPE, MediaType.ANIME.name)
            startActivity(intent)
            true
        }

        assignAdapter()

        initLayout()
        setupObserver()
    }

    private fun setupObserver() {
        viewModel.animeListDataResponse.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> loadingLayout.visibility = View.GONE
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                }
            }
        })

        viewModel.animeListData.observe(viewLifecycleOwner, Observer {
            viewModel.tabItemList.clear()
            it.lists?.forEach { list -> viewModel.tabItemList.add(MediaListTabItem(list.name!!, list.entries?.size!!)) }

            if (viewModel.tabItemList.isNullOrEmpty()) {
                Constant.DEFAULT_ANIME_LIST_ORDER.forEach { list ->
                    viewModel.tabItemList.add(MediaListTabItem(list, 0))
                }
            }

            viewModel.tabItemList.add(0, MediaListTabItem(getString(R.string.all), it.lists?.sumBy { list -> list.entries?.size ?: 0 } ?: 0))

            if (viewModel.selectedTab >= viewModel.tabItemList.size) {
                viewModel.selectedTab = viewModel.tabItemList.size - 1
            }

            val currentTab = viewModel.tabItemList[viewModel.selectedTab]
            toolbarLayout.subtitle = "${currentTab.status} (${currentTab.count})"

            handleSearch(searchView.query.toString())
        })

        viewModel.animeListStyleLiveData.observe(viewLifecycleOwner, Observer {
            assignAdapter()
            initLayout()

            if (viewModel.currentList.isNullOrEmpty()) {
                emptyLayout.visibility = View.VISIBLE
            } else {
                emptyLayout.visibility = View.GONE
            }
        })

        viewModel.updateAnimeListEntryResponse.observe(viewLifecycleOwner, Observer {
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
        animeListRefreshLayout.setOnRefreshListener {
            animeListRefreshLayout.isRefreshing = false
            viewModel.retrieveAnimeListData()
        }

        animeListRearrangeButton.setOnClickListener {
            AlertDialog.Builder(requireActivity())
                .setItems(viewModel.getTabItemArray()) { _, which ->
                    viewModel.selectedTab = which

                    val currentTab = viewModel.tabItemList[viewModel.selectedTab]
                    toolbarLayout.subtitle = "${currentTab.status} (${currentTab.count})"

                    handleSearch(searchView.query.toString())
                }
                .show()
        }

        animeListRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    animeListRearrangeButton.hide()
                } else {
                    animeListRearrangeButton.show()
                }
            }
        })

        val listStyle = viewModel.animeListStyleLiveData.value
        if (listStyle?.toolbarColor != null) {
            toolbarLayout.setBackgroundColor(Color.parseColor(listStyle.toolbarColor))
            toolbarLayout.backgroundTintList = ColorStateList.valueOf(Color.parseColor(listStyle.toolbarColor))
        }

        if (listStyle?.backgroundColor != null) {
            animeListLayout.setBackgroundColor(Color.parseColor(listStyle.backgroundColor))
        }

        if (listStyle?.textColor != null) {
            toolbarLayout.setTitleTextColor(Color.parseColor(listStyle.textColor))
            toolbarLayout.setSubtitleTextColor(Color.parseColor(listStyle.textColor))

            val searchDrawable = itemSearch.icon
            searchDrawable.mutate()
            searchDrawable.setColorFilter(Color.parseColor(listStyle.textColor), PorterDuff.Mode.SRC_ATOP)

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

        if (listStyle?.floatingButtonColor != null) {
            animeListRearrangeButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor(listStyle.floatingButtonColor))
        }

        if (listStyle?.floatingIconColor != null) {
            animeListRearrangeButton.imageTintList = ColorStateList.valueOf(Color.parseColor(listStyle.floatingIconColor))
        }

        if (listStyle?.backgroundImage == true) {
            GlideApp.with(this)
                .load(AndroidUtility.getImageFileFromFolder(activity, MediaType.ANIME))
                .signature(ObjectKey(Utility.getCurrentTimestamp()))
                .into(animeListBackgroundImage)
        }
    }

    private fun handleSearch(query: String?) {
        viewModel.currentList.clear()
        var lastItemIsTitle = false
        val selectedList = viewModel.getSelectedList()
        selectedList.forEachIndexed { index, filtered ->
            if (filtered.id == 0) {
                if (lastItemIsTitle) {
                    viewModel.currentList.removeAt(viewModel.currentList.lastIndex)
                }
                viewModel.currentList.add(filtered)
                lastItemIsTitle = true
            } else if (
                filtered.media?.title?.romaji?.toLowerCase()?.contains(query ?: "") == true ||
                filtered.media?.title?.english?.toLowerCase()?.contains(query ?: "") == true ||
                filtered.media?.title?.native?.toLowerCase()?.contains(query ?: "") == true ||
                filtered.media?.synonyms?.find { synonym -> synonym?.toLowerCase()?.contains(query ?: "") == true } != null
            ) {
                viewModel.currentList.add(filtered)
                lastItemIsTitle = false
            } else if (index == selectedList.lastIndex && lastItemIsTitle) {
                viewModel.currentList.removeAt(viewModel.currentList.lastIndex)
            }
        }

        adapter.notifyDataSetChanged()

        if (viewModel.currentList.isNullOrEmpty()) {
            emptyLayout.visibility = View.VISIBLE
        } else {
            emptyLayout.visibility = View.GONE
        }
    }

    private fun assignAdapter() {
        adapter = when (viewModel.animeListStyleLiveData.value?.listType ?: ListType.LINEAR) {
            ListType.LINEAR -> {
                animeListRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                AnimeListRvAdapter(activity!!, viewModel.currentList, viewModel.scoreFormat, viewModel.animeListStyleLiveData.value, viewModel.useRelativeDate, handleListAction())
            }
            ListType.GRID -> {
                animeListRecyclerView.layoutManager = GridLayoutManager(activity, resources.getInteger(R.integer.gridSpan), GridLayoutManager.VERTICAL, false)
                AnimeListGridRvAdapter(activity!!, viewModel.currentList, viewModel.scoreFormat, viewModel.animeListStyleLiveData.value, viewModel.useRelativeDate, handleListAction())
            }
            ListType.SIMPLIFIED -> {
                animeListRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                AnimeListSimplifiedRvAdapter(activity!!, viewModel.currentList, viewModel.scoreFormat, viewModel.animeListStyleLiveData.value, viewModel.useRelativeDate, handleListAction())
            }
            ListType.ALBUM -> {
                animeListRecyclerView.layoutManager = GridLayoutManager(activity, resources.getInteger(R.integer.gridSpan), GridLayoutManager.VERTICAL, false)
                AnimeListAlbumRvAdapter(activity!!, viewModel.currentList, viewModel.scoreFormat, viewModel.animeListStyleLiveData.value, viewModel.useRelativeDate, handleListAction())
            }
        }

        animeListRecyclerView.adapter = adapter

        if (viewModel.animeListStyleLiveData.value?.listType == ListType.GRID || viewModel.animeListStyleLiveData.value?.listType == ListType.ALBUM) {
            (animeListRecyclerView.layoutManager as GridLayoutManager).spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (adapter.getItemViewType(position) == AnimeListGridRvAdapter.VIEW_TYPE_TITLE) {
                        resources.getInteger(R.integer.gridSpan)
                    } else {
                        1
                    }
                }
            }
        }
    }

    private fun handleListAction() = object : AnimeListListener {
        override fun openEditor(entryId: Int) {
            val intent = Intent(activity, AnimeListEditorActivity::class.java)
            intent.putExtra(AnimeListEditorActivity.INTENT_ENTRY_ID, entryId)
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
                    viewModel.updateAnimeScore(mediaList.id, newScore, newAdvancedScores)
                }
            })
            setScoreDialog.show(childFragmentManager, null)
        }

        override fun openProgressDialog(mediaList: MediaList) {
            val setProgressDialog = SetProgressDialog()
            val bundle = Bundle()
            bundle.putInt(SetProgressDialog.BUNDLE_CURRENT_PROGRESS, mediaList.progress ?: 0)
            if (mediaList.media?.episodes != null) {
                bundle.putInt(SetProgressDialog.BUNDLE_TOTAL_EPISODES, mediaList.media?.episodes!!)
            }
            setProgressDialog.arguments = bundle
            setProgressDialog.setListener(object : SetProgressDialog.SetProgressListener {
                override fun passProgress(newProgress: Int) {
                    handleUpdateProgressBehavior(mediaList, newProgress)
                }
            })
            setProgressDialog.show(childFragmentManager, null)
        }

        override fun incrementProgress(mediaList: MediaList) {
            handleUpdateProgressBehavior(mediaList, mediaList.progress!! + 1)
        }

        override fun openBrowsePage(media: Media) {
            if (media.isAdult == true && !viewModel.allowAdultContent) {
                DialogUtility.showToast(activity, R.string.you_are_not_allowed_to_view_this_content)
                return
            }

            val intent = Intent(activity, BrowseActivity::class.java)
            intent.putExtra(BrowseActivity.TARGET_PAGE, BrowsePage.ANIME.name)
            intent.putExtra(BrowseActivity.LOAD_ID, media.id)
            startActivity(intent)
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

    private fun handleUpdateProgressBehavior(mediaList: MediaList, newProgress: Int) {
        if (mediaList.progress == newProgress) {
            return
        }

        var status = mediaList.status
        var repeat = mediaList.repeat

        if (newProgress == mediaList.media?.episodes) {
            DialogUtility.showOptionDialog(
                requireActivity(),
                R.string.move_to_completed,
                R.string.do_you_want_to_set_this_entry_into_completed,
                R.string.move,
                {
                    if (status == MediaListStatus.REPEATING) {
                        repeat = repeat!! + 1
                    }
                    status = MediaListStatus.COMPLETED
                    viewModel.updateAnimeProgress(mediaList.id, status!!, repeat!!, newProgress)
                },
                R.string.stay,
                {
                    viewModel.updateAnimeProgress(mediaList.id, status!!, repeat!!, newProgress)
                }
            )
        } else {
            when (mediaList.status) {
                MediaListStatus.PLANNING, MediaListStatus.PAUSED, MediaListStatus.DROPPED -> {
                    DialogUtility.showOptionDialog(
                        requireActivity(),
                        R.string.move_to_watching,
                        R.string.do_you_want_to_set_this_entry_into_watching,
                        R.string.move,
                        {
                            status = MediaListStatus.CURRENT
                            viewModel.updateAnimeProgress(mediaList.id, status!!, repeat!!, newProgress)
                        },
                        R.string.stay,
                        {
                            viewModel.updateAnimeProgress(mediaList.id, status!!, repeat!!, newProgress)
                        }
                    )
                }
                MediaListStatus.COMPLETED -> {
                    status = MediaListStatus.CURRENT
                    viewModel.updateAnimeProgress(mediaList.id, status!!, repeat!!, newProgress)
                }
                else -> {
                    viewModel.updateAnimeProgress(mediaList.id, status!!, repeat!!, newProgress)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == MediaFilterActivity.ACTIVITY_FILTER && resultCode == Activity.RESULT_OK) {
            val filterData = if (data?.extras != null && data.extras?.getString(MediaFilterActivity.FILTER_DATA) != null) {
                viewModel.gson.fromJson(data.extras?.getString(MediaFilterActivity.FILTER_DATA), MediaFilterData::class.java)
            } else {
                null
            }
            viewModel.setFilteredData(filterData)
            initLayout()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
