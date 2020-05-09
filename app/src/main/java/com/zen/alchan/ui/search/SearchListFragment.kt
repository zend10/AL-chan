package com.zen.alchan.ui.search


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView

import com.zen.alchan.R
import com.zen.alchan.data.network.Resource
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.pojo.SearchResult
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.browse.BrowseActivity
import com.zen.alchan.ui.browse.media.MediaFragment
import com.zen.alchan.ui.search.adapter.*
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.fragment_search_list.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_loading.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaType

/**
 * A simple [Fragment] subclass.
 */
class SearchListFragment : Fragment() {

    private val viewModel by viewModel<SearchListViewModel>()

    private lateinit var adapter: RecyclerView.Adapter<*>
    private var isLoading = false

    private var disposable: Disposable? = null
    private var searchKeyWord = ""

    companion object {
        const val SEARCH_PAGE = "searchPage"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.searchPage = BrowsePage.valueOf(arguments?.getString(SEARCH_PAGE)!!)
        assignAdapter()

        initLayout()
        setupObserver()
    }

    private fun setupObserver() {
        viewModel.getObserver()?.observe(viewLifecycleOwner, Observer {
            loadingLayout.visibility = View.GONE

            when ((it as Resource<*>?)?.responseStatus) {
                ResponseStatus.SUCCESS -> {
                    if (isLoading) {
                        viewModel.searchResultList.removeAt(viewModel.searchResultList.lastIndex)
                        adapter.notifyItemRemoved(viewModel.searchResultList.size)
                        isLoading = false
                    }

                    if (!viewModel.hasNextPage) {
                        return@Observer
                    }

                    viewModel.page += 1
                    viewModel.isInit = true

                    when (viewModel.searchPage) {
                        BrowsePage.ANIME -> {
                            val searchAnimeResponse = it as Resource<SearchAnimeQuery.Data>
                            viewModel.hasNextPage = searchAnimeResponse.data?.page?.pageInfo?.hasNextPage ?: false
                            searchAnimeResponse.data?.page?.media?.forEach { anime ->
                                viewModel.searchResultList.add(SearchResult(animeSearchResult = anime))
                            }
                        }
                        BrowsePage.MANGA -> {
                            val searchMangaResponse = it as Resource<SearchMangaQuery.Data>
                            viewModel.hasNextPage = searchMangaResponse.data?.page?.pageInfo?.hasNextPage ?: false
                            searchMangaResponse.data?.page?.media?.forEach { manga ->
                                viewModel.searchResultList.add(SearchResult(mangaSearchResult =  manga))
                            }
                        }
                        BrowsePage.CHARACTER -> {
                            val searchCharactersResponse = it as Resource<SearchCharactersQuery.Data>
                            viewModel.hasNextPage = searchCharactersResponse.data?.page?.pageInfo?.hasNextPage ?: false
                            searchCharactersResponse.data?.page?.characters?.forEach { character ->
                                viewModel.searchResultList.add(SearchResult(charactersSearchResult =  character))
                            }
                        }
                        BrowsePage.STAFF -> {
                            val searchStaffsResponse = it as Resource<SearchStaffsQuery.Data>
                            viewModel.hasNextPage = searchStaffsResponse.data?.page?.pageInfo?.hasNextPage ?: false
                            searchStaffsResponse.data?.page?.staff?.forEach { staff ->
                                viewModel.searchResultList.add(SearchResult(staffsSearchResult =  staff))
                            }
                        }
                        BrowsePage.STUDIO -> {
                            val searchStudiosResponse = it as Resource<SearchStudiosQuery.Data>
                            viewModel.hasNextPage = searchStudiosResponse.data?.page?.pageInfo?.hasNextPage ?: false
                            searchStudiosResponse.data?.page?.studios?.forEach { studio ->
                                viewModel.searchResultList.add(SearchResult(studiosSearchResult =  studio))
                            }
                        }
                    }

                    adapter.notifyDataSetChanged()
                    emptyLayout.visibility = if (viewModel.searchResultList.isNullOrEmpty()) View.VISIBLE else View.GONE
                }
                ResponseStatus.ERROR -> {
                    DialogUtility.showToast(activity, it?.message)
                    if (isLoading) {
                        viewModel.searchResultList.removeAt(viewModel.searchResultList.lastIndex)
                        adapter.notifyItemRemoved(viewModel.searchResultList.size)
                        isLoading = false
                    }
                    emptyLayout.visibility = if (viewModel.searchResultList.isNullOrEmpty()) View.VISIBLE else View.GONE
                    if (!viewModel.isInit) {
                        retryButton.visibility = View.VISIBLE
                        retryButton.setOnClickListener {
                            if (searchKeyWord.isNotBlank()) {
                                viewModel.search(searchKeyWord)
                            }
                        }
                    } else {
                        retryButton.visibility = View.GONE
                    }
                }
            }
        })

        if (!viewModel.isInit) {
            if (searchKeyWord.isNotBlank()) {
                viewModel.search(searchKeyWord)
                loadingLayout.visibility = View.VISIBLE
            } else {
                emptyLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun initLayout() {
        searchResultRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.canScrollVertically(1) && viewModel.isInit && !isLoading) {
                    loadMore()
                    isLoading = true
                }
            }
        })

        // Handle search view with Rx
        if (activity is SearchActivity && disposable == null) {
            (activity as SearchActivity).source.subscribe(object : io.reactivex.Observer<String> {
                override fun onSubscribe(d: Disposable) { disposable = d }

                override fun onNext(t: String) {
                    searchKeyWord = t
                    viewModel.searchResultList.clear()

                    isLoading = false
                    viewModel.page = 1
                    viewModel.hasNextPage = true

                    adapter.notifyDataSetChanged()

                    if (searchKeyWord.isNotBlank()) {
                        viewModel.search(searchKeyWord)
                        loadingLayout.visibility = View.VISIBLE
                    } else {
                        emptyLayout.visibility = View.VISIBLE
                    }
                }

                override fun onError(e: Throwable) { e.printStackTrace() }
                override fun onComplete() { }
            })
        }
    }

    private fun loadMore() {
        if (viewModel.hasNextPage) {
            viewModel.searchResultList.add(null)
            adapter.notifyItemInserted(viewModel.searchResultList.lastIndex)
            viewModel.search(searchKeyWord)
        }
    }

    private fun assignAdapter() {
        adapter = when (viewModel.searchPage) {
            BrowsePage.ANIME -> SearchAnimeRvAdapter(activity!!, viewModel.searchResultList, searchListenerObject(BrowsePage.ANIME))
            BrowsePage.MANGA -> SearchMangaRvAdapter(activity!!, viewModel.searchResultList, searchListenerObject(BrowsePage.MANGA))
            BrowsePage.CHARACTER -> SearchCharactersRvAdapter(activity!!, viewModel.searchResultList, searchListenerObject(BrowsePage.CHARACTER))
            BrowsePage.STAFF -> SearchStaffsRvAdapter(activity!!, viewModel.searchResultList, searchListenerObject(BrowsePage.STAFF))
            BrowsePage.STUDIO -> SearchStudioRvAdapter(activity!!, viewModel.searchResultList, searchListenerObject(BrowsePage.STUDIO))
            else -> SearchAnimeRvAdapter(activity!!, viewModel.searchResultList, searchListenerObject(BrowsePage.ANIME))
        }

        searchResultRecyclerView.adapter = adapter
    }

    private fun searchListenerObject(targetPage: BrowsePage) = object : SearchListener {
        override fun passSelectedItem(id: Int) {
            val intent = Intent(activity, BrowseActivity::class.java)
            intent.putExtra(BrowseActivity.TARGET_PAGE, targetPage.name)
            intent.putExtra(BrowseActivity.LOAD_ID, id)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}
