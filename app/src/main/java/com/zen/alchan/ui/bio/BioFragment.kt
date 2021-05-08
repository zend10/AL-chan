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
    private var bioAdapter: BioRvAdapter? = null

    override fun setUpLayout() {
        bioAdapter = BioRvAdapter(requireContext(), listOf())
        bioRecyclerView.adapter = bioAdapter
    }

    override fun setUpObserver() {
        sharedDisposables.add(
            sharedViewModel.profileData.subscribe {
                viewModel.getBioItems(it)
            }
        )

        disposables.add(
            viewModel.bioItems.subscribe {
                bioAdapter?.updateData(it)
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bioAdapter = null
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