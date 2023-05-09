package com.zen.alchan.ui.texteditor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.zen.alchan.R
import com.zen.alchan.databinding.ActivityTextEditorBinding
import com.zen.alchan.helper.enums.TextEditorType
import com.zen.alchan.helper.utils.DeepLink
import com.zen.alchan.ui.base.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

class TextEditorActivity : BaseActivity<ActivityTextEditorBinding>() {

    override lateinit var dialogManager: DialogManager

    override lateinit var navigationManager: NavigationManager

    private val _incomingDeepLink = PublishSubject.create<DeepLink>()
    override val incomingDeepLink: Observable<DeepLink>
        get() = _incomingDeepLink

    override fun generateViewBinding(): ActivityTextEditorBinding {
        return ActivityTextEditorBinding.inflate(layoutInflater)
    }

    override fun setUpLayout() {
        navigationManager = TextEditorNavigationManager(this, supportFragmentManager, binding.textEditorRootLayout)
        dialogManager = DefaultDialogManager(this)

        val textEditorType = TextEditorType.valueOf(intent.getStringExtra(TEXT_EDITOR_TYPE) ?: TextEditorType.TEXT_ACTIVITY.name)
        val activityId = intent.getIntExtra(ACTIVITY_ID, 0)
        val activityReplyId = intent.getIntExtra(ACTIVITY_REPLY_ID, 0)
        val recipientId = intent.getIntExtra(RECIPIENT_ID, 0)
        val username = intent.getStringExtra(USERNAME)
        navigationManager.navigateToTextEditor(textEditorType, activityId, activityReplyId, recipientId, username)
    }

    override fun setUpObserver() {
        // do nothing
    }

    companion object {
        const val ACTIVITY_ID = "activityId"
        const val ACTIVITY_REPLY_ID = "activityReplyId"
        const val RECIPIENT_ID = "recipientId"
        const val USERNAME = "username"
        const val TEXT_EDITOR_TYPE = "textEditorType"
    }
}