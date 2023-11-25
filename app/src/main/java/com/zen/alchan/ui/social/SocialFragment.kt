package com.zen.alchan.ui.social

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.*
import com.zen.alchan.databinding.FragmentSocialBinding
import com.zen.alchan.helper.enums.ActivityListPage
import com.zen.alchan.helper.enums.TextEditorType
import com.zen.alchan.helper.extensions.applyBottomSidePaddingInsets
import com.zen.alchan.helper.extensions.applyTopPaddingInsets
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class SocialFragment : BaseFragment<FragmentSocialBinding, SocialViewModel>() {

    override val viewModel: SocialViewModel by viewModel()

    private var adapter: SocialRvAdapter? = null
    private var likeAdapter: LikeRvAdapter? = null

    private var currentViewer: User? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSocialBinding {
        return FragmentSocialBinding.inflate(inflater, container, false)
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkIfNeedReload()
    }

    override fun setUpLayout() {
        binding.apply {
            setUpToolbar(defaultToolbar.defaultToolbar, getString(R.string.social_hub), R.drawable.ic_delete)
            adapter = SocialRvAdapter(requireContext(), listOf(), screenWidth, currentViewer, AppSetting(), false, getSocialListener())
            socialRecyclerView.adapter = adapter
            likeAdapter = LikeRvAdapter(requireContext(), listOf(), AppSetting(), getLikeListener())

            socialSwipeRefresh.setOnRefreshListener {
                viewModel.reloadData()
            }

            socialRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0)
                        socialPostNewActivityButton.hide()
                    else
                        socialPostNewActivityButton.show()
                }
            })

            socialPostNewActivityButton.clicks {
                navigation.navigateToTextEditor(TextEditorType.TEXT_ACTIVITY)
            }
        }
    }

    override fun setUpInsets() {
        binding.defaultToolbar.defaultToolbar.applyTopPaddingInsets()
        binding.socialRecyclerView.applyBottomSidePaddingInsets()
        binding.socialPostNewActivityLayout.applyBottomSidePaddingInsets()
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
                currentViewer = it.viewer
                adapter = SocialRvAdapter(requireContext(), listOf(), screenWidth, it.viewer, it.appSetting, false, getSocialListener())
                binding.socialRecyclerView.adapter = adapter
                likeAdapter = LikeRvAdapter(requireContext(), listOf(), it.appSetting, getLikeListener())
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

            override fun navigateToActivityDetail(activity: Activity) {
                navigation.navigateToActivityDetail(activity.id) { activityResult, isDeleted ->
                    viewModel.handleActivityDetailResult(activityResult, isDeleted)
                }
            }

            override fun navigateToActivityList(activityListPage: ActivityListPage) {
                navigation.navigateToActivityList(activityListPage)
            }

            override fun toggleLike(activity: Activity, activityReply: ActivityReply?) {
                viewModel.toggleLike(activity)
            }

            override fun viewLikes(activity: Activity, activityReply: ActivityReply?) {
                likeAdapter?.let {
                    it.updateData(activity.likes)
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
                viewModel.setActivityToBeEdited(activity)
                navigation.navigateToTextEditor(
                    if (activity is MessageActivity) TextEditorType.MESSAGE else TextEditorType.TEXT_ACTIVITY,
                    activity.id,
                    null,
                    if (activity is MessageActivity) activity.recipientId else null,
                    null
                )
            }

            override fun delete(activity: Activity, activityReply: ActivityReply?) {
                viewModel.deleteActivity(activity)
            }

            override fun reply(activity: Activity, activityReply: ActivityReply?) {
                navigation.navigateToActivityDetail(activity.id) { activityResult, isDeleted ->
                    viewModel.handleActivityDetailResult(activityResult, isDeleted)
                }
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
        @JvmStatic
        fun newInstance() = SocialFragment()
    }
}