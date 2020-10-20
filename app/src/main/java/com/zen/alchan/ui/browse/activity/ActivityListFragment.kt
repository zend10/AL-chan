package com.zen.alchan.ui.browse.activity


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import com.zen.alchan.R
import com.zen.alchan.data.response.*
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.enums.EditorType
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.pojo.ListActivity
import com.zen.alchan.helper.pojo.MessageActivity
import com.zen.alchan.helper.pojo.TextActivity
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.browse.BrowseActivity
import com.zen.alchan.ui.common.TextEditorActivity
import io.noties.markwon.Markwon
import kotlinx.android.synthetic.main.fragment_activity_list.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_loading.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.ActivityType
import type.MediaType

/**
 * A simple [Fragment] subclass.
 */
class ActivityListFragment : BaseFragment() {

    private val viewModel by viewModel<ActivityListViewModel>()

    private lateinit var adapter: ActivityListRvAdapter
    private var isLoading = false

    private var maxWidth = 0
    private lateinit var markwon: Markwon

    private var itemFilter: MenuItem? = null

    companion object {
        const val USER_ID = "userId"
        const val USER_NAME = "userName"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_activity_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.userId = arguments?.getInt(USER_ID)
        viewModel.userName = arguments?.getString(USER_NAME)

        maxWidth = AndroidUtility.getScreenWidth(activity)
        markwon = AndroidUtility.initMarkwon(activity!!)

        adapter = assignAdapter()
        activityRecyclerView.adapter = adapter

        toolbarLayout.title = getString(R.string.activity)
        toolbarLayout.setNavigationOnClickListener { activity?.onBackPressed() }
        toolbarLayout.navigationIcon = ContextCompat.getDrawable(activity!!, R.drawable.ic_arrow_back)
        toolbarLayout.inflateMenu(R.menu.menu_filter)
        itemFilter = toolbarLayout.menu.findItem(R.id.itemFilter)

        setupObserver()
        initLayout()
    }

    private fun setupObserver() {
        viewModel.notifyActivityList.observe(this, Observer {
            if (true) {
                loadingLayout.visibility = View.VISIBLE
                isLoading = false
                viewModel.refresh()
            }
        })

        viewModel.activityListResponse.observe(viewLifecycleOwner, Observer {
            loadingLayout.visibility = View.GONE
            when (it.responseStatus) {
                ResponseStatus.SUCCESS -> {
                    if (isLoading) {
                        viewModel.activityList.removeAt(viewModel.activityList.lastIndex)
                        adapter.notifyItemRemoved(viewModel.activityList.size)
                        isLoading = false
                    }

                    if (!viewModel.hasNextPage) {
                        return@Observer
                    }

                    viewModel.hasNextPage = it.data?.page?.pageInfo?.hasNextPage ?: false
                    viewModel.page += 1
                    viewModel.isInit = true

                    it.data?.page?.activities?.forEach { act ->
                        val activityItem = when (act?.__typename) {
                            viewModel.textActivityText -> {
                                val item = act.fragments.onTextActivity
                                if (item?.user?.id == null) {
                                    null
                                } else {
                                    val user = User(id = item.user.id, name = item.user.name, avatar = UserAvatar(null, item.user.avatar?.medium))
                                    TextActivity(item.id, item.type, item.replyCount, item.siteUrl, item.isSubscribed, item.likeCount, item.isLiked, item.createdAt, null, null, item.userId, item.text, user)
                                }
                            }
                            viewModel.listActivityText -> {
                                val item = act.fragments.onListActivity
                                if (item?.media?.id == null || item.user?.id == null) {
                                    null
                                } else {
                                    val media = Media(id = item.media.id, title = MediaTitle(userPreferred = item.media.title?.userPreferred!!), coverImage = MediaCoverImage(null, item.media.coverImage?.medium), type = item.media.type, format = item.media.format, startDate = FuzzyDate(item.media.startDate?.year, item.media.startDate?.month, item.media.startDate?.day))
                                    val user = User(id = item.user.id, name = item.user.name, avatar = UserAvatar(null, item.user.avatar?.medium))
                                    ListActivity(item.id, item.type, item.replyCount, item.siteUrl, item.isSubscribed, item.likeCount, item.isLiked, item.createdAt, null, null, item.userId, item.status, item.progress, media, user)
                                }
                            }
                            viewModel.messageActivityText -> {
                                val item = act.fragments.onMessageActivity
                                if (item?.recipient?.id == null || item.messenger?.id == null) {
                                    null
                                } else {
                                    val recipient = User(id = item.recipient.id, name = item.recipient.name, avatar = UserAvatar(null, item.recipient.avatar?.medium))
                                    val messenger = User(id = item.messenger.id, name = item.messenger.name, avatar = UserAvatar(null, item.messenger.avatar?.medium))
                                    MessageActivity(item.id, item.type, item.replyCount, item.siteUrl, item.isSubscribed, item.likeCount, item.isLiked, item.createdAt, null, null, item.recipientId, item.messengerId, item.message, item.isPrivate, recipient, messenger)
                                }
                            }
                            else -> null
                        }

                        if (activityItem != null) {
                            viewModel.activityList.add(activityItem)
                        }
                    }

                    adapter.notifyDataSetChanged()
                    emptyLayout.visibility = if (viewModel.activityList.isNullOrEmpty()) View.VISIBLE else View.GONE
                }
                ResponseStatus.ERROR -> {
                    DialogUtility.showToast(activity, it.message)
                    if (isLoading) {
                        viewModel.activityList.removeAt(viewModel.activityList.lastIndex)
                        adapter.notifyItemRemoved(viewModel.activityList.size)
                        isLoading = false
                    }

                    emptyLayout.visibility = if (viewModel.activityList.isNullOrEmpty()) View.VISIBLE else View.GONE
                }
            }
        })

        viewModel.toggleLikeResponse.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                    val findActivity = viewModel.activityList.find { item -> item?.id == it.data?.id }
                    val activityIndex = viewModel.activityList.indexOf(findActivity)
                    if (activityIndex != -1) {
                        viewModel.activityList[activityIndex]?.isLiked = it.data?.isLiked
                        viewModel.activityList[activityIndex]?.likeCount = it.data?.likeCount ?: 0
                        adapter.notifyItemChanged(activityIndex)
                    }
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                }
            }
        })

        viewModel.toggleActivitySubscriptionResponse.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                    val findActivity = viewModel.activityList.find { item -> item?.id == it.data?.id }
                    val activityIndex = viewModel.activityList.indexOf(findActivity)
                    if (activityIndex != -1) {
                        viewModel.activityList[activityIndex]?.isSubscribed = it.data?.isSubscribed
                        adapter.notifyItemChanged(activityIndex)
                    }
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                }
            }
        })

        viewModel.deleteActivityResponse.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                    val findActivity = viewModel.activityList.find { item -> item?.id == it.data }
                    val activityIndex = viewModel.activityList.indexOf(findActivity)
                    if (activityIndex != -1) {
                        viewModel.activityList.removeAt(activityIndex)
                        adapter.notifyItemRemoved(activityIndex)
                    }
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                }
            }
        })

        if (!viewModel.isInit) {
            viewModel.getActivities()
            loadingLayout.visibility = View.VISIBLE
        }
    }

    private fun initLayout() {
        activityListRefreshLayout.setOnRefreshListener {
            activityListRefreshLayout.isRefreshing = false
            loadingLayout.visibility = View.VISIBLE
            isLoading = false
            viewModel.refresh()
        }

        itemFilter?.setOnMenuItemClickListener {
            val activityTypeStringArray = viewModel.activityTypeArray.map { getString(it) }.toTypedArray()
            MaterialAlertDialogBuilder(requireActivity())
                .setItems(activityTypeStringArray) { _, which ->
                    viewModel.selectedActivityType = viewModel.activityTypeList[which]
                    loadingLayout.visibility = View.VISIBLE
                    isLoading = false
                    viewModel.refresh()
                }
                .show()

            true
        }

        activityRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.canScrollVertically(1) && viewModel.isInit && !isLoading) {
                    loadMore()
                    isLoading = true
                }
            }
        })

        newActivityButton.setOnClickListener {
            val intent = Intent(activity, TextEditorActivity::class.java)
            if (viewModel.userId != null && viewModel.currentUserId != viewModel.userId) {
                intent.putExtra(TextEditorActivity.RECIPIENT_ID, viewModel.userId!!)
                intent.putExtra(TextEditorActivity.RECIPIENT_NAME, viewModel.userName)
            }
            startActivityForResult(intent, EditorType.ACTIVITY.ordinal)
        }
    }

    private fun loadMore() {
        if (viewModel.hasNextPage) {
            viewModel.activityList.add(null)
            adapter.notifyItemInserted(viewModel.activityList.lastIndex)
            viewModel.getActivities()
        }
    }

    private fun assignAdapter(): ActivityListRvAdapter {
        return ActivityListRvAdapter(
            activity!!,
            viewModel.activityList,
            viewModel.currentUserId,
            maxWidth,
            markwon,
            null,
            object : ActivityListener {
                override fun openActivityPage(activityId: Int) {
                    listener?.changeFragment(BrowsePage.ACTIVITY_DETAIL, activityId)
                }

                override fun openUserPage(userId: Int) {
                    listener?.changeFragment(BrowsePage.USER, userId)
                }

                override fun toggleLike(activityId: Int) {
                    viewModel.toggleLike(activityId)
                }

                override fun toggleSubscribe(activityId: Int, subscribe: Boolean) {
                    viewModel.toggleSubscription(activityId, subscribe)
                }

                override fun editActivity(activityId: Int, text: String, recipientId: Int?, recipientName: String?) {
                    val intent = Intent(activity, TextEditorActivity::class.java)
                    intent.putExtra(TextEditorActivity.ACTIVITY_ID, activityId)
                    intent.putExtra(TextEditorActivity.TEXT_CONTENT, text)
                    intent.putExtra(TextEditorActivity.RECIPIENT_ID, recipientId)
                    intent.putExtra(TextEditorActivity.RECIPIENT_NAME, recipientName)
                    startActivityForResult(intent, EditorType.ACTIVITY.ordinal)
                }

                override fun deleteActivity(activityId: Int) {
                    DialogUtility.showOptionDialog(
                        requireActivity(),
                        R.string.delete_activity,
                        R.string.are_you_sure_you_want_to_delete_this_activity,
                        R.string.delete,
                        {
                            viewModel.deleteActivity(activityId)
                        },
                        R.string.cancel,
                        { }
                    )
                }

                override fun viewOnAniList(siteUrl: String?) {
                    if (siteUrl.isNullOrBlank()) {
                        DialogUtility.showToast(activity, R.string.some_data_has_not_been_retrieved)
                        return
                    }

                    CustomTabsIntent.Builder().build().launchUrl(activity!!, Uri.parse(siteUrl))
                }

                override fun copyLink(siteUrl: String?) {
                    if (siteUrl.isNullOrBlank()) {
                        DialogUtility.showToast(activity, R.string.some_data_has_not_been_retrieved)
                        return
                    }

                    AndroidUtility.copyToClipboard(activity, siteUrl)
                    DialogUtility.showToast(activity, R.string.link_copied)
                }

                override fun openMediaPage(mediaId: Int, mediaType: MediaType?) {
                    listener?.changeFragment(BrowsePage.valueOf(mediaType?.name!!), mediaId)
                }

                override fun changeActivityType(selectedActivityType: ArrayList<ActivityType>?) {
                    // do nothing
                }

                override fun changeBestFriend(selectedBestFriendPosition: Int) {
                    // do nothing
                }
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EditorType.ACTIVITY.ordinal && resultCode == Activity.RESULT_OK) {
            loadingLayout.visibility = View.VISIBLE
            isLoading = false
            viewModel.refresh()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activityRecyclerView.adapter = null
        itemFilter = null
    }
}
