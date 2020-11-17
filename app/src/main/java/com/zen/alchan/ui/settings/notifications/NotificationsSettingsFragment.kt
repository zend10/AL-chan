package com.zen.alchan.ui.settings.notifications


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer

import com.zen.alchan.R
import com.zen.alchan.data.response.NotificationOption
import com.zen.alchan.helper.doOnApplyWindowInsets
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.updateBottomPadding
import com.zen.alchan.helper.utils.DialogUtility
import kotlinx.android.synthetic.main.fragment_notifications_settings.*
import kotlinx.android.synthetic.main.layout_loading.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.NotificationType

/**
 * A simple [Fragment] subclass.
 */
class NotificationsSettingsFragment : Fragment() {

    private val viewModel by viewModel<NotificationsSettingsViewModel>()

    private lateinit var itemSave: MenuItem
    private lateinit var checkBoxList: HashMap<CheckBox, NotificationType>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications_settings, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        toolbarLayout.apply {
            title = getString(R.string.notifications_settings)
            navigationIcon = ContextCompat.getDrawable(activity!!, R.drawable.ic_left)
            setNavigationOnClickListener { activity?.onBackPressed() }

            inflateMenu(R.menu.menu_save)
            itemSave = menu.findItem(R.id.itemSave)
        }

        notificationsSettingsLayout.doOnApplyWindowInsets { view, windowInsets, initialPadding ->
            view.updateBottomPadding(windowInsets, initialPadding)
        }

        checkBoxList = hashMapOf(
            Pair(activityReplyCheckBox, NotificationType.ACTIVITY_REPLY),
            Pair(activityReplySubscribedCheckBox, NotificationType.ACTIVITY_REPLY_SUBSCRIBED),
            Pair(followingCheckBox, NotificationType.FOLLOWING),
            Pair(activityMessageCheckBox, NotificationType.ACTIVITY_MESSAGE),
            Pair(activityMentionCheckBox, NotificationType.ACTIVITY_MENTION),
            Pair(activityLikeCheckBox, NotificationType.ACTIVITY_LIKE),
            Pair(activityReplyLikeCheckBox, NotificationType.ACTIVITY_REPLY_LIKE),
            Pair(threadCommentReplyCheckBox, NotificationType.THREAD_COMMENT_REPLY),
            Pair(threadCommentMentionCheckBox, NotificationType.THREAD_COMMENT_MENTION),
            Pair(threadCommentLikeCheckBox, NotificationType.THREAD_COMMENT_LIKE),
            Pair(threadSubscribedCheckBox, NotificationType.THREAD_SUBSCRIBED),
            Pair(threadLikeCheckBox, NotificationType.THREAD_LIKE)
        )

        setupObserver()
        initLayout()
    }

    private fun setupObserver() {
        viewModel.viewerData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                initLayout()
            }
        })

        viewModel.updateAniListSettingsResponse.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> {
                    loadingLayout.visibility = View.VISIBLE
                }
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(activity, R.string.settings_saved)
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
        val options = viewModel.viewerData.value?.options?.notificationOptions

        /*
            Automatically subscribe me to activity I create -> ACTIVITY_REPLY
            Automatically subscribe me to activity I reply to -> ACTIVITY_REPLY_SUBSCRIBED

            When someone follows me -> FOLLOWING
            When I receive message -> ACTIVITY_MESSAGE
            When I am @ mentioned in an activity or activity reply -> ACTIVITY_MENTION
            When someone likes my activity -> ACTIVITY_LIKE
            When someone likes my activity reply -> ACTIVITY_REPLY_LIKE
            When someone replies to my forum comment -> THREAD_COMMENT_REPLY
            When I am @ mentioned in a forum comment -> THREAD_COMMENT_MENTION
            When someone likes my forum comment -> THREAD_COMMENT_LIKE
            When someone replies to a forum thread I'm subscribed to -> THREAD_SUBSCRIBED
            When someone likes my forum thread -> THREAD_LIKE

            AIRING and RELATED_MEDIA_ADDITION are not in AniList notification settings
         */

        if (!viewModel.isInit) {
            checkBoxList.forEach {
                it.key.isChecked = options?.find { option -> option.type == it.value }?.enabled == true
            }
            viewModel.isInit = true
        }

        itemSave.setOnMenuItemClickListener {
            val optionList = ArrayList<NotificationOption>()
            checkBoxList.forEach {
                optionList.add(NotificationOption(it.value, it.key.isChecked))
            }
            viewModel.updateNotificationsSettings(optionList)
            true
        }
    }
}
