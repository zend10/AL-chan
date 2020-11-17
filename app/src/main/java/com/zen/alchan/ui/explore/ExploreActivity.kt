package com.zen.alchan.ui.explore

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jakewharton.rxbinding2.widget.RxTextView
import com.zen.alchan.R
import com.zen.alchan.helper.doOnApplyWindowInsets
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.pojo.FilterRange
import com.zen.alchan.helper.pojo.MediaFilterData
import com.zen.alchan.helper.pojo.SearchResult
import com.zen.alchan.helper.updateSidePadding
import com.zen.alchan.helper.updateTopPadding
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.base.BaseActivity
import com.zen.alchan.ui.browse.BrowseActivity
import com.zen.alchan.ui.explore.adapter.ExploreAnimeRvAdapter
import com.zen.alchan.ui.explore.adapter.ExploreMangaRvAdapter
import com.zen.alchan.ui.filter.MediaFilterActivity
import com.zen.alchan.ui.search.SearchListener
import com.zen.alchan.ui.search.adapter.SearchCharactersRvAdapter
import com.zen.alchan.ui.search.adapter.SearchStaffsRvAdapter
import com.zen.alchan.ui.search.adapter.SearchStudioRvAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_explore.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_loading.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaSeason
import java.util.concurrent.TimeUnit

class ExploreActivity : BaseActivity() {

    private val viewModel by viewModel<ExploreViewModel>()

    private lateinit var adapter: RecyclerView.Adapter<*>
    private var isLoading = false
    private var searchKeyWord = ""
    private var disposable: Disposable? = null

    companion object {
        const val EXPLORE_PAGE = "explorePage"

        const val SELECTED_TAG = "selectedTag"
        const val SELECTED_GENRE = "selectedGenre"
        const val SELECTED_SEASON = "selectedSeason"

        // use this as request code in want to do something with the search result
        const val ACTIVITY_EXPLORE = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore)

        browseLayout.doOnApplyWindowInsets { view, windowInsets, initialPadding ->
            view.updateTopPadding(windowInsets, initialPadding)
            view.updateSidePadding(windowInsets, initialPadding)
        }

        if (viewModel.selectedExplorePage == null) {
            viewModel.selectedExplorePage = BrowsePage.valueOf(intent.getStringExtra(EXPLORE_PAGE))
        }

        if (viewModel.filterData == null && intent?.getStringExtra(SELECTED_TAG) != null) {
            viewModel.filterData = MediaFilterData(selectedTagNames = arrayListOf(intent?.getStringExtra(SELECTED_TAG)!!))
        }

        if (viewModel.filterData == null && intent?.getStringExtra(SELECTED_GENRE) != null) {
            viewModel.filterData = MediaFilterData(selectedGenres = arrayListOf(intent?.getStringExtra(SELECTED_GENRE)!!))
        }

        if (viewModel.filterData == null && intent?.getStringExtra(SELECTED_SEASON) != null) {
            val seasonAndYear = intent?.getStringExtra(SELECTED_SEASON)!!.split(" ")
            val season = MediaSeason.valueOf(seasonAndYear[0])
            val year = seasonAndYear[1]
            viewModel.filterData = MediaFilterData(selectedSeason = season, selectedYear = FilterRange("${year}0101".toInt(), "${year}1231".toInt()))
        }

        assignAdapter()

        initLayout()
        setupObserver()
    }

    private fun setupObserver() {
        viewModel.searchAnimeResponse.observe(this, Observer {
            if (viewModel.selectedExplorePage != BrowsePage.ANIME) return@Observer
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                    if (!handleSearchResult(it.data?.page?.pageInfo?.hasNextPage == true)) return@Observer
                    it.data?.page?.media?.forEach { anime -> viewModel.searchResultList.add(SearchResult(animeSearchResult = anime)) }
                    adapter.notifyDataSetChanged()
                    emptyLayout.visibility = if (viewModel.searchResultList.isNullOrEmpty()) View.VISIBLE else View.GONE
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    handleSearchError(it.message)
                }
            }
        })

        viewModel.searchMangaResponse.observe(this, Observer {
            if (viewModel.selectedExplorePage != BrowsePage.MANGA) return@Observer
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                    if (!handleSearchResult(it.data?.page?.pageInfo?.hasNextPage == true)) return@Observer
                    it.data?.page?.media?.forEach { manga -> viewModel.searchResultList.add(SearchResult(mangaSearchResult = manga)) }
                    adapter.notifyDataSetChanged()
                    emptyLayout.visibility = if (viewModel.searchResultList.isNullOrEmpty()) View.VISIBLE else View.GONE
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    handleSearchError(it.message)
                }
            }
        })

        viewModel.searchCharactersResponse.observe(this, Observer {
            if (viewModel.selectedExplorePage != BrowsePage.CHARACTER) return@Observer
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                    if (!handleSearchResult(it.data?.page?.pageInfo?.hasNextPage == true)) return@Observer
                    it.data?.page?.characters?.forEach { character -> viewModel.searchResultList.add(SearchResult(charactersSearchResult = character)) }
                    adapter.notifyDataSetChanged()
                    emptyLayout.visibility = if (viewModel.searchResultList.isNullOrEmpty()) View.VISIBLE else View.GONE
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    handleSearchError(it.message)
                }
            }
        })

        viewModel.searchStaffsResponse.observe(this, Observer {
            if (viewModel.selectedExplorePage != BrowsePage.STAFF) return@Observer
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                    if (!handleSearchResult(it.data?.page?.pageInfo?.hasNextPage == true)) return@Observer
                    it.data?.page?.staff?.forEach { staff -> viewModel.searchResultList.add(SearchResult(staffsSearchResult = staff)) }
                    adapter.notifyDataSetChanged()
                    emptyLayout.visibility = if (viewModel.searchResultList.isNullOrEmpty()) View.VISIBLE else View.GONE
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    handleSearchError(it.message)
                }
            }
        })

        viewModel.searchStudiosResponse.observe(this, Observer {
            if (viewModel.selectedExplorePage != BrowsePage.STUDIO) return@Observer
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                    if (!handleSearchResult(it.data?.page?.pageInfo?.hasNextPage == true)) return@Observer
                    it.data?.page?.studios?.forEach { studio -> viewModel.searchResultList.add(SearchResult(studiosSearchResult = studio)) }
                    adapter.notifyDataSetChanged()
                    emptyLayout.visibility = if (viewModel.searchResultList.isNullOrEmpty()) View.VISIBLE else View.GONE
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    handleSearchError(it.message)
                }
            }
        })

        if (!viewModel.isInit) {
            viewModel.search(searchKeyWord)
            loadingLayout.visibility = View.VISIBLE
        }
    }

    private fun initLayout() {
        searchBackButton.setOnClickListener { finish() }

        if (disposable == null) {
            disposable = RxTextView.textChanges(searchBarEditText)
                .debounce(800, TimeUnit.MILLISECONDS)
                .skip(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { string -> if (viewModel.isInit) handleSearch(string?.toString()) }
        }

        searchSettingButton.visibility = if (viewModel.selectedExplorePage == BrowsePage.ANIME || viewModel.selectedExplorePage == BrowsePage.MANGA) {
            View.VISIBLE
        } else {
            View.GONE
        }

        searchSettingButton.setOnClickListener {
            if (viewModel.selectedExplorePage != BrowsePage.ANIME && viewModel.selectedExplorePage != BrowsePage.MANGA) {
                return@setOnClickListener
            }

            val intent = Intent(this, MediaFilterActivity::class.java)
            intent.putExtra(MediaFilterActivity.MEDIA_TYPE, viewModel.selectedExplorePage?.name)
            intent.putExtra(MediaFilterActivity.IS_EXPLORE, true)
            intent.putExtra(MediaFilterActivity.FILTER_DATA, viewModel.gson.toJson(viewModel.filterData))
            startActivityForResult(intent, MediaFilterActivity.ACTIVITY_FILTER)
        }

        exploreTypeText.text = viewModel.selectedExplorePage?.name

        exploreTypeText.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setItems(viewModel.explorePageArray) { _, which ->
                    viewModel.selectedExplorePage = BrowsePage.valueOf(viewModel.explorePageArray[which])
                    exploreTypeText.text = viewModel.selectedExplorePage?.name
                    assignAdapter()
                    handleSearch(searchKeyWord)

                    searchSettingButton.visibility = if (viewModel.selectedExplorePage == BrowsePage.ANIME || viewModel.selectedExplorePage == BrowsePage.MANGA) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                }
                .show()
        }

        exploreRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.canScrollVertically(1) && viewModel.isInit && !isLoading) {
                    loadMore()
                    isLoading = true
                }
            }
        })
    }

    private fun loadMore() {
        if (viewModel.hasNextPage) {
            viewModel.searchResultList.add(null)
            adapter.notifyItemInserted(viewModel.searchResultList.lastIndex)
            viewModel.search(searchKeyWord)
        }
    }

    private fun handleSearch(newKeyWord: String?) {
        loadingLayout.visibility = View.VISIBLE

        searchKeyWord = newKeyWord ?: ""
        isLoading = false

        viewModel.searchResultList.clear()
        viewModel.page = 1
        viewModel.hasNextPage = true

        viewModel.search(searchKeyWord)
    }

    private fun assignAdapter() {
        adapter = when (viewModel.selectedExplorePage) {
            BrowsePage.ANIME -> ExploreAnimeRvAdapter(this, viewModel.searchResultList, searchListenerObject(BrowsePage.ANIME))
            BrowsePage.MANGA -> ExploreMangaRvAdapter(this, viewModel.searchResultList, searchListenerObject(BrowsePage.MANGA))
            BrowsePage.CHARACTER -> SearchCharactersRvAdapter(this, viewModel.searchResultList, true, searchListenerObject(BrowsePage.CHARACTER))
            BrowsePage.STAFF -> SearchStaffsRvAdapter(this, viewModel.searchResultList, true, searchListenerObject(BrowsePage.STAFF))
            BrowsePage.STUDIO -> SearchStudioRvAdapter(this, viewModel.searchResultList, true, searchListenerObject(BrowsePage.STUDIO))
            else -> ExploreAnimeRvAdapter(this, viewModel.searchResultList, searchListenerObject(BrowsePage.ANIME))
        }

        exploreRecyclerView.adapter = adapter
    }

    private fun searchListenerObject(explorePage: BrowsePage) = object : SearchListener {
        override fun passSelectedItem(id: Int) {
            if (intent?.getStringExtra(SELECTED_TAG) != null ||
                intent.getStringExtra(SELECTED_GENRE) != null ||
                intent.getStringExtra(SELECTED_SEASON) != null
            ) {
                val intent = Intent()
                intent.putExtra(BrowseActivity.TARGET_PAGE, explorePage.name)
                intent.putExtra(BrowseActivity.LOAD_ID, id)
                setResult(Activity.RESULT_OK, intent)
                finish()
                return
            }

            val intent = Intent(this@ExploreActivity, BrowseActivity::class.java)
            intent.putExtra(BrowseActivity.TARGET_PAGE, explorePage.name)
            intent.putExtra(BrowseActivity.LOAD_ID, id)
            startActivity(intent)
        }
    }

    private fun handleSearchResult(hasNextPage: Boolean): Boolean {
        if (isLoading) {
            viewModel.searchResultList.removeAt(viewModel.searchResultList.lastIndex)
            adapter.notifyItemRemoved(viewModel.searchResultList.size)
            isLoading = false
        }

        if (!viewModel.hasNextPage) {
            return false
        }

        viewModel.page += 1
        viewModel.isInit = true
        viewModel.hasNextPage = hasNextPage

        return true
    }

    private fun handleSearchError(errorMessage: String?) {
        DialogUtility.showToast(this, errorMessage)

        if (isLoading) {
            viewModel.searchResultList.removeAt(viewModel.searchResultList.lastIndex)
            adapter.notifyItemRemoved(viewModel.searchResultList.size)
            isLoading = false
        }

        emptyLayout.visibility = if (viewModel.searchResultList.isNullOrEmpty()) View.VISIBLE else View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == MediaFilterActivity.ACTIVITY_FILTER && resultCode == Activity.RESULT_OK) {
            if (data?.extras != null && data.extras?.getString(MediaFilterActivity.FILTER_DATA) != null) {
                viewModel.filterData = viewModel.gson.fromJson(data.extras?.getString(MediaFilterActivity.FILTER_DATA), MediaFilterData::class.java)
            } else {
                viewModel.filterData = null
            }

            handleSearch(searchKeyWord)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}
