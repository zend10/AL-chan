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
import com.zen.alchan.databinding.FragmentActivityDetailBinding
import com.zen.alchan.helper.extensions.applyBottomSidePaddingInsets
import com.zen.alchan.helper.extensions.applyTopPaddingInsets
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.social.SocialListener
import com.zen.alchan.ui.social.SocialRvAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel


class ActivityDetailFragment : BaseFragment<FragmentActivityDetailBinding, ActivityDetailViewModel>() {

    override val viewModel: ActivityDetailViewModel by viewModel()

    private var adapter: SocialRvAdapter? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentActivityDetailBinding {
        return FragmentActivityDetailBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        with(binding) {
            setUpToolbar(defaultToolbar.defaultToolbar, getString(R.string.activity_detail))
            adapter = SocialRvAdapter(requireContext(), listOf(), null, AppSetting(), true, getSocialListener())
            activityRecyclerView.adapter = adapter

            activitySwipeRefresh.setOnRefreshListener {
                viewModel.reloadData()
            }
        }
    }

    override fun setUpInsets() {
        binding.defaultToolbar.defaultToolbar.applyTopPaddingInsets()
        binding.activityRecyclerView.applyBottomSidePaddingInsets()
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
                binding.activitySwipeRefresh.isRefreshing = it
            },
            viewModel.adapterComponent.subscribe {
                adapter = SocialRvAdapter(requireContext(), listOf(), it.viewer, it.appSetting, true, getSocialListener())
                binding.activityRecyclerView.adapter = adapter
            },
            viewModel.socialItemList.subscribe {
                adapter?.updateData(it, true)
            }
        )


        arguments?.getInt(ACTIVITY_ID)?.let {
            viewModel.loadData(ActivityDetailParam(it))
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
        adapter = null
    }

    companion object {
        private const val ACTIVITY_ID = "activityId"

        @JvmStatic
        fun newInstance(activityId: Int) =
            ActivityDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(ACTIVITY_ID, activityId)
                }
            }
    }
}