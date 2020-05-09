package com.zen.alchan.ui.browse.staff.anime


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView

import com.zen.alchan.R
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.pojo.StaffMedia
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.browse.media.MediaFragment
import com.zen.alchan.ui.browse.staff.StaffFragment
import kotlinx.android.synthetic.main.fragment_staff_anime.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_loading.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaType

/**
 * A simple [Fragment] subclass.
 */
class StaffAnimeFragment : BaseFragment() {

    private val viewModel by viewModel<StaffAnimeViewModel>()

    private lateinit var adapter: StaffAnimeRvAdapter
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_staff_anime, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.staffId = arguments?.getInt(StaffFragment.STAFF_ID)
        adapter = assignAdapter()
        staffAnimeRecyclerView.adapter = adapter

        initLayout()
        setupObserver()
    }

    private fun assignAdapter(): StaffAnimeRvAdapter {
        return StaffAnimeRvAdapter(activity!!, viewModel.staffMedia, object : StaffAnimeRvAdapter.StaffAnimeListener {
            override fun passSelectedMedia(mediaId: Int, mediaType: MediaType) {
                val fragment = MediaFragment()
                val bundle = Bundle()
                bundle.putInt(MediaFragment.MEDIA_ID, mediaId)
                bundle.putString(MediaFragment.MEDIA_TYPE, mediaType.name)
                fragment.arguments = bundle
                listener?.changeFragment(fragment)
            }
        })
    }

    private fun setupObserver() {
        viewModel.staffMediaData.observe(viewLifecycleOwner, Observer {
            loadingLayout.visibility = View.GONE
            when (it.responseStatus) {
                ResponseStatus.SUCCESS -> {
                    if (isLoading) {
                        viewModel.staffMedia.removeAt(viewModel.staffMedia.lastIndex)
                        adapter.notifyItemRemoved(viewModel.staffMedia.size)
                        isLoading = false
                    }

                    if (!viewModel.hasNextPage) {
                        return@Observer
                    }

                    viewModel.hasNextPage = it.data?.staff?.staffMedia?.pageInfo?.hasNextPage ?: false
                    viewModel.page += 1
                    viewModel.isInit = true

                    it.data?.staff?.staffMedia?.edges?.forEach { edge ->
                        val staffMedia = StaffMedia(
                            edge?.node?.id,
                            edge?.node?.title?.userPreferred,
                            edge?.node?.coverImage?.large,
                            edge?.node?.type,
                            edge?.staffRole
                        )
                        viewModel.staffMedia.add(staffMedia)
                    }

                    adapter.notifyDataSetChanged()
                    emptyLayout.visibility = if (viewModel.staffMedia.isNullOrEmpty()) View.VISIBLE else View.GONE
                }
                ResponseStatus.ERROR -> {
                    DialogUtility.showToast(activity, it.message)
                    if (isLoading) {
                        viewModel.staffMedia.removeAt(viewModel.staffMedia.lastIndex)
                        adapter.notifyItemRemoved(viewModel.staffMedia.size)
                        isLoading = false
                    }
                    emptyLayout.visibility = if (viewModel.staffMedia.isNullOrEmpty()) View.VISIBLE else View.GONE
                    if (!viewModel.isInit) {
                        retryButton.visibility = View.VISIBLE
                        retryButton.setOnClickListener { viewModel.getStaffMedia() }
                    } else {
                        retryButton.visibility = View.GONE
                    }
                }
            }
        })

        if (!viewModel.isInit) {
            viewModel.getStaffMedia()
            loadingLayout.visibility = View.VISIBLE
        }
    }

    private fun initLayout() {
        staffAnimeRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.canScrollVertically(1) && viewModel.isInit && !isLoading) {
                    loadMore()
                    isLoading = true
                }
            }
        })
    }

    private fun loadMore() {
        if (viewModel.hasNextPage) {
            viewModel.staffMedia.add(null)
            adapter.notifyItemInserted(viewModel.staffMedia.lastIndex)
            viewModel.getStaffMedia()
        }
    }
}
