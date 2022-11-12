package com.zen.alchan.ui.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.Activity
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.databinding.LayoutInfiniteScrollingBinding
import com.zen.alchan.helper.extensions.applyBottomSidePaddingInsets
import com.zen.alchan.helper.extensions.applyTopPaddingInsets
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.social.SocialListener
import com.zen.alchan.ui.social.SocialRvAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel


class ActivityListFragment : BaseFragment<LayoutInfiniteScrollingBinding, ActivityListViewModel>() {

    override val viewModel: ActivityListViewModel by viewModel()

    private var adapter: SocialRvAdapter? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LayoutInfiniteScrollingBinding {
        return LayoutInfiniteScrollingBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        with(binding) {
            setUpToolbar(defaultToolbar.defaultToolbar, getString(R.string.activity_list))
            adapter = SocialRvAdapter(requireContext(), listOf(), null, AppSetting(), false, getSocialListener())
            infiniteScrollingRecyclerView.adapter = adapter

            infiniteScrollingSwipeRefresh.setOnRefreshListener {

            }
        }
    }

    override fun setUpInsets() {
        binding.defaultToolbar.defaultToolbar.applyTopPaddingInsets()
        binding.infiniteScrollingRecyclerView.applyBottomSidePaddingInsets()
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.success.subscribe {
                dialog.showToast(it)
            },
            viewModel.error.subscribe {
                dialog.showToast(it)
            },
            viewModel.loading.subscribe {
                binding.infiniteScrollingSwipeRefresh.isRefreshing = it
            }
        )

        arguments?.getInt(USER_ID)?.let {
            viewModel.loadData(ActivityListParam(it))
        }
    }

    private fun getSocialListener(): SocialListener {
        return object : SocialListener {
            override fun navigateToUser(user: User) {
                navigation.navigateToUser(user.id)
            }

            override fun navigateToMedia(media: Media) {
                navigation.navigateToMedia(media.getId())
            }

            override fun navigateToActivityDetail(activity: Activity) {
                navigation.navigateToActivityDetail(activity.id)
            }

            override fun navigateToActivityList() {

            }

            override fun toggleLike(activity: Activity) {
//                viewModel.toggleLike(activity)
            }

            override fun viewLikes(activity: Activity) {

            }

            override fun toggleSubscribe(activity: Activity) {
//                viewModel.toggleSubscription(activity)
            }

            override fun viewOnAniList(activity: Activity) {
                if (activity.siteUrl.isNotBlank())
                    navigation.openWebView(activity.siteUrl)
                else
                    dialog.showToast(R.string.this_activity_is_already_removed)
            }

            override fun copyActivityLink(activity: Activity) {
//                viewModel.copyActivityLink(activity)
            }

            override fun report(activity: Activity) {
                if (activity.siteUrl.isNotBlank()) {
                    navigation.openWebView(activity.siteUrl)
                    dialog.showToast(R.string.please_click_on_the_more_icon_beside_the_date_and_click_report)
                } else
                    dialog.showToast(R.string.this_activity_is_already_removed)
            }

            override fun edit(activity: Activity) {

            }

            override fun delete(activity: Activity) {

            }

            override fun reply(activity: Activity) {

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }

    companion object {
        private const val USER_ID = "userId"
        @JvmStatic
        fun newInstance(userId: Int?) =
            ActivityListFragment().apply {
                arguments = Bundle().apply {
                    putInt(USER_ID, userId ?: 0)
                }
            }
    }
}