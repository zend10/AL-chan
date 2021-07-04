package com.zen.alchan.ui.reorder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.databinding.FragmentReorderBinding
import com.zen.alchan.helper.extensions.applyBottomPaddingInsets
import com.zen.alchan.helper.extensions.applyTopPaddingInsets
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReorderFragment : BaseFragment<FragmentReorderBinding, ReorderViewModel>() {

    override val viewModel: ReorderViewModel by viewModel()
    private val sharedViewModel by sharedViewModel<SharedReorderViewModel>()

    private var reorderAdapter: ReorderRvAdapter? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentReorderBinding {
        return FragmentReorderBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            defaultToolbar.defaultToolbar.apply {
                setNavigationIcon(R.drawable.ic_left)
                setNavigationOnClickListener { goBack() }
            }

            reorderAdapter = ReorderRvAdapter(requireContext(), listOf())
            reorderRecyclerView.adapter = reorderAdapter
        }
    }

    override fun setUpInsets() {
        binding.defaultToolbar.defaultToolbar.applyTopPaddingInsets()
        binding.reorderSaveLayout.applyBottomPaddingInsets()
    }

    override fun setUpObserver() {
        sharedDisposables.add(
            sharedViewModel.unorderedList.subscribe {
                reorderAdapter?.updateData(it)
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        reorderAdapter = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = ReorderFragment()
    }
}