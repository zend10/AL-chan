package com.zen.alchan.ui.browse.studio


import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import com.zen.alchan.R
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.pojo.StudioMedia
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.browse.media.MediaFragment
import kotlinx.android.synthetic.main.fragment_studio.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_loading.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaType
import kotlin.math.abs

/**
 * A simple [Fragment] subclass.
 */
class StudioFragment : BaseFragment() {

    private val viewModel by viewModel<StudioViewModel>()

    private lateinit var scaleUpAnim: Animation
    private lateinit var scaleDownAnim: Animation

    private lateinit var adapter: StudioMediaRvAdapter
    private var isLoading = false

    private lateinit var itemOpenAniList: MenuItem
    private lateinit var itemCopyLink: MenuItem

    companion object {
        const val STUDIO_ID = "studioId"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_studio, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.studioId = arguments?.getInt(STUDIO_ID)
        scaleUpAnim = AnimationUtils.loadAnimation(activity, R.anim.scale_up)
        scaleDownAnim = AnimationUtils.loadAnimation(activity, R.anim.scale_down)

        studioToolbar.setNavigationOnClickListener { activity?.finish() }
        studioToolbar.navigationIcon = ContextCompat.getDrawable(activity!!, R.drawable.ic_delete)
        studioToolbar.inflateMenu(R.menu.menu_anilist_link)
        itemOpenAniList = studioToolbar.menu.findItem(R.id.itemOpenAnilist)
        itemCopyLink = studioToolbar.menu.findItem(R.id.itemCopyLink)

        adapter = assignAdapter()
        studioRecyclerView.adapter = adapter

        setupObserver()
        initLayout()
    }

    override fun onStart() {
        super.onStart()
        viewModel.checkStudioIsFavorite()
    }

    private fun assignAdapter(): StudioMediaRvAdapter {
        return StudioMediaRvAdapter(activity!!, viewModel.studioMediaList, object : StudioMediaRvAdapter.StudioMediaListener {
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
        viewModel.studioData.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                    if (it.data?.Studio() != null) {
                        viewModel.currentStudioData = it.data.Studio()
                        setupHeader()
                    }
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                }
            }
        })

        viewModel.studioIsFavoriteData.observe(viewLifecycleOwner, Observer {
            if (it.responseStatus == ResponseStatus.SUCCESS) {
                if (it.data?.Studio()?.isFavourite == true) {
                    studioFavoriteButton.text = getString(R.string.favorited)
                    studioFavoriteButton.setTextColor(AndroidUtility.getResValueFromRefAttr(context, R.attr.themePrimaryColor))
                    studioFavoriteButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(activity!!, android.R.color.transparent))
                    studioFavoriteButton.strokeColor = ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(context, R.attr.themePrimaryColor))
                    studioFavoriteButton.strokeWidth = 2
                } else {
                    studioFavoriteButton.text = getString(R.string.set_as_favorite)
                    studioFavoriteButton.setTextColor(AndroidUtility.getResValueFromRefAttr(context, R.attr.themeBackgroundColor))
                    studioFavoriteButton.backgroundTintList = ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(context, R.attr.themePrimaryColor))
                    studioFavoriteButton.strokeWidth = 0
                }

                studioFavoriteButton.isEnabled = true
            }
        })

        viewModel.toggleFavouriteResponse.observe(this, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    viewModel.checkStudioIsFavorite()
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(activity, R.string.change_saved)
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                }
            }
        })

        viewModel.studioMediaData.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.SUCCESS -> {
                    if (isLoading) {
                        viewModel.studioMediaList.removeAt(viewModel.studioMediaList.lastIndex)
                        adapter.notifyItemRemoved(viewModel.studioMediaList.size)
                        isLoading = false
                    }

                    if (!viewModel.hasNextPage) {
                        return@Observer
                    }

                    viewModel.hasNextPage = it.data?.Studio()?.media()?.pageInfo()?.hasNextPage() ?: false
                    viewModel.page += 1
                    viewModel.isInit = true

                    it.data?.Studio()?.media()?.edges()?.forEach { edge ->
                        val staffMedia = StudioMedia(
                            edge.node()?.id(),
                            edge.node()?.title()?.userPreferred(),
                            edge.node()?.type(),
                            edge.node()?.format(),
                            edge.node()?.coverImage()?.large()
                        )
                        viewModel.studioMediaList.add(staffMedia)
                    }

                    adapter.notifyDataSetChanged()
                    emptyLayout.visibility = if (viewModel.studioMediaList.isNullOrEmpty()) View.VISIBLE else View.GONE
                }
                ResponseStatus.ERROR -> {
                    DialogUtility.showToast(activity, it.message)
                    if (isLoading) {
                        viewModel.studioMediaList.removeAt(viewModel.studioMediaList.lastIndex)
                        adapter.notifyItemRemoved(viewModel.studioMediaList.size)
                        isLoading = false
                    }
                    emptyLayout.visibility = if (viewModel.studioMediaList.isNullOrEmpty()) View.VISIBLE else View.GONE
                    if (!viewModel.isInit) {
                        retryButton.visibility = View.VISIBLE
                        retryButton.setOnClickListener { viewModel.getStudioMedia() }
                    } else {
                        retryButton.visibility = View.GONE
                    }
                }
            }
        })

        viewModel.getStudio()
        if (!viewModel.isInit) {
            viewModel.getStudioMedia()
        }
    }

    private fun setupHeader() {
        studioNameText.text = viewModel.currentStudioData?.name()
        studioFavoriteCountText.text = viewModel.currentStudioData?.favourites()?.toString()

        itemOpenAniList.isVisible = true
        itemCopyLink.isVisible = true

        itemOpenAniList.setOnMenuItemClickListener {
            CustomTabsIntent.Builder()
                .build()
                .launchUrl(activity!!, Uri.parse(viewModel.currentStudioData?.siteUrl()))
            true
        }

        itemCopyLink.setOnMenuItemClickListener {
            AndroidUtility.copyToClipboard(activity, viewModel.currentStudioData?.siteUrl()!!)
            DialogUtility.showToast(activity, R.string.link_copied)
            true
        }
    }

    private fun initLayout() {
        studioRefreshLayout.setOnRefreshListener {
            studioRefreshLayout.isRefreshing = false
            viewModel.getStudio()
            viewModel.checkStudioIsFavorite()
            if (!viewModel.isInit) {
                viewModel.getStudioMedia()
            }
        }

        studioAppBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            // disable refresh when toolbar is not fully expanded
            studioRefreshLayout.isEnabled = verticalOffset == 0

            // 50 is magic number gotten from trial and error
            if (abs(verticalOffset) - appBarLayout.totalScrollRange >= -50) {
                if (studioBannerContentLayout.isVisible) {
                    studioBannerContentLayout.startAnimation(scaleDownAnim)
                    studioBannerContentLayout.visibility = View.INVISIBLE
                }
            } else {
                if (studioBannerContentLayout.isInvisible) {
                    studioBannerContentLayout.startAnimation(scaleUpAnim)
                    studioBannerContentLayout.visibility = View.VISIBLE
                }
            }
        })

        studioFavoriteButton.setOnClickListener {
            viewModel.updateFavorite()
        }

        studioRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.canScrollVertically(1) && viewModel.isInit && !isLoading) {
                    loadMore()
                    isLoading = true
                }
            }
        })

        studioMediaSortText.text = viewModel.mediaSortArray[viewModel.mediaSortIndex]

        studioMediaSortText.setOnClickListener {
            MaterialAlertDialogBuilder(activity)
                .setItems(viewModel.mediaSortArray) { _, which ->
                    viewModel.mediaSortIndex = which
                    studioMediaSortText.text = viewModel.mediaSortArray[viewModel.mediaSortIndex]

                    viewModel.studioMediaList.clear()
                    viewModel.page = 1
                    viewModel.hasNextPage = true
                    viewModel.getStudioMedia()
                }
                .show()
        }
    }

    private fun loadMore() {
        if (viewModel.hasNextPage) {
            viewModel.studioMediaList.add(null)
            adapter.notifyItemInserted(viewModel.studioMediaList.lastIndex)
            viewModel.getStudioMedia()
        }
    }
}
