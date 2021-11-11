package com.zen.alchan.ui.browse

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.databinding.FragmentBrowseBinding
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.base.BrowseNavigationManager
import com.zen.alchan.ui.base.DefaultBrowseNavigationManager
import com.zen.alchan.ui.base.NavigationManager
import org.koin.androidx.viewmodel.ext.android.viewModel

class BrowseFragment : BaseFragment<FragmentBrowseBinding, BrowseViewModel>() {

    override val viewModel: BrowseViewModel by viewModel()

    lateinit var browseNavigationManager: BrowseNavigationManager
        private set

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBrowseBinding {
        return FragmentBrowseBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        browseNavigationManager = DefaultBrowseNavigationManager(requireContext(), childFragmentManager, binding.browseLayout)

        arguments?.getString(BROWSE_PAGE)?.let { page ->
            browseNavigationManager.pushBrowseScreenPage(BrowseNavigationManager.Page.valueOf(page), arguments?.getInt(ID))
        }

        arguments?.remove(ID)
        arguments?.remove(BROWSE_PAGE)
    }

    override fun setUpObserver() {


    }

    companion object {
        const val BROWSE_PAGE = "browsePage"
        const val ID = "id"

        @JvmStatic
        fun newInstance(page: BrowseNavigationManager.Page, id: Int? = null) =
            BrowseFragment().apply {
                arguments = Bundle().apply {
                    putString(BROWSE_PAGE, page.name)
                    if (id != null) putInt(ID, id)
                }
            }
    }
}