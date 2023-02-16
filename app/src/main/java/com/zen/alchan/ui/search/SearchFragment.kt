package com.zen.alchan.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.*
import com.zen.alchan.databinding.FragmentSearchBinding
import com.zen.alchan.helper.extensions.applyBottomSidePaddingInsets
import com.zen.alchan.helper.extensions.applyTopPaddingInsets
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.ui.base.BaseFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class SearchFragment : BaseFragment<FragmentSearchBinding, SearchViewModel>() {

    override val viewModel: SearchViewModel by viewModel()

    private var adapter: SearchRvAdapter? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            searchBackButton.clicks {
                goBack()
            }

            searchSettingButton.clicks {
                searchEditText.clearFocus()
                viewModel.loadSearchCategories()
            }

            adapter = SearchRvAdapter(requireContext(), listOf(), AppSetting(), false, getSearchListener())
            searchRecyclerView.adapter = adapter

            searchSwipeRefresh.setOnRefreshListener {
                viewModel.reloadData()
            }

            searchRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy != 0) searchEditText.clearFocus()
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.canScrollVertically(1)) {
                        viewModel.loadNextPage()
                    }
                }
            })

            searchEditText.setOnFocusChangeListener { _, hasFocus ->
                toggleKeyboard(hasFocus)
            }

            searchEditText.requestFocus()
        }
    }

    override fun setUpInsets() {
        binding.searchLayout.applyTopPaddingInsets()
        binding.searchRecyclerView.applyBottomSidePaddingInsets()
    }

    override fun setUpObserver() {
        disposables.add(
            RxTextView.textChanges(binding.searchEditText)
                .skipInitialValue()
                .debounce(800, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { viewModel.doSearch(it.toString()) }
        )

        disposables.addAll(
            viewModel.loading.subscribe {
                binding.searchSwipeRefresh.isRefreshing = it
            },
            viewModel.error.subscribe {
                dialog.showToast(it)
            },
            viewModel.appSetting.subscribe {
                adapter = SearchRvAdapter(requireContext(), listOf(), it, false, getSearchListener())
                binding.searchRecyclerView.adapter = adapter
            },
            viewModel.searchItems.subscribe {
                adapter?.updateData(it, true)
            },
            viewModel.emptyLayoutVisibility.subscribe {
                binding.emptyLayout.emptyLayout.show(it)
            },
            viewModel.searchCategoryList.subscribe {
                dialog.showListDialog(it) { data, _ ->
                    binding.searchRecyclerView.scrollToPosition(0)
                    viewModel.updateSelectedSearchCategory(data)
                }
            },
            viewModel.searchPlaceholderText.subscribe {
                binding.searchEditText.hint = getString(it)
            }
        )

        viewModel.loadData(Unit)
    }

    private fun getSearchListener(): SearchRvAdapter.SearchListener {
        return object : SearchRvAdapter.SearchListener {
            override fun navigateToMedia(media: Media) {
                binding.searchEditText.clearFocus()
                navigation.navigateToMedia(media.getId())
            }

            override fun navigateToCharacter(character: Character) {
                binding.searchEditText.clearFocus()
                navigation.navigateToCharacter(character.id)
            }

            override fun navigateToStaff(staff: Staff) {
                binding.searchEditText.clearFocus()
                navigation.navigateToStaff(staff.id)
            }

            override fun navigateToStudio(studio: Studio) {
                binding.searchEditText.clearFocus()
                navigation.navigateToStudio(studio.id)
            }

            override fun navigateToUser(user: User) {
                binding.searchEditText.clearFocus()
                navigation.navigateToUser(user.id)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = SearchFragment()
    }
}