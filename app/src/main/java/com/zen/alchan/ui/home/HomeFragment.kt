package com.zen.alchan.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.helper.extensions.applyInsets
import com.zen.alchan.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : BaseFragment(R.layout.fragment_home) {

    private val viewModel by viewModel<HomeViewModel>()

    override fun setupObserver() {
        disposables.add(
            viewModel.loading.subscribe {

            }
        )

        disposables.add(
            viewModel.trendingAnime.subscribe {
                if (it.isNotEmpty()) {

                }
            }
        )

        viewModel.getHomeData()
    }

    override fun setupLayout() {
        homeAppBarLayout.applyInsets()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}