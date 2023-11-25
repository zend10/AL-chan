package com.zen.alchan.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.*
import com.zen.alchan.databinding.LayoutInfiniteScrollingBinding
import com.zen.alchan.helper.enums.ActivityListPage
import com.zen.alchan.helper.enums.TextEditorType
import com.zen.alchan.helper.extensions.applyBottomSidePaddingInsets
import com.zen.alchan.helper.extensions.applyTopPaddingInsets
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.pojo.SocialAdapterComponent
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.social.LikeRvAdapter
import com.zen.alchan.ui.social.SocialListener
import com.zen.alchan.ui.social.SocialRvAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel


class ActivityListFragment : BaseFragment<LayoutInfiniteScrollingBinding, ActivityListViewModel>() {

    override val viewModel: ActivityListViewModel by viewModel()

    private var adapter: SocialRvAdapter? = null
    private var likeAdapter: LikeRvAdapter? = null

    private var menuItemSelectActivityType: MenuItem? = null

    private var currentActivityUserId = 0
    private var adapterComponent = SocialAdapterComponent()

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LayoutInfiniteScrollingBinding {
        return LayoutInfiniteScrollingBinding.inflate(inflater, container, false)
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkIfNeedReload()
    }

    override fun setUpLayout() {
        with(binding) {
            with(defaultToolbar.defaultToolbar) {
                setUpToolbar(this, getString(R.string.activity_list))
                inflateMenu(R.menu.menu_activity_list)
                menuItemSelectActivityType = defaultToolbar.defaultToolbar.menu.findItem(R.id.itemActivityType)
            }

            menuItemSelectActivityType?.setOnMenuItemClickListener {
                viewModel.loadActivityTypeList()
                true
            }

            infiniteScrollingRecyclerView.updatePadding(left = 0, right = 0)

            adapter = SocialRvAdapter(requireContext(), listOf(), screenWidth, adapterComponent.viewer, adapterComponent.appSetting, false, getSocialListener())
            infiniteScrollingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            infiniteScrollingRecyclerView.adapter = adapter
            likeAdapter = LikeRvAdapter(requireContext(), listOf(), AppSetting(), getLikeListener())

            infiniteScrollingSwipeRefresh.setOnRefreshListener {
                viewModel.reloadData()
            }

            infiniteScrollingRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.canScrollVertically(1)) {
                        viewModel.loadNextPage()
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0)
                        infiniteScrollingActionButton.hide()
                    else
                        infiniteScrollingActionButton.show()
                }
            })

            infiniteScrollingActionButton.clicks {
                navigation.navigateToTextEditor(
                    if (isViewerActivity()) TextEditorType.TEXT_ACTIVITY else TextEditorType.MESSAGE,
                    null,
                    null,
                    if (isViewerActivity()) null else currentActivityUserId,
                    null
                )
            }

            infiniteScrollingActionButtonLayout.show(true)
        }
    }

    override fun setUpInsets() {
        binding.defaultToolbar.defaultToolbar.applyTopPaddingInsets()
        binding.infiniteScrollingRecyclerView.applyBottomSidePaddingInsets()
        binding.infiniteScrollingActionButtonLayout.applyBottomSidePaddingInsets()
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.toolbarTitle.subscribe {
                binding.defaultToolbar.defaultToolbar.title = getString(it)
            },
            viewModel.loading.subscribe {
                binding.infiniteScrollingSwipeRefresh.isRefreshing = it
            },
            viewModel.success.subscribe {
                dialog.showToast(it)
            },
            viewModel.error.subscribe {
                dialog.showToast(it)
            },
            viewModel.adapterComponent.subscribe {
                adapterComponent = it
                adapter = SocialRvAdapter(requireContext(), listOf(), screenWidth, it.viewer, it.appSetting, false, getSocialListener())
                binding.infiniteScrollingRecyclerView.adapter = adapter
                likeAdapter = LikeRvAdapter(requireContext(), listOf(), it.appSetting, getLikeListener())
            },
            viewModel.socialItemList.subscribe {
                adapter?.updateData(it, true)
            },
            viewModel.emptyLayoutVisibility.subscribe {
                binding.emptyLayout.emptyLayout.show(it)
            },
            viewModel.activityTypeList.subscribe {
                dialog.showMultiSelectDialog(it.first, it.second) {
                    viewModel.updateSelectedActivityTypes(it)
                }
            }
        )

        arguments?.let {
            val activityListPage = ActivityListPage.valueOf(it.getString(ACTIVITY_LIST_PAGE) ?: ActivityListPage.SPECIFIC_USER.name)
            currentActivityUserId = it.getInt(USER_ID, 0)
            viewModel.loadData(ActivityListParam(activityListPage, currentActivityUserId))
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
                navigation.navigateToActivityDetail(activity.id) { activityResult, isDeleted ->
                    viewModel.handleActivityDetailResult(activityResult, isDeleted)
                }
            }

            override fun navigateToActivityList(activityListPage: ActivityListPage) {
                // no need to handle
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

    private fun isViewerActivity(): Boolean {
        return currentActivityUserId == 0 || currentActivityUserId == adapterComponent.viewer?.id
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        likeAdapter = null
        menuItemSelectActivityType = null
    }

    companion object {
        private const val USER_ID = "userId"
        private const val ACTIVITY_LIST_PAGE = "activityListPage"

        @JvmStatic
        fun newInstance(activityListPage: ActivityListPage, userId: Int?) =
            ActivityListFragment().apply {
                arguments = Bundle().apply {
                    putString(ACTIVITY_LIST_PAGE, activityListPage.name)
                    putInt(USER_ID, userId ?: 0)
                }
            }
    }
}