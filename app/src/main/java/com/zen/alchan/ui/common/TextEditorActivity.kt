package com.zen.alchan.ui.common

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.Observer
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
        const val EDITOR_TYPE = "editorType"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_editor)

        viewModel.editorType = EditorType.valueOf(intent.getStringExtra(EDITOR_TYPE))

        setSupportActionBar(toolbarLayout)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_delete)
        supportActionBar?.title = getString(R.string.text_editor)

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

        rangeMarkdownLayout.forEachIndexed { index, appCompatImageView ->
            appCompatImageView.setOnClickListener {
                val start = editorEditText.selectionStart
                val end = editorEditText.selectionEnd
                val markdown = rangeMarkdown[index]
                if (start == end) {
                    editorEditText.text?.insert(start, markdown)
                } else {
                    editorEditText.text?.insert(end, markdown.substring(0, markdown.length / 2))
                    editorEditText.text?.insert(start, markdown.substring(markdown.length / 2))
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_post, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.itemPost) {
            if (editorEditText.text?.trim().isNullOrBlank()) {
                DialogUtility.showToast(this, R.string.please_write_something)
                return false
            }

            DialogUtility.showOptionDialog(
                this,
                R.string.post_this_message,
                R.string.are_you_sure_you_want_to_post_this_message,
                R.string.post,
                {
                    viewModel.post(editorEditText.text?.trim().toString())
                },
                R.string.cancel,
                {}
            )

            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
