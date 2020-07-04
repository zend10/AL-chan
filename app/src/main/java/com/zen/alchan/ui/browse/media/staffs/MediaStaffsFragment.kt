package com.zen.alchan.ui.browse.media.staffs


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView

import com.zen.alchan.R
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.pojo.MediaStaffs
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.browse.media.MediaFragment
import com.zen.alchan.ui.browse.staff.StaffFragment
import kotlinx.android.synthetic.main.fragment_media_staffs.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_loading.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaType

/**
 * A simple [Fragment] subclass.
 */
class MediaStaffsFragment : BaseFragment() {

    private val viewModel by viewModel<MediaStaffsViewModel>()

    private lateinit var adapter: MediaStaffsRvAdapter
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_media_staffs, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.mediaId = arguments?.getInt(MediaFragment.MEDIA_ID)
        viewModel.mediaType = MediaType.valueOf(arguments?.getString(MediaFragment.MEDIA_TYPE)!!)
        adapter = assignAdapter()
        staffsRecyclerView.adapter = adapter

        initLayout()
        setupObserver()
    }

    private fun assignAdapter(): MediaStaffsRvAdapter {
        return MediaStaffsRvAdapter(activity!!, viewModel.mediaStaffs, object : MediaStaffsRvAdapter.MediaStaffsListener {
            override fun passSelectedStaff(staffId: Int) {
                listener?.changeFragment(BrowsePage.STAFF, staffId)
            }
        })
    }

    private fun setupObserver() {
        viewModel.mediaStaffsData.observe(viewLifecycleOwner, Observer {
            loadingLayout.visibility = View.GONE
            when (it.responseStatus) {
                ResponseStatus.SUCCESS -> {
                    if (isLoading) {
                        viewModel.mediaStaffs.removeAt(viewModel.mediaStaffs.lastIndex)
                        adapter.notifyItemRemoved(viewModel.mediaStaffs.size)
                        isLoading = false
                    }

                    if (!viewModel.hasNextPage) {
                        return@Observer
                    }

                    viewModel.hasNextPage = it.data?.media?.staff?.pageInfo?.hasNextPage ?: false
                    viewModel.page += 1
                    viewModel.isInit = true

                    it.data?.media?.staff?.edges?.forEach { edge ->
                        val mediaStaff = MediaStaffs(
                            edge?.node?.id,
                            edge?.node?.name?.full,
                            edge?.node?.image?.large,
                            edge?.role
                        )
                        viewModel.mediaStaffs.add(mediaStaff)
                    }

                    adapter.notifyDataSetChanged()
                    emptyLayout.visibility = if (viewModel.mediaStaffs.isNullOrEmpty()) View.VISIBLE else View.GONE
                }
                ResponseStatus.ERROR -> {
                    DialogUtility.showToast(activity, it.message)
                    if (isLoading) {
                        viewModel.mediaStaffs.removeAt(viewModel.mediaStaffs.lastIndex)
                        adapter.notifyItemRemoved(viewModel.mediaStaffs.size)
                        isLoading = false
                    }
                    emptyLayout.visibility = if (viewModel.mediaStaffs.isNullOrEmpty()) View.VISIBLE else View.GONE
                }
            }
        })

        if (!viewModel.isInit) {
            viewModel.getMediaStaffs()
            loadingLayout.visibility = View.VISIBLE
        }
    }

    private fun initLayout() {
        staffsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
            viewModel.mediaStaffs.add(null)
            adapter.notifyItemInserted(viewModel.mediaStaffs.lastIndex)
            viewModel.getMediaStaffs()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        staffsRecyclerView.adapter = null
    }
}
