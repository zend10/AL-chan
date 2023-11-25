package com.zen.alchan.ui.settings.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.R
import com.zen.databinding.FragmentNotificationsSettingsBinding
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.ui.base.BaseFragment
import io.reactivex.rxjava3.core.Observable
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.zen.alchan.type.NotificationType

class NotificationsSettingsFragment : BaseFragment<FragmentNotificationsSettingsBinding, NotificationsSettingsViewModel>() {

    override val viewModel: NotificationsSettingsViewModel by viewModel()

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNotificationsSettingsBinding {
        return FragmentNotificationsSettingsBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            setUpToolbar(defaultToolbar.defaultToolbar, getString(R.string.notifications_settings))

            notificationsSettingsSubscribeActivityCreatedCheckBox.setOnClickListener {
                viewModel.updateNotificationOption(NotificationType.ACTIVITY_REPLY, notificationsSettingsSubscribeActivityCreatedCheckBox.isChecked)
            }

            notificationsSettingsSubscribeActivityRepliedCheckBox.setOnClickListener {
                viewModel.updateNotificationOption(NotificationType.ACTIVITY_REPLY_SUBSCRIBED, notificationsSettingsSubscribeActivityRepliedCheckBox.isChecked)
            }

            notificationsSettingsSomeoneFollowCheckBox.setOnClickListener {
                viewModel.updateNotificationOption(NotificationType.FOLLOWING, notificationsSettingsSomeoneFollowCheckBox.isChecked)
            }

            notificationsSettingsReceiveMessageCheckBox.setOnClickListener {
                viewModel.updateNotificationOption(NotificationType.ACTIVITY_MESSAGE, notificationsSettingsReceiveMessageCheckBox.isChecked)
            }

            notificationsSettingsMentionedInActivityCheckBox.setOnClickListener {
                viewModel.updateNotificationOption(NotificationType.ACTIVITY_MENTION, notificationsSettingsMentionedInActivityCheckBox.isChecked)
            }

            notificationsSettingsSomeoneLikesActivityCheckBox.setOnClickListener {
                viewModel.updateNotificationOption(NotificationType.ACTIVITY_LIKE, notificationsSettingsSomeoneLikesActivityCheckBox.isChecked)
            }

            notificationsSettingsSomeoneLikesActivityReplyCheckBox.setOnClickListener {
                viewModel.updateNotificationOption(NotificationType.ACTIVITY_REPLY_LIKE, notificationsSettingsSomeoneLikesActivityReplyCheckBox.isChecked)
            }

            notificationsSettingsSomeoneRepliesForumCheckBox.setOnClickListener {
                viewModel.updateNotificationOption(NotificationType.THREAD_COMMENT_REPLY, notificationsSettingsSomeoneRepliesForumCheckBox.isChecked)
            }

            notificationsSettingsMentionedInForumCheckBox.setOnClickListener {
                viewModel.updateNotificationOption(NotificationType.THREAD_COMMENT_MENTION, notificationsSettingsMentionedInForumCheckBox.isChecked)
            }

            notificationsSettingsSomeoneLikesForumCommentCheckBox.setOnClickListener {
                viewModel.updateNotificationOption(NotificationType.THREAD_COMMENT_LIKE, notificationsSettingsSomeoneLikesForumCommentCheckBox.isChecked)
            }

            notificationsSettingsSomeoneRepliesForumSubscribedCheckBox.setOnClickListener {
                viewModel.updateNotificationOption(NotificationType.THREAD_SUBSCRIBED, notificationsSettingsSomeoneRepliesForumSubscribedCheckBox.isChecked)
            }

            notificationsSettingsSomeoneLikesForumThreadCheckBox.setOnClickListener {
                viewModel.updateNotificationOption(NotificationType.THREAD_LIKE, notificationsSettingsSomeoneLikesForumThreadCheckBox.isChecked)
            }

            notificationsSettingsEntryCreatedCheckBox.setOnClickListener {
                viewModel.updateNotificationOption(NotificationType.RELATED_MEDIA_ADDITION, notificationsSettingsEntryCreatedCheckBox.isChecked)
            }

            notificationsSettingsDataChangedCheckBox.setOnClickListener {
                viewModel.updateNotificationOption(NotificationType.MEDIA_DATA_CHANGE, notificationsSettingsDataChangedCheckBox.isChecked)
            }

            notificationsSettingsEntryMergedCheckBox.setOnClickListener {
                viewModel.updateNotificationOption(NotificationType.MEDIA_MERGE, notificationsSettingsEntryMergedCheckBox.isChecked)
            }

            notificationsSettingsEntryDeletedCheckBox.setOnClickListener {
                viewModel.updateNotificationOption(NotificationType.MEDIA_DELETION, notificationsSettingsEntryDeletedCheckBox.isChecked)
            }

            notificationsSettingsSaveLayout.positiveButton.text = getString(R.string.save_changes)
            notificationsSettingsSaveLayout.positiveButton.clicks {
                viewModel.saveNotificationsSettings()
            }
        }
    }

    override fun setUpInsets() {
        binding.defaultToolbar.defaultToolbar.applyTopPaddingInsets()
        binding.notificationSettingsLayout.applySidePaddingInsets()
        binding.notificationsSettingsSaveLayout.oneButtonLayout.applyBottomPaddingInsets()
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.loading.subscribe {
                binding.loadingLayout.loadingLayout.show(it)
            },
            Observable.merge(viewModel.success, viewModel.error).subscribe {
                dialog.showToast(it)
            },
            viewModel.activityReply.subscribe {
                binding.notificationsSettingsSubscribeActivityCreatedCheckBox.isChecked = it
            },
            viewModel.activityReplySubscribed.subscribe {
                binding.notificationsSettingsSubscribeActivityRepliedCheckBox.isChecked = it
            },
            viewModel.following.subscribe {
                binding.notificationsSettingsSomeoneFollowCheckBox.isChecked = it
            },
            viewModel.activityMessage.subscribe {
                binding.notificationsSettingsReceiveMessageCheckBox.isChecked = it
            },
            viewModel.activityMention.subscribe {
                binding.notificationsSettingsMentionedInActivityCheckBox.isChecked = it
            },
            viewModel.activityLike.subscribe {
                binding.notificationsSettingsSomeoneLikesActivityCheckBox.isChecked = it
            },
            viewModel.activityReplyLike.subscribe {
                binding.notificationsSettingsSomeoneLikesActivityReplyCheckBox.isChecked = it
            },
            viewModel.threadCommentReply.subscribe {
                binding.notificationsSettingsSomeoneRepliesForumCheckBox.isChecked = it
            },
            viewModel.threadCommentMention.subscribe {
                binding.notificationsSettingsMentionedInForumCheckBox.isChecked = it
            },
            viewModel.threadCommentLike.subscribe {
                binding.notificationsSettingsSomeoneLikesForumCommentCheckBox.isChecked = it
            },
            viewModel.threadSubscribed.subscribe {
                binding.notificationsSettingsSomeoneRepliesForumSubscribedCheckBox.isChecked = it
            },
            viewModel.threadLike.subscribe {
                binding.notificationsSettingsSomeoneLikesForumThreadCheckBox.isChecked = it
            },
            viewModel.relatedMediaAddition.subscribe {
                binding.notificationsSettingsEntryCreatedCheckBox.isChecked = it
            },
            viewModel.mediaDataChange.subscribe {
                binding.notificationsSettingsDataChangedCheckBox.isChecked = it
            },
            viewModel.mediaMerge.subscribe {
                binding.notificationsSettingsEntryMergedCheckBox.isChecked = it
            },
            viewModel.mediaDeletion.subscribe {
                binding.notificationsSettingsEntryDeletedCheckBox.isChecked = it
            }
        )

        viewModel.loadData(Unit)
    }

    companion object {
        @JvmStatic
        fun newInstance() = NotificationsSettingsFragment()
    }
}