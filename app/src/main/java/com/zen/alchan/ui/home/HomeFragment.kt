package com.zen.alchan.ui.home


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.bumptech.glide.request.RequestOptions

import com.zen.alchan.R
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.search.SearchActivity
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_loading.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    private val viewModel by viewModel<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupObserver()
        initLayout()
    }

    private fun setupObserver() {
        viewModel.viewerData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                initLayout()
            }
        })

        // TODO: refresh if anilist settings or list setting are changed

        viewModel.viewerDataResponse.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> {
//                    profileRefreshLayout.isRefreshing = false
                    loadingLayout.visibility = View.VISIBLE
                }
                ResponseStatus.SUCCESS -> loadingLayout.visibility = View.GONE
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                }
            }
        })

        viewModel.initData()
    }

    private fun initLayout() {
        val user = viewModel.viewerData.value

        GlideApp.with(this).load(R.drawable.welcome_background).into(headerImage)
        searchBar.setOnClickListener { startActivity(Intent(activity, SearchActivity::class.java)) }

        greetingsText.text = "Hello, ${user?.name}."
        GlideApp.with(this).load(user?.avatar?.large).apply(RequestOptions.circleCropTransform()).into(userAvatar)

        if (viewModel.homeShowWatching) {
            currentlyWatchingLayout.visibility = View.VISIBLE
        } else {
            currentlyWatchingLayout.visibility = View.GONE
        }

        if (viewModel.homeShowReading) {
            currentlyReadingLayout.visibility = View.VISIBLE
        } else {
            currentlyReadingLayout.visibility = View.GONE
        }


    }
}
