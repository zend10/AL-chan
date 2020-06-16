package com.zen.alchan.ui.social


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.ActivityReply
import com.zen.alchan.helper.pojo.ListActivity
import com.zen.alchan.helper.pojo.MessageActivity
import com.zen.alchan.helper.pojo.TextActivity
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.browse.BrowseActivity
import com.zen.alchan.ui.search.SearchActivity
import io.noties.markwon.Markwon
import kotlinx.android.synthetic.main.fragment_social.*
import kotlinx.android.synthetic.main.layout_empty.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaType

/**
 * A simple [Fragment] subclass.
 */
class SocialFragment : Fragment() {

    private val viewModel by viewModel<SocialViewModel>()

    private lateinit var bestFriendAdapter: BestFriendRvAdapter
    private lateinit var friendsActivityAdapter: FriendsActivityRvAdapter

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

        initLayout()
        setupObserver()
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

        viewModel.friendsActivityResponse.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> friendsActivityLoading.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    friendsActivityLoading.visibility = View.GONE
                    viewModel.activityList.clear()
                    it.data?.page?.activities?.forEach { act ->
                        val activityItem = when (act?.__typename) {
                            viewModel.TEXT_ACTIVITY -> {
                                val item = act.fragments.onTextActivity
                                val replies = viewModel.getReplies(act.__typename, act.fragments)
                                val likes = viewModel.getLikes(act.__typename, act.fragments)
                                val user = User(id = item?.user?.id!!, name = item.user.name, avatar = UserAvatar(null, item.user.avatar?.medium))
                                TextActivity(item.id, item.type, item.replyCount, item.siteUrl, item.isSubscribed, item.likeCount, item.isLiked, item.createdAt, replies, likes, item.userId, item.text, user)
                            }
                            viewModel.LIST_ACTIVITY -> {
                                val item = act.fragments.onListActivity!!
                                val replies = viewModel.getReplies(viewModel.LIST_ACTIVITY, act.fragments)
                                val likes = viewModel.getLikes(viewModel.LIST_ACTIVITY, act.fragments)
                                val media = Media(id = item.media?.id!!, title = MediaTitle(item.media.title?.userPreferred!!), coverImage = MediaCoverImage(null, item.media.coverImage?.medium), type = item.media.type, format = item.media.format, startDate = FuzzyDate(item.media.startDate?.year, item.media.startDate?.month, item.media.startDate?.day))
                                val user = User(id = item.user?.id!!, name = item.user.name, avatar = UserAvatar(null, item.user.avatar?.medium))
                                ListActivity(item.id, item.type, item.replyCount, item.siteUrl, item.isSubscribed, item.likeCount, item.isLiked, item.createdAt, replies, likes, item.userId, item.status, item.progress, media, user)
                            }
                            viewModel.MESSAGE_ACTIVITY -> {
                                val item = act.fragments.onMessageActivity!!
                                val replies = viewModel.getReplies(viewModel.MESSAGE_ACTIVITY, act.fragments)
                                val likes = viewModel.getLikes(viewModel.MESSAGE_ACTIVITY, act.fragments)
                                val recipient = User(id = item.recipient?.id!!, name = item.recipient.name, avatar = UserAvatar(null, item.recipient.avatar?.medium))
                                val messenger = User(id = item.messenger?.id!!, name = item.messenger.name, avatar = UserAvatar(null, item.messenger.avatar?.medium))
                                MessageActivity(item.id, item.type, item.replyCount, item.siteUrl, item.isSubscribed, item.likeCount, item.isLiked, item.createdAt, replies, likes, item.recipientId, item.messengerId, item.message, item.isPrivate, recipient, messenger)
                            }
                            else -> null
                        }

                        if (activityItem != null) {
                            viewModel.activityList.add(activityItem)
                        }
                    }

                    friendsActivityAdapter.notifyDataSetChanged()

                    if (viewModel.activityList.size == 0) {
                        emptyLayout.visibility = View.VISIBLE
                        friendsActivityRecyclerView.visibility = View.GONE
                    }
                }
                ResponseStatus.ERROR -> {
                    friendsActivityLoading.visibility = View.GONE
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
            // open browse type activity
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
        return BestFriendRvAdapter(activity!!, viewModel.bestFriends, AndroidUtility.getScreenWidth(activity) / 5,
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

    private fun assignAdapter(): FriendsActivityRvAdapter {
        return FriendsActivityRvAdapter(activity!!, viewModel.activityList, viewModel.currentUserId, maxWidth, markwon,
            object : ActivityListener {
                override fun openActivityPage(activityId: Int) {
                    // open browse
                }

                override fun openUserPage(userId: Int) {
                    val intent = Intent(activity, BrowseActivity::class.java)
                    intent.putExtra(BrowseActivity.TARGET_PAGE, BrowsePage.USER.name)
                    intent.putExtra(BrowseActivity.LOAD_ID, userId)
                    startActivity(intent)
                }

                override fun toggleLike(activityId: Int) {
                    // call api
                }

                override fun toggleSubscribe(activityId: Int) {
                    // call api
                }

                override fun editActivity(activityId: Int) {
                    // open new activity
                }

                override fun deleteActivity(activityId: Int) {
                    // call api
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
}
