package com.zen.alchan.ui.media.mediasocial

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.zen.alchan.R
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.databinding.LayoutInfiniteScrollingBinding
import com.zen.alchan.helper.extensions.applyBottomSidePaddingInsets
import com.zen.alchan.helper.extensions.applyTopPaddingInsets
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.pojo.SocialAdapterComponent
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaSocialFragment : BaseFragment<LayoutInfiniteScrollingBinding, MediaSocialViewModel>() {

    override val viewModel: MediaSocialViewModel by viewModel()

    private lateinit var media: Media
    private var adapterComponent: SocialAdapterComponent = SocialAdapterComponent()
    private var adapter: MediaSocialRvAdapter? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LayoutInfiniteScrollingBinding {
        return LayoutInfiniteScrollingBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        with(binding) {
            setUpToolbar(defaultToolbar.defaultToolbar, getString(R.string.social))

            adapter = MediaSocialRvAdapter(requireContext(), listOf(), adapterComponent.viewer, adapterComponent.appSetting, getMediaSocialListener())
            infiniteScrollingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            infiniteScrollingRecyclerView.adapter = adapter

            infiniteScrollingSwipeRefresh.setOnRefreshListener {
                viewModel.reloadData()
            }
        }
    }

    override fun setUpInsets() {
        binding.defaultToolbar.defaultToolbar.applyTopPaddingInsets()
        binding.infiniteScrollingRecyclerView.applyBottomSidePaddingInsets()
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.loading.subscribe {
                binding.infiniteScrollingSwipeRefresh.isRefreshing = it
            },
            viewModel.error.subscribe {
                dialog.showToast(it)
            },
            viewModel.adapterComponent.subscribe {
                adapterComponent = it
                binding.defaultToolbar.defaultToolbar.subtitle = media.getTitle(it.appSetting)
                adapter = MediaSocialRvAdapter(requireContext(), listOf(), it.viewer, it.appSetting, getMediaSocialListener())
                binding.infiniteScrollingRecyclerView.adapter = adapter
            },
            viewModel.socialItemList.subscribe {
                adapter?.updateData(it, true)
            },
            viewModel.emptyLayoutVisibility.subscribe {
                binding.emptyLayout.emptyLayout.show(it)
            }
        )

        viewModel.loadData(MediaSocialParam(media))
    }

    private fun getMediaSocialListener(): MediaSocialRvAdapter.MediaSocialListener {
        return object : MediaSocialRvAdapter.MediaSocialListener {
            override fun navigateToUser(user: User) {
                navigation.navigateToUser(user.id)
            }

            override fun seeMoreFollowingMediaList() {
                viewModel.loadFollowingMediaListNextPage()
            }

            override fun seeMoreMediaActivity() {
                viewModel.loadMediaActivityNextPage()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

    companion object {
        @JvmStatic
        fun newInstance(media: Media) = MediaSocialFragment().apply {
            this.media = media
        }
    }
}