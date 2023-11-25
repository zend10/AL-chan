package com.zen.alchan.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.*
import com.zen.databinding.FragmentActivityDetailBinding
import com.zen.alchan.helper.enums.ActivityListPage
import com.zen.alchan.helper.enums.TextEditorType
import com.zen.alchan.helper.extensions.applyBottomSidePaddingInsets
import com.zen.alchan.helper.extensions.applyTopPaddingInsets
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.pojo.SocialAdapterComponent
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.social.LikeRvAdapter
import com.zen.alchan.ui.social.SocialListener
import com.zen.alchan.ui.social.SocialRvAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel


class ActivityDetailFragment : BaseFragment<FragmentActivityDetailBinding, ActivityDetailViewModel>() {

    override val viewModel: ActivityDetailViewModel by viewModel()

    private var adapter: SocialRvAdapter? = null
    private var likeAdapter: LikeRvAdapter? = null
    private var listener: ActivityDetailListener? = null

    private var currentActivityId = 0
    private var adapterComponent = SocialAdapterComponent()

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentActivityDetailBinding {
        return FragmentActivityDetailBinding.inflate(inflater, container, false)
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkIfNeedReload()
    }

    override fun setUpLayout() {
        with(binding) {
            setUpToolbar(defaultToolbar.defaultToolbar, getString(R.string.activity_detail))
            adapter = SocialRvAdapter(requireContext(), listOf(), screenWidth, adapterComponent.viewer, adapterComponent.appSetting, true, getSocialListener())
            activityRecyclerView.adapter = adapter
            likeAdapter = LikeRvAdapter(requireContext(), listOf(), adapterComponent.appSetting, getLikeListener())

            activitySwipeRefresh.setOnRefreshListener {
                viewModel.reloadData()
            }

            activityRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0)
                        activityReplyButton.hide()
                    else
                        activityReplyButton.show()
                }
            })

            activityReplyButton.clicks {
                navigation.navigateToTextEditor(TextEditorType.ACTIVITY_REPLY, currentActivityId)
            }
        }
    }

    override fun setUpInsets() {
        binding.defaultToolbar.defaultToolbar.applyTopPaddingInsets()
        binding.activityRecyclerView.applyBottomSidePaddingInsets()
        binding.activityReplyLayout.applyBottomSidePaddingInsets()
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
                adapterComponent = it
                adapter = SocialRvAdapter(requireContext(), listOf(), screenWidth, it.viewer, it.appSetting, true, getSocialListener())
                binding.activityRecyclerView.adapter = adapter
                likeAdapter = LikeRvAdapter(requireContext(), listOf(), it.appSetting, getLikeListener())
            },
            viewModel.socialItemList.subscribe {
                adapter?.updateData(it, true)
            },
            viewModel.activityDetailResult.subscribe {
                listener?.getActivityDetailResult(it.first, it.second)
                if (it.second) goBack()
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
                // no need to handle
            }

            override fun navigateToActivityList(activityListPage: ActivityListPage) {
                // no need to handle
            }

            override fun toggleLike(activity: Activity, activityReply: ActivityReply?) {
                activityReply?.let { viewModel.toggleLike(it) } ?: viewModel.toggleLike(activity)
            }

            override fun viewLikes(activity: Activity, activityReply: ActivityReply?) {
                likeAdapter?.let {
                    activityReply?.let { likeAdapter?.updateData(it.likes) } ?: it.updateData(activity.likes)
                    dialog.showListDialog(it)
                }
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

            override fun edit(activity: Activity, activityReply: ActivityReply?) {
                activityReply?.let {
                    viewModel.setReplyToBeEdited(activityReply)
                    navigation.navigateToTextEditor(TextEditorType.ACTIVITY_REPLY, activity.id, activityReply.id, null, null)
                } ?: run {
                    viewModel.setActivityToBeEdited(activity)
                    navigation.navigateToTextEditor(
                        if (activity is MessageActivity) TextEditorType.MESSAGE else TextEditorType.TEXT_ACTIVITY,
                        activity.id,
                        null,
                        if (activity is MessageActivity) activity.recipientId else null,
                        null
                    )
                }
            }

            override fun delete(activity: Activity, activityReply: ActivityReply?) {
                activityReply?.let { viewModel.deleteActivityReply(activityReply) } ?: viewModel.deleteActivity(activity)
            }

            override fun reply(activity: Activity, activityReply: ActivityReply?) {
                activityReply?.let {
                    navigation.navigateToTextEditor(TextEditorType.ACTIVITY_REPLY, activity.id, null, null, activityReply.user.name)
                } ?: navigation.navigateToTextEditor(TextEditorType.ACTIVITY_REPLY, activity.id, null, null, activity.user().name)
            }
        }
    }

    private fun getLikeListener(): LikeRvAdapter.LikeListener {
        return object : LikeRvAdapter.LikeListener {
            override fun navigateToUser(user: User) {
                dialog.dismissListDialog()
                navigation.navigateToUser(user.id)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        likeAdapter = null
    }

    companion object {
        private const val ACTIVITY_ID = "activityId"

        @JvmStatic
        fun newInstance(activityId: Int, listener: ActivityDetailListener) =
            ActivityDetailFragment().apply {
                currentActivityId = activityId
                arguments = Bundle().apply {
                    putInt(ACTIVITY_ID, activityId)
                }
                this.listener = listener
            }
    }

    interface ActivityDetailListener {
        fun getActivityDetailResult(activity: Activity, isDeleted: Boolean)
    }
}