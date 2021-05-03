package com.zen.alchan.ui.bio

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.profile.ProfileViewModel
import com.zen.alchan.ui.profile.SharedProfileViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_bio.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class BioFragment : BaseFragment(R.layout.fragment_bio) {

    private val viewModel by viewModel<BioViewModel>()
    private val sharedViewModel by sharedViewModel<SharedProfileViewModel>()

    private val sharedDisposables = CompositeDisposable()
    private lateinit var bioAdapter: BioRvAdapter

    override fun setupLayout() {
        bioAdapter = BioRvAdapter(requireContext(), listOf())
        bioRecyclerView.adapter = bioAdapter
    }

    override fun setupObserver() {
        sharedDisposables.add(
            sharedViewModel.bioItems.subscribe {
                bioAdapter.updateData(it)
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedDisposables.clear()
    }

    companion object {
        @JvmStatic
        fun newInstance() = BioFragment()
    }
}