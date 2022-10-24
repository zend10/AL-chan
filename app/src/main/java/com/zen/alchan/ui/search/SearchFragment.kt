package com.zen.alchan.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.databinding.FragmentSearchBinding
import com.zen.alchan.helper.extensions.applyTopPaddingInsets
import com.zen.alchan.helper.extensions.clicks
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

            }

            adapter = SearchRvAdapter(requireContext(), listOf(), AppSetting(), getSearchListener())
            searchRecyclerView.adapter = adapter

            searchSwipeRefresh.setOnRefreshListener {

            }
        }
    }

    override fun setUpInsets() {
        binding.searchLayout.applyTopPaddingInsets()
    }

    override fun setUpObserver() {
        disposables.add(
            RxTextView.textChanges(binding.searchEditText)
                .debounce(800, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { viewModel.doSearch(it.toString()) }
        )

        disposables.addAll(
            viewModel.appSetting.subscribe {
                adapter = SearchRvAdapter(requireContext(), listOf(), it, getSearchListener())
                binding.searchRecyclerView.adapter = adapter
            },
            viewModel.searchItems.subscribe {
                adapter?.updateData(it, true)
            }
        )

        viewModel.loadData(Unit)
    }

    private fun getSearchListener(): SearchRvAdapter.SearchListener {
        return object : SearchRvAdapter.SearchListener {
            override fun navigateToMedia(media: Media) {
                navigation.navigateToMedia(media.getId())
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = SearchFragment()
    }
}