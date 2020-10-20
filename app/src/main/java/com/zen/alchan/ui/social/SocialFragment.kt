package com.zen.alchan.ui.social


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import com.zen.alchan.R
import com.zen.alchan.data.response.*
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.enums.EditorType
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.ListActivity
import com.zen.alchan.helper.pojo.MessageActivity
import com.zen.alchan.helper.pojo.TextActivity
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.browse.BrowseActivity
import com.zen.alchan.ui.browse.activity.ActivityListRvAdapter
import com.zen.alchan.ui.browse.activity.ActivityListener
import com.zen.alchan.ui.common.TextEditorActivity
import com.zen.alchan.ui.search.SearchActivity
import com.zen.alchan.ui.social.global.GlobalFeedActivity
import io.noties.markwon.Markwon
import kotlinx.android.synthetic.main.fragment_social.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_loading.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.ActivityType
import type.MediaType

/**
 * A simple [Fragment] subclass.
 */
class SocialFragment : Fragment() {

    private val viewModel by viewModel<SocialViewModel>()

    private lateinit var friendsActivityAdapter: ActivityListRvAdapter

    private var maxWidth = 0
    private lateinit var markwon: Markwon

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_social, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        maxWidth = AndroidUtility.getScreenWidth(activity)
        markwon = AndroidUtility.initMarkwon(activity!!)

        friendsActivityAdapter = assignAdapter()
        friendsActivityRecyclerView.adapter= friendsActivityAdapter

        if (!viewModel.enableSocial) {
            socialDisabledText.visibility = View.VISIBLE
            socialRefreshLayout.visibility = View.GONE
            newActivityButton.visibility = View.GONE
        } else {
            socialDisabledText.visibility = View.GONE
            socialRefreshLayout.visibility = View.VISIBLE
            newActivityButton.visibility = View.VISIBLE
            initLayout()
            setupObserver()
        }
    }

    private fun setupObserver() {
        viewModel.mostTrendingAnimeBannerLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                viewModel.socialFilter.bannerUrl = it
                friendsActivityAdapter.notifyItemChanged(0)
            }
        })

        viewModel.bestFriendChangedNotifier.observe(viewLifecycleOwner, Observer {
            viewModel.reinitBestFriends()
            friendsActivityAdapter.notifyDataSetChanged()
            viewModel.socialFilter.selectedBestFriend = null
            viewModel.retrieveFriendsActivity()
        })

        viewModel.notifyFriendsActivity.observe(viewLifecycleOwner, Observer {
            if (it) {
                viewModel.retrieveFriendsActivity()
            }
        })

        viewModel.friendsActivityResponse.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                    viewModel.activityList.clear()
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

                    friendsActivityAdapter = assignAdapter()
                    friendsActivityRecyclerView.adapter= friendsActivityAdapter

                    if (viewModel.activityList.size == 0) {
                        emptyLayout.visibility = View.VISIBLE
                        friendsActivityRecyclerView.visibility = View.GONE
                    } else {
                        emptyLayout.visibility = View.GONE
                        friendsActivityRecyclerView.visibility = View.VISIBLE
                    }
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                }
            }
        })

        viewModel.toggleLikeResponse.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                    val findActivity = viewModel.activityList.find { item -> item.id == it.data?.id }
                    val activityIndex = viewModel.activityList.indexOf(findActivity)
                    if (activityIndex != -1) {
                        viewModel.activityList[activityIndex].isLiked = it.data?.isLiked
                        viewModel.activityList[activityIndex].likeCount = it.data?.likeCount ?: 0
                        friendsActivityAdapter.notifyItemChanged(activityIndex + 1)
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
                    val findActivity = viewModel.activityList.find { item -> item.id == it.data?.id }
                    val activityIndex = viewModel.activityList.indexOf(findActivity)
                    if (activityIndex != -1) {
                        viewModel.activityList[activityIndex].isSubscribed = it.data?.isSubscribed
                        friendsActivityAdapter.notifyItemChanged(activityIndex + 1)
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
                    viewModel.retrieveFriendsActivity()
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                }
            }
        })

        viewModel.initData()
    }

    private fun initLayout() {
        socialRefreshLayout.setOnRefreshListener {
            socialRefreshLayout.isRefreshing = false
            viewModel.retrieveFriendsActivity()
        }

        newActivityButton.setOnClickListener {
            val intent = Intent(activity, TextEditorActivity::class.java)
            startActivityForResult(intent, EditorType.ACTIVITY.ordinal)
        }
    }

    private fun assignAdapter(): ActivityListRvAdapter {
        return ActivityListRvAdapter(activity!!, viewModel.activityList, viewModel.currentUserId, maxWidth, markwon, viewModel.socialFilter,
            object : ActivityListener {
                override fun openActivityPage(activityId: Int) {
                    val intent = Intent(activity, BrowseActivity::class.java)
                    intent.putExtra(BrowseActivity.TARGET_PAGE, BrowsePage.ACTIVITY_DETAIL.name)
                    intent.putExtra(BrowseActivity.LOAD_ID, activityId)
                    startActivity(intent)
                }

                override fun openUserPage(userId: Int) {
                    val intent = Intent(activity, BrowseActivity::class.java)
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
                    val intent = Intent(activity, BrowseActivity::class.java)
                    intent.putExtra(BrowseActivity.TARGET_PAGE, mediaType?.name)
                    intent.putExtra(BrowseActivity.LOAD_ID, mediaId)
                    startActivity(intent)
                }

                override fun changeActivityType(selectedActivityType: ArrayList<ActivityType>?) {
                    viewModel.socialFilter.selectedActivityType = selectedActivityType
                    viewModel.retrieveFriendsActivity()
                }

                override fun changeBestFriend(selectedBestFriendPosition: Int) {
                    viewModel.socialFilter.selectedBestFriend = null

                    viewModel.socialFilter.bestFriends.forEachIndexed { index, bestFriend ->
                        if (index == selectedBestFriendPosition) {
                            viewModel.socialFilter.bestFriends[index].isSelected = !viewModel.socialFilter.bestFriends[index].isSelected
                            if (viewModel.socialFilter.bestFriends[index].isSelected) {
                                viewModel.socialFilter.selectedBestFriend = viewModel.socialFilter.bestFriends[index]
                            }
                        } else {
                            viewModel.socialFilter.bestFriends[index].isSelected = false
                        }
                    }

                    viewModel.retrieveFriendsActivity()
                }
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EditorType.ACTIVITY.ordinal && resultCode == Activity.RESULT_OK) {
            viewModel.retrieveFriendsActivity()
        }
    }
}
