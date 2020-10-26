package com.zen.alchan.ui.common

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.ImageViewCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zen.alchan.R
import com.zen.alchan.helper.enums.EditorType
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_spoiler.*
import kotlinx.android.synthetic.main.activity_text_editor.*
import kotlinx.android.synthetic.main.dialog_input.*
import kotlinx.android.synthetic.main.dialog_input.view.*
import kotlinx.android.synthetic.main.layout_loading.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.net.URLEncoder

class TextEditorActivity : BaseActivity() {

    private val viewModel by viewModel<TextEditorViewModel>()

    private lateinit var rangeMarkdownLayout: ArrayList<AppCompatImageView>
    private lateinit var onlyStartMarkdownLayout: ArrayList<AppCompatImageView>
    private lateinit var dialogMarkdownLayout: ArrayList<AppCompatImageView>

    private val rangeMarkdown = arrayListOf(
        "____", "__", "~~~~", "~!!~", "~~~~~~", "``"
    )

    private val onlyStartMarkdown = arrayListOf(
        "1. ", "- ", "# ", "> "
    )

    private val dialogMarkdown = arrayListOf(
        "[link ]", "img220", "youtube", "webm"
    )

    companion object {
        const val EDITOR_TYPE = "editorType" // always required

        const val ACTIVITY_ID = "activityId" // needed if edit activity or reply to activity
        const val REPLY_ID = "replyId" // needed if edit a reply
        const val TEXT_CONTENT = "textContent" // needed if edit
        const val RECIPIENT_ID = "recipientId" // needed if send a message
        const val RECIPIENT_NAME = "recipientName" // needed if send a message
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_editor)

        viewModel.editorType = EditorType.valueOf(intent.getStringExtra(EDITOR_TYPE) ?: EditorType.ACTIVITY.name)

        textLimit.visibility = View.GONE

        if (viewModel.editorType == EditorType.REVIEW) {
            textLimit.visibility = View.VISIBLE
            viewModel.originalText = intent.getStringExtra(TEXT_CONTENT)
            initReviewLayout()
        } else if (viewModel.editorType == EditorType.ACTIVITY_REPLY) {
            viewModel.activityId = intent.getIntExtra(ACTIVITY_ID, 0)

            // if edit
            if (intent.getIntExtra(REPLY_ID, 0) != 0) {
                viewModel.replyId = intent.getIntExtra(REPLY_ID, 0)
                viewModel.originalText = intent.getStringExtra(TEXT_CONTENT)
            }
        } else {
            // if edit
            if (intent.getIntExtra(ACTIVITY_ID, 0) != 0) {
                viewModel.activityId = intent.getIntExtra(ACTIVITY_ID, 0)
                viewModel.originalText = intent.getStringExtra(TEXT_CONTENT)
            }

            // if has recipient
            if (intent.getIntExtra(RECIPIENT_ID, 0) != 0) {
                viewModel.recipientId = intent.getIntExtra(RECIPIENT_ID, 0)
                viewModel.recipientName = intent.getStringExtra(RECIPIENT_NAME)
            } else {
                textLimit.visibility = View.VISIBLE
            }
        }

        setSupportActionBar(toolbarLayout)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        supportActionBar?.title = when (viewModel.editorType) {
            EditorType.ACTIVITY -> {
                if (viewModel.activityId != null) {
                    getString(R.string.edit_message)
                } else if (viewModel.recipientId != null) {
                    getString(R.string.send_message)
                } else {
                    getString(R.string.post_new_message)
                }
            }
            EditorType.ACTIVITY_REPLY -> {
                if (viewModel.replyId != null) {
                    getString(R.string.edit_message)
                } else {
                    getString(R.string.reply_message)
                }
            }
            EditorType.REVIEW -> {
                getString(R.string.write_review)
            }
            else -> getString(R.string.post_new_message)
        }

        rangeMarkdownLayout = arrayListOf(
            formatBoldIcon, formatItalicIcon, formatStrikeThroughIcon, formatSpoilerIcon, formatCenterIcon, formatCodeIcon
        )

        onlyStartMarkdownLayout = arrayListOf(
            formatOrderedListIcon, formatUnorderedListIcon, formatHeaderIcon, formatQuoteIcon
        )

        dialogMarkdownLayout = arrayListOf(
            formatLinkIcon, formatImageIcon, formatYoutubeIcon, formatVideoIcon
        )

        setupObserver()
        initLayout()
    }

    private fun setupObserver() {
        viewModel.postTextActivityResponse.observe(this, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(this, R.string.message_posted)

                    val intent = Intent()
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(this, it.message)
                }
            }
        })

        viewModel.postMessageActivityResponse.observe(this, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(this, R.string.message_sent)

                    val intent = Intent()
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(this, it.message)
                }
            }
        })

        viewModel.postActivityReplyResponse.observe(this, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(this, R.string.reply_sent)

                    val intent = Intent()
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(this, it.message)
                }
            }
        })
    }

    private fun initLayout() {
        editorEditText.requestFocus()

        if (!viewModel.originalText.isNullOrBlank() && !viewModel.isInit) {
            editorEditText.setText(viewModel.originalText)
            viewModel.isInit = true
        }

        rangeMarkdownLayout.forEachIndexed { index, appCompatImageView ->
            appCompatImageView.setOnClickListener {
                val start = editorEditText.selectionStart
                val end = editorEditText.selectionEnd
                val markdown = rangeMarkdown[index]
                if (start == end) {
                    editorEditText.text?.insert(start, markdown)
                } else {
                    editorEditText.text?.insert(end, markdown.substring(markdown.length / 2))
                    editorEditText.text?.insert(start, markdown.substring(0, markdown.length / 2))
                }
            }
        }

        onlyStartMarkdownLayout.forEachIndexed { index, appCompatImageView ->
            appCompatImageView.setOnClickListener {
                val start = editorEditText.selectionStart
                val markdown = onlyStartMarkdown[index]
                editorEditText.text?.insert(start, markdown)
            }
        }

        dialogMarkdownLayout.forEachIndexed { index, appCompatImageView ->
            appCompatImageView.setOnClickListener {
                val inputDialogView = layoutInflater.inflate(R.layout.dialog_input, inputDialogLayout, false)
                val title = when (index) {
                    0 -> R.string.please_input_a_url
                    1 -> R.string.please_input_an_image_url
                    2 -> R.string.please_input_a_youtube_video_url
                    3 -> R.string.please_input_a_webm_video_url
                    else -> R.string.please_input_a_url
                }
                DialogUtility.showCustomViewDialog(
                    this,
                    title,
                    inputDialogView,
                    R.string.add,
                    {
                        val newEntry = inputDialogView.inputField.text.toString().trim()
                        if (newEntry.isNotBlank()) {
                            val start = editorEditText.selectionStart
                            val markdown = dialogMarkdown[index]
                            editorEditText.text?.insert(start, "${markdown}(${newEntry})")
                        }
                    },
                    R.string.cancel,
                    { }
                )
            }
        }

        previewButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("alchan://spoiler?data=${URLEncoder.encode(editorEditText.text?.trim().toString(), "utf-8")}")
            startActivity(intent)
        }
    }

    private fun initReviewLayout() {
        textLimit.text = "${getString(R.string.the_text_must_be_at_least_2200_characters)} (${editorEditText.text?.toString()?.replace(" ", "")?.replace("\n", "")?.trim()?.length}/2200)"

        editorEditText.addTextChangedListener {
            textLimit.text = "${getString(R.string.the_text_must_be_at_least_2200_characters)} (${it?.toString()?.replace(" ", "")?.replace("\n", "")?.trim()?.length}/2200)"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_post, menu)

        if (viewModel.editorType == EditorType.REVIEW) {
            menu?.findItem(R.id.itemPost)?.title = getString(R.string.save)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.itemPost) {
            if (editorEditText.text?.trim().isNullOrBlank()) {
                DialogUtility.showToast(this, R.string.please_write_something)
                return false
            }

            if (viewModel.editorType == EditorType.REVIEW) {
                val intent = Intent()
                intent.putExtra(TEXT_CONTENT, editorEditText.text?.trim())
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                var title = R.string.post_this_message
                var message = getString(R.string.are_you_sure_you_want_to_post_this_message)
                var positiveText = R.string.post

                if (viewModel.editorType == EditorType.ACTIVITY_REPLY) {
                    if (viewModel.replyId != null) {
                        title = R.string.edit_this_message
                        message = getString(R.string.are_you_sure_you_want_to_edit_this_message)
                        positiveText = R.string.edit
                    } else {
                        title = R.string.send_this_reply
                        message = getString(R.string.are_you_sure_you_want_to_send_this_reply)
                        positiveText = R.string.send
                    }
                } else {
                    if (viewModel.activityId != null) {
                        title = R.string.edit_this_message
                        message = getString(R.string.are_you_sure_you_want_to_edit_this_message)
                        positiveText = R.string.edit
                    } else if (viewModel.recipientId != null) {
                        title = R.string.send_this_message
                        message = getString(R.string.are_you_sure_you_want_to_send_this_message, viewModel.recipientName)
                        positiveText = R.string.send
                    }
                }

                val builder = MaterialAlertDialogBuilder(this)

                val text = editorEditText.text?.trim().toString()

                builder.apply {
                    setTitle(title)
                    setMessage(message)
                    setPositiveButton(positiveText) { _, _ -> viewModel.post(text) }
                    if (viewModel.activityId == null && viewModel.recipientId != null) {
                        setNeutralButton(R.string.send_private) { _, _ -> viewModel.post(text, true) }
                    }
                    setNegativeButton(R.string.cancel) { _, _ -> }
                    show()
                }
            }

            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
