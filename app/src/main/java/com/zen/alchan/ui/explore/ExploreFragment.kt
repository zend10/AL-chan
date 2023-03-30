package com.zen.alchan.ui.explore

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.*
import com.zen.alchan.databinding.FragmentExploreBinding
import com.zen.alchan.helper.enums.SearchCategory
import com.zen.alchan.helper.extensions.applyBottomSidePaddingInsets
import com.zen.alchan.helper.extensions.applyTopPaddingInsets
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.search.SearchRvAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class ExploreFragment : BaseFragment<FragmentExploreBinding, ExploreViewModel>() {

    override val viewModel: ExploreViewModel by viewModel()

    private var adapter: SearchRvAdapter? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentExploreBinding {
        return FragmentExploreBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        with(binding) {
            exploreBackButton.clicks {
                goBack()
            }

            exploreCategoryButton.clicks {
                exploreEditText.clearFocus()
                viewModel.loadSearchCategories()
            }

            exploreSettingButton.clicks {
                exploreEditText.clearFocus()
                viewModel.loadMediaFilterComponent()
            }

            adapter = SearchRvAdapter(requireContext(), listOf(), AppSetting(), true, getSearchListener())
            exploreRecyclerView.adapter = adapter

            exploreSwipeRefresh.setOnRefreshListener {
                viewModel.reloadData()
            }

            exploreRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy != 0) exploreEditText.clearFocus()
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.canScrollVertically(1)) {
                        viewModel.loadNextPage()
                    }
                }
            })

            exploreEditText.setOnFocusChangeListener { _, hasFocus ->
                toggleKeyboard(hasFocus)
            }
        }
    }

    override fun setUpInsets() {
        binding.exploreLayout.applyTopPaddingInsets()
        binding.exploreRecyclerView.applyBottomSidePaddingInsets()
    }

    override fun setUpObserver() {
        disposables.add(
            RxTextView.textChanges(binding.exploreEditText)
                .skipInitialValue()
                .debounce(800, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { viewModel.doSearch(it.toString()) }
        )

        disposables.addAll(
            viewModel.loading.subscribe {
                binding.exploreSwipeRefresh.isRefreshing = it
            },
            viewModel.error.subscribe {
                dialog.showToast(it)
            },
            viewModel.appSetting.subscribe {
                adapter = SearchRvAdapter(requireContext(), listOf(), it, true, getSearchListener())
                binding.exploreRecyclerView.adapter = adapter
            },
            viewModel.searchItems.subscribe {
                adapter?.updateData(it, true)
            },
            viewModel.emptyLayoutVisibility.subscribe {
                binding.emptyLayout.emptyLayout.show(it)
            },
            viewModel.searchCategoryList.subscribe {
                dialog.showListDialog(it) { data, _ ->
                    binding.exploreRecyclerView.scrollToPosition(0)
                    viewModel.updateSelectedSearchCategory(data)
                }
            },
            viewModel.searchPlaceholderText.subscribe {
                binding.exploreEditText.hint = getString(it)
            },
            viewModel.filterVisibility.subscribe {
                binding.exploreSettingButton.show(it)
            },
            viewModel.mediaFilterComponent.subscribe {
                navigation.navigateToFilter(it.mediaFilter, it.mediaType, it.scoreFormat, it.isUserList, it.isViewer) {
                    viewModel.updateMediaFilter(it)
                }
            },
            viewModel.scrollToTopTrigger.subscribe {
                binding.exploreRecyclerView.scrollToPosition(0)
            }
        )

        arguments?.getString(SEARCH_CATEGORY)?.let {
            viewModel.loadData(SearchCategory.valueOf(it))
        }
    }

    private fun getSearchListener(): SearchRvAdapter.SearchListener {
        return object : SearchRvAdapter.SearchListener {
            override fun navigateToMedia(media: Media) {
                binding.exploreEditText.clearFocus()
                navigation.navigateToMedia(media.getId())
            }

            override fun navigateToCharacter(character: Character) {
                binding.exploreEditText.clearFocus()
                navigation.navigateToCharacter(character.id)
            }

            override fun navigateToStaff(staff: Staff) {
                binding.exploreEditText.clearFocus()
                navigation.navigateToStaff(staff.id)
            }

            override fun navigateToStudio(studio: Studio) {
                binding.exploreEditText.clearFocus()
                navigation.navigateToStudio(studio.id)
            }

            override fun navigateToUser(user: User) {
                binding.exploreEditText.clearFocus()
                navigation.navigateToUser(user.id)
            }

            override fun showQuickDetail(media: Media) {
                dialog.showMediaQuickDetailDialog(media)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

    companion object {
        private const val SEARCH_CATEGORY = "searchCategory"

        @JvmStatic
        fun newInstance(searchCategory: SearchCategory) = ExploreFragment().apply {
            arguments = Bundle().apply {
                putString(SEARCH_CATEGORY, searchCategory.name)
            }
        }
    }
}