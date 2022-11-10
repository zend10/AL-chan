package com.zen.alchan.ui.social

import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.Activity
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.databinding.FragmentSocialBinding
import com.zen.alchan.helper.extensions.applyBottomSidePaddingInsets
import com.zen.alchan.helper.extensions.applyTopPaddingInsets
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class SocialFragment : BaseFragment<FragmentSocialBinding, SocialViewModel>() {

    override val viewModel: SocialViewModel by viewModel()

    private var adapter: SocialRvAdapter? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSocialBinding {
        return FragmentSocialBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            setUpToolbar(defaultToolbar.defaultToolbar, getString(R.string.social_hub), R.drawable.ic_delete)
            adapter = SocialRvAdapter(requireContext(), listOf(), AppSetting(), getSocialListener())
            socialRecyclerView.adapter = adapter

            socialSwipeRefresh.setOnRefreshListener {

            }
        }
    }

    override fun setUpInsets() {
        binding.defaultToolbar.defaultToolbar.applyTopPaddingInsets()
        binding.socialRecyclerView.applyBottomSidePaddingInsets()
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.error.subscribe {
                dialog.showToast(it)
            },
            viewModel.success.subscribe {
                dialog.showToast(it)
            },
            viewModel.loading.subscribe {
                binding.socialSwipeRefresh.isRefreshing = it
            },
            viewModel.adapterComponent.subscribe {
                adapter = SocialRvAdapter(requireContext(), listOf(), it, getSocialListener())
                binding.socialRecyclerView.adapter = adapter
            },
            viewModel.socialItemList.subscribe {
                adapter?.updateData(it, true)
            }
        )

        viewModel.loadData(Unit)
    }

    private fun getSocialListener(): SocialListener {
        return object : SocialListener {
            override fun navigateToUser(user: User) {
                navigation.navigateToUser(user.id)
            }

            override fun navigateToMedia(media: Media) {
                navigation.navigateToMedia(media.getId())
            }

            override fun navigateToActivity(activity: Activity) {

            }

            override fun navigateToActivityList() {

            }

            override fun toggleLike(activity: Activity) {
                viewModel.toggleLike(activity)
            }

            override fun toggleSubscribe(activity: Activity) {
                viewModel.toggleSubscription(activity)
            }

            override fun viewOnAniList(activity: Activity) {
                if (activity.siteUrl.isNotBlank())
                    navigation.openWebView(activity.siteUrl)
                else
                    dialog.showToast(R.string.this_activity_is_already_removed)
            }

            override fun copyActivityLink(activity: Activity) {
                viewModel.copyActivityLink(activity)
            }

            override fun report(activity: Activity) {
                if (activity.siteUrl.isNotBlank()) {
                    navigation.openWebView(activity.siteUrl)
                    dialog.showToast(R.string.please_click_on_the_more_icon_beside_the_date_and_click_report)
                } else
                    dialog.showToast(R.string.this_activity_is_already_removed)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = SocialFragment()
    }
}