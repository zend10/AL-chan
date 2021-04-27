package com.zen.alchan.ui.bio

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.profile.ProfileViewModel
import io.reactivex.disposables.CompositeDisposable
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class BioFragment : BaseFragment(R.layout.fragment_bio) {

    private val viewModel by viewModel<BioViewModel>()
    private val sharedViewModel by sharedViewModel<ProfileViewModel>()

    private val sharedDisposables = CompositeDisposable()

    override fun setupLayout() {

    }

    override fun setupObserver() {

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