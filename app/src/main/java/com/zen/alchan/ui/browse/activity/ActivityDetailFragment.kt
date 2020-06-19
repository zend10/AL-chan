package com.zen.alchan.ui.browse.activity


import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.bumptech.glide.request.RequestOptions

import com.zen.alchan.R
import com.zen.alchan.data.response.*
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.ListActivity
import com.zen.alchan.helper.pojo.MessageActivity
import com.zen.alchan.helper.pojo.TextActivity
import com.zen.alchan.helper.replaceUnderscore
import com.zen.alchan.helper.secondsToDateTime
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.base.BaseFragment
import io.noties.markwon.Markwon
import kotlinx.android.synthetic.main.fragment_activity_detail.*
import kotlinx.android.synthetic.main.layout_loading.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class ActivityDetailFragment : BaseFragment() {

    private val viewModel by viewModel<ActivityDetailViewModel>()

    private lateinit var likesRvAdapter: ActivityLikesRvAdapter
    private lateinit var repliesRvAdapter: ActivityRepliesRvAdapter

    private var maxWidth = 0
    private lateinit var markwon: Markwon

    companion object {
        const val ACTIVITY_ID = "activityId"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_activity_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.activityId = arguments?.getInt(ACTIVITY_ID)

        maxWidth = AndroidUtility.getScreenWidth(activity)
        markwon = AndroidUtility.initMarkwon(activity!!)

        toolbarLayout.setNavigationOnClickListener { activity?.finish() }
        toolbarLayout.navigationIcon = ContextCompat.getDrawable(activity!!, R.drawable.ic_delete)
        toolbarLayout.inflateMenu(R.menu.menu_activity_detail)
        toolbarLayout.menu.findItem(R.id.itemReply).setOnMenuItemClickListener {
            // open editor
            true
        }

        setupObserver()
        initLayout()
    }

    private fun setupObserver() {
        viewModel.activityDetailResponse.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE

                    val act = it.data?.activity
                    val replies = viewModel.getReplies(act?.__typename, act?.fragments)
                    val likes = viewModel.getLikes(act?.__typename, act?.fragments)

                    viewModel.activityDetail = when (act?.__typename) {
                        viewModel.TEXT_ACTIVITY -> {
                            val item = act.fragments.onTextActivity
                            val user = User(id = item?.user?.id!!, name = item.user.name, avatar = UserAvatar(null, item.user.avatar?.medium))
                            TextActivity(item.id, item.type, item.replyCount, item.siteUrl, item.isSubscribed, item.likeCount, item.isLiked, item.createdAt, replies, likes, item.userId, item.text, user)
                        }
                        viewModel.LIST_ACTIVITY -> {
                            val item = act.fragments.onListActivity!!
                            val media = Media(id = item.media?.id!!, title = MediaTitle(item.media.title?.userPreferred!!), coverImage = MediaCoverImage(null, item.media.coverImage?.medium), type = item.media.type, format = item.media.format, startDate = FuzzyDate(item.media.startDate?.year, item.media.startDate?.month, item.media.startDate?.day))
                            val user = User(id = item.user?.id!!, name = item.user.name, avatar = UserAvatar(null, item.user.avatar?.medium))
                            ListActivity(item.id, item.type, item.replyCount, item.siteUrl, item.isSubscribed, item.likeCount, item.isLiked, item.createdAt, replies, likes, item.userId, item.status, item.progress, media, user)
                        }
                        viewModel.MESSAGE_ACTIVITY -> {
                            val item = act.fragments.onMessageActivity!!
                            val recipient = User(id = item.recipient?.id!!, name = item.recipient.name, avatar = UserAvatar(null, item.recipient.avatar?.medium))
                            val messenger = User(id = item.messenger?.id!!, name = item.messenger.name, avatar = UserAvatar(null, item.messenger.avatar?.medium))
                            MessageActivity(item.id, item.type, item.replyCount, item.siteUrl, item.isSubscribed, item.likeCount, item.isLiked, item.createdAt, replies, likes, item.recipientId, item.messengerId, item.message, item.isPrivate, recipient, messenger)
                        }
                        else -> null
                    }

                    initLayout()
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                }
            }
        })

        if (viewModel.activityDetail == null) {
            viewModel.getActivityDetail()
        }
    }

    private fun initLayout() {
        activityDetailRefreshLayout.setOnRefreshListener {
            activityDetailRefreshLayout.isRefreshing = false
            viewModel.getActivityDetail()
        }

        if (viewModel.activityDetail == null) {
            return
        }

        val act = viewModel.activityDetail!!

        activityListLayout.visibility = View.GONE
        activityTextLayout.visibility = View.GONE

        timeText.text = act.createdAt.secondsToDateTime()

        activityReplyText.text = if (act.replyCount != 0) act.replyCount.toString() else ""
        activityReplyLayout.setOnClickListener {
            // open editor
        }

        activityLikeIcon.imageTintList = if (act.isLiked == true) {
            ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(context, R.attr.themePrimaryColor))
        } else {
            ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(context, R.attr.themeContentColor))
        }
        activityLikeText.text = if (act.likeCount != 0) act.likeCount.toString() else ""
        activityLikeLayout.setOnClickListener {
            // toggle like
        }

        activitySubscribeIcon.imageTintList = if (act.isSubscribed == true) {
            ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(context, R.attr.themePrimaryColor))
        } else {
            ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(context, R.attr.themeContentColor))
        }
        activitySubscribeLayout.setOnClickListener {
            // toggle subscribe
        }

        activityMoreLayout.setOnClickListener {
            // view pop up menu (edit, delete, view in anilist, copy link)
            val popupMenu = PopupMenu(activity!!, it)
            popupMenu.menuInflater.inflate(R.menu.menu_activity, popupMenu.menu)

            popupMenu.menu.apply {
                findItem(R.id.itemEdit).isVisible = (act is TextActivity && act.userId == viewModel.userId) ||
                        (act is MessageActivity && act.messengerId == viewModel.userId)
                findItem(R.id.itemDelete).isVisible = (act is TextActivity && act.userId == viewModel.userId) ||
                        (act is ListActivity && act.userId == viewModel.userId) ||
                        (act is MessageActivity && (act.recipientId == viewModel.userId || act.messengerId == viewModel.userId))
            }

            popupMenu.setOnMenuItemClickListener { menuItem: MenuItem? ->
                when (menuItem?.itemId) {
                    R.id.itemEdit -> editActivity(act.id)
                    R.id.itemDelete -> deleteActivity(act.id)
                    R.id.itemViewOnAniList -> viewOnAniList(act.siteUrl)
                    R.id.itemCopyLink -> copyLink(act.siteUrl)
                }
                true
            }
            popupMenu.show()
        }

        if (viewModel.activityDetail?.likes.isNullOrEmpty()) {
            likesLayout.visibility = View.GONE
        } else {
            likesLayout.visibility = View.VISIBLE
        }

        likesRvAdapter = assignLikesAdapter()
        likesRecyclerView.adapter = likesRvAdapter

        if (viewModel.activityDetail?.replies.isNullOrEmpty()) {
            activityRepliesRecyclerView.visibility = View.GONE
        } else {
            activityRepliesRecyclerView.visibility = View.VISIBLE
        }

        repliesRvAdapter = assignRepliesAdapter()
        activityRepliesRecyclerView.adapter = repliesRvAdapter

        when (act) {
            is TextActivity -> handleTextActivityLayout(act)
            is ListActivity -> handleListActivityLayout(act)
            is MessageActivity -> handleMessageActivityLayout(act)
        }
    }

    private fun handleTextActivityLayout(act: TextActivity) {
        nameText.text = act.user?.name
        nameText.setOnClickListener {
            listener?.changeFragment(BrowsePage.USER, act.userId!!)
        }

        GlideApp.with(this).load(act.user?.avatar?.medium).apply(RequestOptions.circleCropTransform()).into(avatarImage)
        avatarImage.setOnClickListener {
            listener?.changeFragment(BrowsePage.USER, act.userId!!)
        }

        AndroidUtility.convertMarkdown(activity!!, activityTextLayout, act.text, maxWidth, markwon)

        activityListLayout.visibility = View.GONE
        activityTextLayout.visibility = View.VISIBLE
        recipientLayout.visibility = View.GONE
        privateLayout.visibility = View.GONE
    }

    private fun handleListActivityLayout(act: ListActivity) {
        nameText.text = act.user?.name
        nameText.setOnClickListener {
            listener?.changeFragment(BrowsePage.USER, act.userId!!)
        }

        GlideApp.with(this).load(act.user?.avatar?.medium).apply(RequestOptions.circleCropTransform()).into(avatarImage)
        avatarImage.setOnClickListener {
            listener?.changeFragment(BrowsePage.USER, act.userId!!)
        }

        listActivityText.text = "${act.status?.capitalize()}${if (act.progress != null) " ${act.progress} of " else " "}${act.media?.title?.userPreferred}"

        mediaTitleText.text = act.media?.title?.userPreferred

        GlideApp.with(this).load(act.media?.coverImage?.medium).into(mediaImage)

        mediaYearText.text = if (act.media?.startDate?.year != null) {
            act.media.startDate.year.toString()
        } else {
            "TBA"
        }

        mediaTypeText.text = act.media?.type?.name

        if (act.media?.format != null) {
            mediaFormatDivider.visibility = View.VISIBLE
            mediaFormatText.text = act.media.format.name.replaceUnderscore()
        } else {
            mediaFormatDivider.visibility = View.GONE
            mediaFormatText.text = ""
        }

        mediaLayout.setOnClickListener {
            listener?.changeFragment(BrowsePage.valueOf(act.media?.type!!.name), act.media.id)
        }

        activityListLayout.visibility = View.VISIBLE
        activityTextLayout.visibility = View.GONE
        recipientLayout.visibility = View.GONE
        privateLayout.visibility = View.GONE
    }

    private fun handleMessageActivityLayout(act: MessageActivity) {
        nameText.text = act.messenger?.name ?: ""
        nameText.setOnClickListener {
            listener?.changeFragment(BrowsePage.USER, act.messengerId!!)
        }
        GlideApp.with(this).load(act.messenger?.avatar?.medium).apply(RequestOptions.circleCropTransform()).into(avatarImage)
        avatarImage.setOnClickListener {
            listener?.changeFragment(BrowsePage.USER, act.messengerId!!)
        }

        recipientLayout.visibility = View.VISIBLE
        recipientNameText.text = act.recipient?.name ?: ""
        recipientNameText.setOnClickListener {
            listener?.changeFragment(BrowsePage.USER, act.recipientId!!)
        }

        privateLayout.visibility = if (act.isPrivate == true) View.VISIBLE else View.GONE

        AndroidUtility.convertMarkdown(activity!!, activityTextLayout, act.message, maxWidth, markwon)

        activityListLayout.visibility = View.GONE
        activityTextLayout.visibility = View.VISIBLE
        recipientLayout.visibility = View.VISIBLE
    }

    private fun editActivity(activityId: Int) {

    }

    private fun deleteActivity(activityId: Int) {

    }

    private fun viewOnAniList(siteUrl: String?) {
        if (siteUrl.isNullOrBlank()) {
            DialogUtility.showToast(activity, R.string.some_data_has_not_been_retrieved)
            return
        }

        CustomTabsIntent.Builder().build().launchUrl(activity!!, Uri.parse(siteUrl))
    }

    private fun copyLink(siteUrl: String?) {
        if (siteUrl.isNullOrBlank()) {
            DialogUtility.showToast(activity, R.string.some_data_has_not_been_retrieved)
            return
        }

        AndroidUtility.copyToClipboard(activity, siteUrl)
        DialogUtility.showToast(activity, R.string.link_copied)
    }

    private fun assignLikesAdapter(): ActivityLikesRvAdapter {
        return ActivityLikesRvAdapter(
            activity!!,
            viewModel.activityDetail?.likes ?: listOf(),
            maxWidth / 10,
            object : ActivityLikesRvAdapter.ActivityLikesListener {
                override fun openUserPage(userId: Int) {
                    listener?.changeFragment(BrowsePage.USER, userId)
                }

                override fun showUsername(name: String) {
                    DialogUtility.showToast(activity, name)
                }
            }
        )
    }

    private fun assignRepliesAdapter(): ActivityRepliesRvAdapter {
        return ActivityRepliesRvAdapter(
            activity!!,
            viewModel.activityDetail?.replies ?: listOf(),
            viewModel.userId,
            maxWidth,
            markwon,
            object : ActivityRepliesRvAdapter.ActivityRepliesListener {
                override fun openUserPage(userId: Int) {
                    listener?.changeFragment(BrowsePage.USER, userId)
                }

                override fun editReply(replyId: Int) {
                    // open editor
                }

                override fun deleteReply(replyId: Int) {
                    // delete
                }
            }
        )
    }
}
