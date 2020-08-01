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
import type.MediaType

/**
 * A simple [Fragment] subclass.
 */
class SocialFragment : Fragment() {

    private val viewModel by viewModel<SocialViewModel>()

    private lateinit var bestFriendAdapter: BestFriendRvAdapter
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

        bestFriendAdapter = assignBestFriendAdapter()
        bestFriendsRecyclerView.adapter = bestFriendAdapter

        friendsActivityAdapter = assignAdapter()
        friendsActivityRecyclerView.adapter= friendsActivityAdapter

        if (!viewModel.enableSocial) {
            socialDisabledText.visibility = View.VISIBLE
            socialRefreshLayout.visibility = View.GONE
        } else {
            socialDisabledText.visibility = View.GONE
            socialRefreshLayout.visibility = View.VISIBLE
            initLayout()
            setupObserver()
        }
    }

    private fun setupObserver() {
        viewModel.mostTrendingAnimeBannerLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                GlideApp.with(this).load(it).into(globalActivityImage)
            } else {
                GlideApp.with(this).load(0).into(globalActivityImage)
            }
        })

        viewModel.bestFriendChangedNotifier.observe(viewLifecycleOwner, Observer {
            viewModel.reinitBestFriends()
            bestFriendAdapter.notifyDataSetChanged()
            viewModel.selectedBestFriend = null
            viewModel.retrieveFriendsActivity()
        })

        viewModel.notifyFriendsActivity.observe(viewLifecycleOwner, Observer {
            if (true) {
                viewModel.retrieveFriendsActivity()
            }
        })

        viewModel.friendsActivityResponse.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> friendsActivityLoading.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    friendsActivityLoading.visibility = View.GONE
                    viewModel.activityList.clear()
                    it.data?.page?.activities?.forEach { act ->
                        val activityItem = when (act?.__typename) {
                            viewModel.textActivityText -> {
                                val item = act.fragments.onTextActivity
                                val user = User(id = item?.user?.id!!, name = item.user.name, avatar = UserAvatar(null, item.user.avatar?.medium))
                                TextActivity(item.id, item.type, item.replyCount, item.siteUrl, item.isSubscribed, item.likeCount, item.isLiked, item.createdAt, null, null, item.userId, item.text, user)
                            }
                            viewModel.listActivityText -> {
                                val item = act.fragments.onListActivity!!
                                val media = Media(id = item.media?.id!!, title = MediaTitle(item.media.title?.userPreferred!!), coverImage = MediaCoverImage(null, item.media.coverImage?.medium), type = item.media.type, format = item.media.format, startDate = FuzzyDate(item.media.startDate?.year, item.media.startDate?.month, item.media.startDate?.day))
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
                    friendsActivityLoading.visibility = View.GONE
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
                        friendsActivityAdapter.notifyItemChanged(activityIndex)
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
                        friendsActivityAdapter.notifyItemChanged(activityIndex)
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

        visitGlobalActivityButton.setOnClickListener {
            val intent = Intent(activity, GlobalFeedActivity::class.java)
            intent.putExtra(GlobalFeedActivity.SELECTED_FILTER, GlobalFeedActivity.FILTER_GLOBAL)
            startActivity(intent)
        }

        friendsActivityViewMore.setOnClickListener {
            val intent = Intent(activity, GlobalFeedActivity::class.java)
            if (viewModel.selectedBestFriend != null) {
                intent.putExtra(GlobalFeedActivity.SELECTED_FILTER, GlobalFeedActivity.FILTER_BEST_FRIEND)
                intent.putExtra(GlobalFeedActivity.SELECTED_BEST_FRIEND, viewModel.selectedBestFriend?.id)
            } else {
                intent.putExtra(GlobalFeedActivity.SELECTED_FILTER, GlobalFeedActivity.FILTER_FOLLOWING)
            }
            intent.putExtra(GlobalFeedActivity.SELECTED_ACTIVITY_TYPE, viewModel.activityTypeList.indexOf(viewModel.selectedActivityType))
            startActivity(intent)
        }

        bestFriendInfo.setOnClickListener {
            DialogUtility.showInfoDialog(
                activity,
                R.string.best_friend_instruction
            )
        }

        friendsActivityFilterText.text = getString(viewModel.activityTypeArray[viewModel.activityTypeList.indexOf(viewModel.selectedActivityType)])

        friendsActivityFilterText.setOnClickListener {
            val activityTypeStringArray = viewModel.activityTypeArray.map { getString(it) }.toTypedArray()
            MaterialAlertDialogBuilder(activity)
                .setItems(activityTypeStringArray) { _, which ->
                    viewModel.selectedActivityType = viewModel.activityTypeList[which]
                    friendsActivityFilterText.text = getString(viewModel.activityTypeArray[which])
                    viewModel.retrieveFriendsActivity()
                }
                .show()
        }
    }

    private fun assignBestFriendAdapter(): BestFriendRvAdapter {
        return BestFriendRvAdapter(activity!!, viewModel.bestFriends, AndroidUtility.getScreenWidth(activity) / resources.getInteger(R.integer.horizontalListCharacterDivider),
            object : BestFriendRvAdapter.BestFriendListener {
                override fun passSelectedBestFriend(position: Int, id: Int?) {
                    if (position != 0) {
                        viewModel.selectedBestFriend = null

                        viewModel.bestFriends.forEachIndexed { index, bestFriend ->
                            if (index == position) {
                                viewModel.bestFriends[index].isSelected = !viewModel.bestFriends[index].isSelected
                                if (viewModel.bestFriends[index].isSelected) {
                                    viewModel.selectedBestFriend = viewModel.bestFriends[index]
                                }
                            } else {
                                viewModel.bestFriends[index].isSelected = false
                            }
                        }

                        bestFriendAdapter.notifyDataSetChanged()
                        viewModel.retrieveFriendsActivity()
                    } else {
                        val intent = Intent(activity, SearchActivity::class.java)
                        intent.putExtra(SearchActivity.SEARCH_USER, true)
                        startActivity(intent)
                    }
                }

                override fun openUserPage(id: Int) {
                    val intent = Intent(activity, BrowseActivity::class.java)
                    intent.putExtra(BrowseActivity.TARGET_PAGE, BrowsePage.USER.name)
                    intent.putExtra(BrowseActivity.LOAD_ID, id)
                    startActivity(intent)
                }
            })
    }

    private fun assignAdapter(): ActivityListRvAdapter {
        return ActivityListRvAdapter(activity!!, viewModel.activityList, viewModel.currentUserId, maxWidth, markwon,
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
                        activity,
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
