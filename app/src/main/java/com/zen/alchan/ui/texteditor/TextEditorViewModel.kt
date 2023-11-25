package com.zen.alchan.ui.texteditor

import com.zen.R
import com.zen.alchan.data.repository.SocialRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.enums.TextEditorType
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

class TextEditorViewModel(
    private val socialRepository: SocialRepository,
    private val userRepository: UserRepository
) : BaseViewModel<TextEditorParam>() {

    private val _postDialog = PublishSubject.create<Pair<Pair<Int, Int>, Triple<Int, Int, Int?>>>()
    val postDialog: Observable<Pair<Pair<Int, Int>, Triple<Int, Int, Int?>>> // first pair is title and text, second pair is button
        get() = _postDialog

    private val _mentionUser = PublishSubject.create<String>()
    val mentionUser: Observable<String>
        get() = _mentionUser

    private var activityId: Int? = null
    private var activityReplyId: Int? = null
    private var recipientId: Int? = null
    private var username: String? = null
    private var textEditorType: TextEditorType = TextEditorType.TEXT_ACTIVITY

    override fun loadData(param: TextEditorParam) {
        loadOnce {
            textEditorType = param.textEditorType
            activityId = param.activityId
            activityReplyId = param.activityReplyId
            recipientId = param.recipientId
            username = param.username

            username?.let {
                _mentionUser.onNext("@$username")
            }

            if (activityReplyId != null) {
                disposables.add(
                    socialRepository.replyToBeEdited
                        .filter { it.data != null }
                        .map { it.data!! }
                        .applyScheduler()
                        .subscribe {
                            _mentionUser.onNext(it?.text ?: "")
                            socialRepository.clearReplyToBeEdited()
                        }
                )
            } else if (activityId != null) {
                disposables.add(
                    socialRepository.activityToBeEdited
                        .filter { it.data != null }
                        .map { it.data!! }
                        .zipWith(userRepository.getAppSetting()) { activity, appSetting ->
                            activity to appSetting
                        }
                        .applyScheduler()
                        .subscribe { (activity, appSetting) ->
                            _mentionUser.onNext(activity.message(appSetting))
                            socialRepository.clearActivityToBeEdited()
                        }
                )
            }
        }
    }

    fun postActivity(text: String, sendPrivate: Boolean) {
        _loading.onNext(true)

        val completable = when (textEditorType) {
            TextEditorType.TEXT_ACTIVITY -> Completable.fromObservable(socialRepository.saveTextActivity(activityId, text))
            TextEditorType.ACTIVITY_REPLY -> Completable.fromObservable(socialRepository.saveActivityReply(activityReplyId, activityId ?: 0, text))
            TextEditorType.MESSAGE -> Completable.fromObservable(socialRepository.saveMessageActivity(activityId, recipientId ?: 0, text, sendPrivate))
            TextEditorType.REVIEW -> Completable.never()
        }

        disposables.add(
            completable
                .applyScheduler()
                .doFinally {
                    _loading.onNext(false)
                }
                .subscribe(
                    {
                        _success.onNext(
                            when (textEditorType) {
                                TextEditorType.TEXT_ACTIVITY -> R.string.message_posted
                                TextEditorType.ACTIVITY_REPLY -> R.string.reply_sent
                                TextEditorType.MESSAGE -> R.string.message_sent
                                TextEditorType.REVIEW -> R.string.review_posted
                            }
                        )
                    },
                    {
                        _error.onNext(it.getStringResource())
                    }
                )
        )
    }

    fun loadPostDialogDetail(text: String) {
        if (text.isBlank()) {
            _error.onNext(R.string.please_write_something)
            return
        }

        if (textEditorType == TextEditorType.TEXT_ACTIVITY && text.length < TEXT_ACTIVITY_MIN_CHAR_COUNT) {
            _error.onNext(R.string.the_text_must_be_at_least_5_characters)
            return
        }

        when (textEditorType) {
            TextEditorType.TEXT_ACTIVITY -> {
                val title = R.string.post_this_message
                val message = R.string.are_you_sure_you_want_to_post_this_message
                val positiveButton = R.string.post
                val negativeButton = R.string.cancel
                _postDialog.onNext(
                    Pair(
                        Pair(title, message),
                        Triple(positiveButton, negativeButton, null)
                    )
                )
            }
            TextEditorType.ACTIVITY_REPLY -> {
                val title = R.string.send_this_reply
                val message = R.string.are_you_sure_you_want_to_send_this_reply
                val positiveButton = R.string.send
                val negativeButton = R.string.cancel
                _postDialog.onNext(
                    Pair(
                        Pair(title, message),
                        Triple(positiveButton, negativeButton, null)
                    )
                )
            }
            TextEditorType.MESSAGE -> {
                val title = R.string.send_this_message
                val message = R.string.are_you_sure_you_want_to_send_this_message
                val positiveButton = R.string.send
                val negativeButton = R.string.cancel
                val thirdButton = if (activityId == null) R.string.send_private else null
                _postDialog.onNext(
                    Pair(
                        Pair(title, message),
                        Triple(positiveButton, negativeButton, thirdButton)
                    )
                )
            }
            TextEditorType.REVIEW -> {

            }
        }
    }

    companion object {
        private const val TEXT_ACTIVITY_MIN_CHAR_COUNT = 5
    }
}