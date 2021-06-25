package com.zen.alchan.ui.bio

import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.databinding.FragmentBioBinding
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.profile.SharedProfileViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class BioFragment : BaseFragment<FragmentBioBinding, BioViewModel>() {

    override val viewModel: BioViewModel by viewModel()
    private val sharedViewModel by sharedViewModel<SharedProfileViewModel>()

    private var bioAdapter: BioRvAdapter? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBioBinding {
        return FragmentBioBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        bioAdapter = BioRvAdapter(requireContext(), listOf())
        binding.bioRecyclerView.adapter = bioAdapter
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

    companion object {
        @JvmStatic
        fun newInstance() = BioFragment()
    }
}