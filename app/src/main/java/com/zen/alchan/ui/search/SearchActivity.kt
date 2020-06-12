package com.zen.alchan.ui.search

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.button.MaterialButton
import com.jakewharton.rxbinding2.widget.RxTextView
import com.zen.alchan.R
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.ui.base.BaseActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_search.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class SearchActivity : BaseActivity() {

    private val viewModel by viewModel<SearchViewModel>()

    private lateinit var viewPagerAdapter: SearchViewPagerAdapter
    private lateinit var searchCategoryButtonList: List<MaterialButton>

    val source = PublishSubject.create<String>() // to send search query to SearchListFragment
    private lateinit var disposable: Disposable

    companion object {
        const val SEARCH_USER = "searchUser"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchCategoryButtonList = listOf(
            searchAnimeButton, searchMangaButton, searchCharactersButton, searchStaffsButton, searchStudiosButton, searchUsersButton
        )

        if (intent.getBooleanExtra(SEARCH_USER, false)) {
            viewModel.selectedPage = BrowsePage.USER
            searchUsersButton.parent.requestChildFocus(searchUsersButton, searchUsersButton)
        }

        initLayout()
    }

    private fun initLayout() {
        searchBackButton.setOnClickListener { finish() }

        searchBarEditText.requestFocus()

        disposable = RxTextView.textChanges(searchBarEditText)
            .debounce(800, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { string -> source.onNext(string.toString()) }

        searchAnimeButton.setOnClickListener { handleSearchCategoryButton(BrowsePage.ANIME) }
        searchMangaButton.setOnClickListener { handleSearchCategoryButton(BrowsePage.MANGA) }
        searchCharactersButton.setOnClickListener { handleSearchCategoryButton(BrowsePage.CHARACTER) }
        searchStaffsButton.setOnClickListener { handleSearchCategoryButton(BrowsePage.STAFF) }
        searchStudiosButton.setOnClickListener { handleSearchCategoryButton(BrowsePage.STUDIO) }
        searchUsersButton.setOnClickListener { handleSearchCategoryButton(BrowsePage.USER) }

        viewPagerAdapter = SearchViewPagerAdapter(supportFragmentManager, viewModel.searchPageList)
        searchViewPager.adapter = viewPagerAdapter
        searchViewPager.offscreenPageLimit = viewPagerAdapter.count

        searchViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                viewModel.selectedPage = viewModel.searchPageList[position]
                handleSearchCategoryButton(viewModel.selectedPage)
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) { }

            override fun onPageScrollStateChanged(state: Int) { }
        })

        handleSearchCategoryButton(viewModel.selectedPage)
    }

    private fun handleSearchCategoryButton(selectedPage: BrowsePage) {
        viewModel.selectedPage = selectedPage

        val selectedIndex = viewModel.searchPageList.indexOf(viewModel.selectedPage)

        searchCategoryButtonList.forEachIndexed { index, materialButton ->
            if (index == selectedIndex) {
                materialButton.setTextColor(AndroidUtility.getResValueFromRefAttr(this, R.attr.themeBackgroundColor))
                materialButton.backgroundTintList = ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(this, R.attr.themeSecondaryColor))
            } else {
                materialButton.setTextColor(AndroidUtility.getResValueFromRefAttr(this, R.attr.themeSecondaryColor))
                materialButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.transparent))
            }
        }

        searchViewPager.currentItem = selectedIndex
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}
