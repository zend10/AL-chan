package com.zen.alchan.ui.social.global

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.data.response.*
import com.zen.alchan.helper.changeStatusBarColor
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.enums.EditorType
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.pojo.ListActivity
import com.zen.alchan.helper.pojo.MessageActivity
import com.zen.alchan.helper.pojo.TextActivity
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.base.BaseActivity
import com.zen.alchan.ui.browse.BrowseActivity
import com.zen.alchan.ui.browse.activity.ActivityListRvAdapter
import com.zen.alchan.ui.browse.activity.ActivityListener
import com.zen.alchan.ui.common.TextEditorActivity
import io.noties.markwon.Markwon
import kotlinx.android.synthetic.main.activity_global_feed.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_loading.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.ActivityType
import type.MediaType

class GlobalFeedActivity : BaseActivity() {

    private val viewModel by viewModel<GlobalFeedViewModel>()

    private lateinit var adapter: ActivityListRvAdapter
    private var isLoading = false

    private var maxWidth = 0
    private lateinit var markwon: Markwon

    companion object {
        const val SELECTED_FILTER = "filter"
        const val SELECTED_ACTIVITY_TYPE = "activityType"
        const val SELECTED_BEST_FRIEND = "bestFriend" // only send this if SELECTED_FILTER is FILTER_BEST_FRIEND

        const val FILTER_GLOBAL = 0
        const val FILTER_FOLLOWING = 1
        const val FILTER_BEST_FRIEND = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_global_feed)

        changeStatusBarColor(AndroidUtility.getResValueFromRefAttr(this, R.attr.themeCardColor))

        viewModel.reinitBestFriends()

        viewModel.selectedFilterIndex = when (intent.getIntExtra(SELECTED_FILTER, 0)) {
            FILTER_GLOBAL -> FILTER_GLOBAL
            FILTER_FOLLOWING -> FILTER_FOLLOWING
            FILTER_BEST_FRIEND -> {
                val selectedBestFriend = intent.getIntExtra(SELECTED_BEST_FRIEND, 0)
                val findBestFriend = viewModel.bestFriends.find { it.id == selectedBestFriend }
                if (findBestFriend != null) {
                    viewModel.bestFriends.indexOf(findBestFriend)
                } else {
                    FILTER_GLOBAL
                }
            }
            else -> FILTER_GLOBAL
        }

        viewModel.selectedActivityType = viewModel.activityTypeList[intent.getIntExtra(SELECTED_ACTIVITY_TYPE, 0)]

        setSupportActionBar(toolbarLayout)
        supportActionBar?.apply {
            title = getString(R.string.global_activity)
            setDisplayHomeAsUpEnabled(true)
        }

        maxWidth = AndroidUtility.getScreenWidth(this)
        markwon = AndroidUtility.initMarkwon(this)

        adapter = assignAdapter()
        globalFeedRecyclerView.adapter = adapter

        setupObserver()
        initLayout()
    }

    private fun setupObserver() {
        viewModel.notifyGlobalActivity.observe(this, Observer {
            if (true) {
                loadingLayout.visibility = View.VISIBLE
                isLoading = false
                viewModel.refresh()
            }
        })

        viewModel.globalActivityListResponse.observe(this, Observer {
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
                                val user = User(id = item?.user?.id!!, name = item.user.name, avatar = UserAvatar(null, item.user.avatar?.medium))
                                TextActivity(item.id, item.type, item.replyCount, item.siteUrl, item.isSubscribed, item.likeCount, item.isLiked, item.createdAt, null, null, item.userId, item.text, user)
                            }
                            viewModel.listActivityText -> {
                                val item = act.fragments.onListActivity!!
                                val media = Media(id = item.media?.id!!, title = MediaTitle(userPreferred = item.media.title?.userPreferred!!), coverImage = MediaCoverImage(null, item.media.coverImage?.medium), type = item.media.type, format = item.media.format, startDate = FuzzyDate(item.media.startDate?.year, item.media.startDate?.month, item.media.startDate?.day))
                                val user = User(id = item.user?.id!!, name = item.user.name, avatar = UserAvatar(null, item.user.avatar?.medium))
                                ListActivity(item.id, item.type, item.replyCount, item.siteUrl, item.isSubscribed, item.likeCount, item.isLiked, item.createdAt, null, null, item.userId, item.status, item.progress, media, user)
                            }
                            viewModel.messageActivityText -> {
                                val item = act.fragments.onMessageActivity!!
                                val recipient = User(id = item.recipient?.id!!, name = item.recipient.name, avatar = UserAvatar(null, item.recipient.avatar?.medium))
                                val messenger = User(id = item.messenger?.id!!, name = item.messenger.name, avatar = UserAvatar(null, item.messenger.avatar?.medium))
                                MessageActivity(item.id, item.type, item.replyCount, item.siteUrl, item.isSubscribed, item.likeCount, item.isLiked, item.createdAt, null, null, item.recipientId, item.messengerId, item.message, item.isPrivate, recipient, messenger)
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
                    DialogUtility.showToast(this, it.message)
                    if (isLoading) {
                        viewModel.activityList.removeAt(viewModel.activityList.lastIndex)
                        adapter.notifyItemRemoved(viewModel.activityList.size)
                        isLoading = false
                    }

                    emptyLayout.visibility = if (viewModel.activityList.isNullOrEmpty()) View.VISIBLE else View.GONE
                }
            }
        })

        viewModel.toggleLikeResponse.observe(this, Observer {
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
                    DialogUtility.showToast(this, it.message)
                }
            }
        })

        viewModel.toggleActivitySubscriptionResponse.observe(this, Observer {
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
                    DialogUtility.showToast(this, it.message)
                }
            }
        })

        viewModel.deleteActivityResponse.observe(this, Observer {
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
                    DialogUtility.showToast(this, it.message)
                }
            }
        })

        if (!viewModel.isInit) {
            loadingLayout.visibility = View.VISIBLE
            viewModel.getActivities()
        }
    }

    private fun initLayout() {
        globalFeedRefreshLayout.setOnRefreshListener {
            globalFeedRefreshLayout.isRefreshing = false
            loadingLayout.visibility = View.VISIBLE
            isLoading = false
            viewModel.refresh()
        }

        globalFeedRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.canScrollVertically(1) && viewModel.isInit && !isLoading) {
                    loadMore()
                    isLoading = true
                }
            }
        })

        newActivityButton.setOnClickListener {
            val intent = Intent(this, TextEditorActivity::class.java)
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
        return ActivityListRvAdapter(this, viewModel.activityList, viewModel.currentUserId, maxWidth, markwon, null, object : ActivityListener {
            override fun openActivityPage(activityId: Int) {
                val intent = Intent(this@GlobalFeedActivity, BrowseActivity::class.java)
                intent.putExtra(BrowseActivity.TARGET_PAGE, BrowsePage.ACTIVITY_DETAIL.name)
                intent.putExtra(BrowseActivity.LOAD_ID, activityId)
                startActivity(intent)
            }

            override fun openUserPage(userId: Int) {
                val intent = Intent(this@GlobalFeedActivity, BrowseActivity::class.java)
                intent.putExtra(BrowseActivity.TARGET_PAGE, BrowsePage.USER.name)
                intent.putExtra(BrowseActivity.LOAD_ID, userId)
                startActivity(intent)
            }

            override fun toggleLike(activityId: Int) {
                viewModel.toggleLike(activityId)
            }

            override fun toggleSubscribe(activityId: Int, subscribe: Boolean) {
                viewModel.toggleSubscription(activityId, subscribe)
            }

            override fun editActivity(
                activityId: Int,
                text: String,
                recipientId: Int?,
                recipientName: String?
            ) {
                val intent = Intent(this@GlobalFeedActivity, TextEditorActivity::class.java)
                intent.putExtra(TextEditorActivity.ACTIVITY_ID, activityId)
                intent.putExtra(TextEditorActivity.TEXT_CONTENT, text)
                intent.putExtra(TextEditorActivity.RECIPIENT_ID, recipientId)
                intent.putExtra(TextEditorActivity.RECIPIENT_NAME, recipientName)
                startActivityForResult(intent, EditorType.ACTIVITY.ordinal)
            }

            override fun deleteActivity(activityId: Int) {
                DialogUtility.showOptionDialog(
                    this@GlobalFeedActivity,
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
                    DialogUtility.showToast(this@GlobalFeedActivity, R.string.some_data_has_not_been_retrieved)
                    return
                }

                CustomTabsIntent.Builder().build().launchUrl(this@GlobalFeedActivity, Uri.parse(siteUrl))
            }

            override fun copyLink(siteUrl: String?) {
                if (siteUrl.isNullOrBlank()) {
                    DialogUtility.showToast(this@GlobalFeedActivity, R.string.some_data_has_not_been_retrieved)
                    return
                }

                AndroidUtility.copyToClipboard(this@GlobalFeedActivity, siteUrl)
                DialogUtility.showToast(this@GlobalFeedActivity, R.string.link_copied)
            }

            override fun openMediaPage(mediaId: Int, mediaType: MediaType?) {
                val intent = Intent(this@GlobalFeedActivity, BrowseActivity::class.java)
                intent.putExtra(BrowseActivity.TARGET_PAGE, mediaType?.name)
                intent.putExtra(BrowseActivity.LOAD_ID, mediaId)
                startActivity(intent)
            }

            override fun changeActivityType(selectedActivityType: ArrayList<ActivityType>?) {
                // do nothing
            }

            override fun changeBestFriend(selectedBestFriendPosition: Int) {
                // do nothing
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_filter, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.itemFilter) {
            val filterDialog = GlobalFeedFilterBottomSheet()
            filterDialog.setListener(object : GlobalFeedFilterBottomSheet.GlobalFeedFilterListener {
                override fun passFilterData(
                    selectedFilterIndex: Int,
                    activityTypes: ArrayList<ActivityType>?
                ) {
                    viewModel.selectedFilterIndex = selectedFilterIndex
                    viewModel.selectedActivityType = activityTypes
                    loadingLayout.visibility = View.VISIBLE
                    isLoading = false
                    viewModel.refresh()
                }
            })
            val bundle = Bundle()
            bundle.putInt(GlobalFeedFilterBottomSheet.SELECTED_FILTER, viewModel.selectedFilterIndex ?: 0)
            bundle.putInt(GlobalFeedFilterBottomSheet.SELECTED_TYPE, viewModel.activityTypeList.indexOf(viewModel.selectedActivityType))
            filterDialog.arguments = bundle
            filterDialog.show(supportFragmentManager, null)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EditorType.ACTIVITY.ordinal && resultCode == Activity.RESULT_OK) {
            loadingLayout.visibility = View.VISIBLE
            isLoading = false
            viewModel.refresh()
        }
    }
}
