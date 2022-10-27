package com.zen.alchan.ui.explore

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.databinding.FragmentExploreBinding
import com.zen.alchan.helper.enums.SearchCategory
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class ExploreFragment : BaseFragment<FragmentExploreBinding, ExploreViewModel>() {

    override val viewModel: ExploreViewModel by viewModel()

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentExploreBinding {
        return FragmentExploreBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {

        }
    }

    override fun setUpObserver() {


        arguments?.getString(SEARCH_CATEGORY)?.let {
            viewModel.loadData(SearchCategory.valueOf(it))
        }
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