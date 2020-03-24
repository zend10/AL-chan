package com.zen.alchan.ui.animelist


import android.content.res.ColorStateList
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson

import com.zen.alchan.R
import com.zen.alchan.data.response.MediaListCollection
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.pojo.MediaFilteredData
import com.zen.alchan.helper.pojo.MediaListTabItem
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.general.MediaFilterBottomSheet
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_anime_list.*
import kotlinx.android.synthetic.main.layout_loading.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaType

/**
 * A simple [Fragment] subclass.
 */
class AnimeListFragment : Fragment() {

    private val viewModel by viewModel<AnimeListViewModel>()

    private lateinit var viewPagerAdapter: AnimeListViewPagerAdapter
    private lateinit var itemSearch: MenuItem
    private lateinit var itemCustomiseList: MenuItem
    private lateinit var itemFilter: MenuItem

    private lateinit var searchView: SearchView
    val source = PublishSubject.create<String>() // to send search query to AnimeListItemFragment

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
            itemCustomiseList = menu.findItem(R.id.itemCustomiseList)
            itemFilter = menu.findItem(R.id.itemFilter)
        }

        searchView = itemSearch.actionView as SearchView
        searchView.queryHint = getString(R.string.search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                source.onNext(query ?: "")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                source.onNext(newText ?: "")
                return true
            }
        })

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

        setupObserver()
        initLayout()
    }

    private fun setupObserver() {
        viewModel.animeListDataResponse.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> {
                    animeListRefreshLayout.isRefreshing = false
                    loadingLayout.visibility = View.VISIBLE
                }
                ResponseStatus.SUCCESS -> {
                    initLayout()
                    loadingLayout.visibility = View.GONE
                }
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
            loadingLayout.visibility = View.VISIBLE
            viewModel.retrieveAnimeListData()
        }

        val animeList = viewModel.animeListData.value?.lists

        if (!animeList.isNullOrEmpty()) {
            viewModel.tabItemList.clear()
            animeList.forEach { viewModel.tabItemList.add(MediaListTabItem(it.name!!, it.entries?.size!!)) }

            viewPagerAdapter = AnimeListViewPagerAdapter(childFragmentManager, viewModel.tabItemList)
            animeListViewPager.adapter = viewPagerAdapter
            animeListViewPager.offscreenPageLimit = viewPagerAdapter.count

            animeListTabLayout.setupWithViewPager(animeListViewPager)

            animeListViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageSelected(position: Int) {
                    viewModel.selectedTab = position
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    // do nothing
                }

                override fun onPageScrollStateChanged(state: Int) {
                    // do nothing
                }
            })

            animeListViewPager.currentItem = viewModel.selectedTab
        }

//        toolbarLayout.setBackgroundColor(AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeSecondaryColor))

//        val searchDrawable = itemSearch.icon
//        searchDrawable.mutate()
//        searchDrawable.setColorFilter(AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeNegativeColor), PorterDuff.Mode.SRC_ATOP)
    }
}
