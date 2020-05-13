package com.zen.alchan.ui.animelist


import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
import com.zen.alchan.ui.animelist.editor.AnimeListEditorActivity
import com.zen.alchan.ui.animelist.list.AnimeListGridRvAdapter
import com.zen.alchan.ui.animelist.list.AnimeListListener
import com.zen.alchan.ui.animelist.list.AnimeListRvAdapter
import com.zen.alchan.ui.browse.BrowseActivity
import com.zen.alchan.ui.common.SetProgressDialog
import com.zen.alchan.ui.common.SetScoreDialog
import com.zen.alchan.ui.common.customise.CustomiseListActivity
import com.zen.alchan.ui.common.filter.MediaFilterBottomSheet
import io.reactivex.subjects.PublishSubject
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
    private lateinit var itemList: MenuItem
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

        toolbarLayout.apply {
            title = getString(R.string.anime_list)
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

                    viewModel.currentList = ArrayList(viewModel.animeListData.value?.lists?.find { it.name == currentTab.status }?.entries)

                    adapter = assignAdapter()
                    animeListRecyclerView.adapter = adapter

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
            bundle.putString(MediaFilterBottomSheet.BUNDLE_MEDIA_TYPE, MediaType.ANIME.name)
            bundle.putString(MediaFilterBottomSheet.BUNDLE_FILTERED_DATA, viewModel.gson.toJson(viewModel.filteredData))
            filterDialog.arguments = bundle
            filterDialog.show(childFragmentManager, null)
            true
        }

        itemCustomiseList.setOnMenuItemClickListener {
            val intent = Intent(activity, CustomiseListActivity::class.java)
            intent.putExtra(CustomiseListActivity.MEDIA_TYPE, MediaType.ANIME.name)
            startActivity(intent)
            true
        }

        adapter = assignAdapter()
        animeListRecyclerView.adapter = adapter

        initLayout()
        setupObserver()
    }

    private fun setupObserver() {
        viewModel.animeListDataResponse.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> {
                    animeListRefreshLayout.isRefreshing = false
                    loadingLayout.visibility = View.VISIBLE
                }
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                }
            }
        })

        viewModel.animeListData.observe(viewLifecycleOwner, Observer {
            viewModel.tabItemList.clear()
            it.lists?.forEach { list -> viewModel.tabItemList.add(MediaListTabItem(list.name!!, list.entries?.size!!)) }

            val currentTab = viewModel.tabItemList[viewModel.selectedTab]
            toolbarLayout.subtitle = "${currentTab.status} (${currentTab.count})"

            viewModel.currentList.clear()
            viewModel.currentList.addAll(ArrayList(it.lists?.find { it.name == currentTab.status }?.entries))
            adapter.notifyDataSetChanged()

            if (viewModel.currentList.isNullOrEmpty()) {
                emptyLayout.visibility = View.VISIBLE
            } else {
                emptyLayout.visibility = View.GONE
            }
        })

        viewModel.animeListStyleLiveData.observe(viewLifecycleOwner, Observer {
            adapter = assignAdapter()
            animeListRecyclerView.adapter = adapter
            initLayout()

            if (viewModel.currentList.isNullOrEmpty()) {
                emptyLayout.visibility = View.VISIBLE
            } else {
                emptyLayout.visibility = View.GONE
            }
        })

        viewModel.initData()
    }

    private fun initLayout() {
        animeListRefreshLayout.setOnRefreshListener {
            loadingLayout.visibility = View.VISIBLE
            viewModel.retrieveAnimeListData()
        }

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
                .load(AndroidUtility.getImageFileFromFolder(activity, MediaType.ANIME))
                .signature(ObjectKey(Utility.getCurrentTimestamp()))
                .into(animeListBackgroundImage)
        }
    }

    private fun assignAdapter(): RecyclerView.Adapter<*> {
        return when (viewModel.animeListStyleLiveData.value?.listType ?: ListType.LINEAR) {
            ListType.LINEAR -> {
                animeListRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                AnimeListRvAdapter(activity!!, viewModel.currentList, viewModel.scoreFormat, viewModel.animeListStyleLiveData.value, handleListAction())
            }
            ListType.GRID -> {
                animeListRecyclerView.layoutManager = GridLayoutManager(activity, 3, GridLayoutManager.VERTICAL, false)
                AnimeListGridRvAdapter(activity!!, viewModel.currentList, viewModel.scoreFormat, viewModel.animeListStyleLiveData.value, handleListAction())
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

            DialogUtility.showOptionDialog(
                activity,
                R.string.open_media_page,
                "Do you want to open ${media.title?.userPreferred} page?",
                R.string.open,
                {
                    val intent = Intent(activity, BrowseActivity::class.java)
                    intent.putExtra(BrowseActivity.TARGET_PAGE, BrowsePage.ANIME.name)
                    intent.putExtra(BrowseActivity.LOAD_ID, media.id)
                    startActivity(intent)
                },
                R.string.cancel,
                { }
            )
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
                activity,
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
                        activity,
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
}
