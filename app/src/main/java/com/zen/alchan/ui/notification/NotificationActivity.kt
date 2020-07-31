package com.zen.alchan.ui.notification

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zen.alchan.R
import com.zen.alchan.helper.changeStatusBarColor
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.base.BaseActivity
import com.zen.alchan.ui.browse.BrowseActivity
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_loading.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaType

class NotificationActivity : BaseActivity() {

    private val viewModel by viewModel<NotificationViewModel>()

    private lateinit var adapter: NotificationRvAdapter
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        changeStatusBarColor(AndroidUtility.getResValueFromRefAttr(this, R.attr.themeCardColor))

        setSupportActionBar(toolbarLayout)
        supportActionBar?.apply {
            title = getString(R.string.notifications)
            setDisplayHomeAsUpEnabled(true)
        }

        adapter = assignAdapter()
        notificationRecyclerView.adapter = adapter

        setupObserver()
        initLayout()
    }

    fun setupObserver() {
        viewModel.notificationsResponse.observe(this, Observer {
            loadingLayout.visibility = View.GONE
            when (it.responseStatus) {
                ResponseStatus.SUCCESS -> {
                    if (isLoading) {
                        viewModel.notificationList.removeAt(viewModel.notificationList.lastIndex)
                        adapter.notifyItemRemoved(viewModel.notificationList.size)
                        isLoading = false
                    }

                    if (!viewModel.hasNextPage) {
                        return@Observer
                    }

                    viewModel.hasNextPage = it.data?.page?.pageInfo?.hasNextPage ?: false
                    viewModel.page += 1
                    viewModel.isInit = true

                    it.data?.page?.notifications?.forEach { notification ->
                        viewModel.notificationList.add(notification)
                    }

                    adapter.notifyDataSetChanged()
                    emptyLayout.visibility = if (viewModel.notificationList.isNullOrEmpty()) View.VISIBLE else View.GONE
                }
                ResponseStatus.ERROR -> {
                    DialogUtility.showToast(this, it.message)

                    if (isLoading) {
                        viewModel.notificationList.removeAt(viewModel.notificationList.lastIndex)
                        adapter.notifyItemRemoved(viewModel.notificationList.size)
                        isLoading = false
                    }

                    emptyLayout.visibility = if (viewModel.notificationList.isNullOrEmpty()) View.VISIBLE else View.GONE
                }
            }
        })

        if (!viewModel.isInit) {
            loadingLayout.visibility = View.VISIBLE
            viewModel.getNotifications()
        }
    }

    fun initLayout() {
        notificationRefreshLayout.setOnRefreshListener {
            notificationRefreshLayout.isRefreshing = false
            loadingLayout.visibility = View.VISIBLE
            isLoading = false

            viewModel.page = 1
            viewModel.hasNextPage = true
            viewModel.notificationList.clear()
            adapter.notifyDataSetChanged()

            viewModel.getNotifications()
        }

        notificationTypeText.text = getString(viewModel.notificationTypesArray[viewModel.notificationTypesList.indexOf(viewModel.selectedTypes)])
        notificationTypeText.setOnClickListener {
            val stringArray = viewModel.notificationTypesArray.map { getString(it) }.toTypedArray()
            MaterialAlertDialogBuilder(this)
                .setItems(stringArray) { _, which ->
                    viewModel.selectedTypes = viewModel.notificationTypesList[which]
                    notificationTypeText.text = stringArray[which]

                    loadingLayout.visibility = View.VISIBLE
                    isLoading = false

                    viewModel.page = 1
                    viewModel.hasNextPage = true
                    viewModel.notificationList.clear()
                    adapter.notifyDataSetChanged()

                    viewModel.getNotifications()
                }
                .show()
        }

        notificationRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.canScrollVertically(1) && viewModel.isInit && !isLoading) {
                    loadMore()
                    isLoading = true
                }
            }
        })
    }

    private fun loadMore() {
        if (viewModel.hasNextPage) {
            viewModel.notificationList.add(null)
            adapter.notifyItemInserted(viewModel.notificationList.lastIndex)
            viewModel.getNotifications()
        }
    }

    private fun assignAdapter(): NotificationRvAdapter {
        return NotificationRvAdapter(this, viewModel.notificationList, object : NotificationRvAdapter.NotificationListener {
            override fun openUserPage(userId: Int) {
                openPage(BrowsePage.USER, userId)
            }

            override fun openActivityDetail(activityId: Int) {
                openPage(BrowsePage.ACTIVITY_DETAIL, activityId)
            }

            override fun openMediaPage(mediaId: Int, mediaType: MediaType) {
                openPage(BrowsePage.valueOf(mediaType.name), mediaId)
            }

            override fun openThread(threadId: Int, siteUrl: String) {
                if (siteUrl.isBlank()) {
                    DialogUtility.showToast(this@NotificationActivity, R.string.some_data_has_not_been_retrieved)
                } else {
                    CustomTabsIntent.Builder().build().launchUrl(this@NotificationActivity, Uri.parse(siteUrl))
                }
            }

            override fun openThreadReply(threadReplyId: Int, siteUrl: String) {
                if (siteUrl.isBlank()) {
                    DialogUtility.showToast(this@NotificationActivity, R.string.some_data_has_not_been_retrieved)
                } else {
                    CustomTabsIntent.Builder().build().launchUrl(this@NotificationActivity, Uri.parse(siteUrl))
                }
            }
        })
    }

    private fun openPage(targetPage: BrowsePage, id: Int) {
        val intent = Intent(this@NotificationActivity, BrowseActivity::class.java)
        intent.putExtra(BrowseActivity.TARGET_PAGE, targetPage.name)
        intent.putExtra(BrowseActivity.LOAD_ID, id)
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
