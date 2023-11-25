package com.zen.alchan.ui.texteditor

import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageView
import com.zen.alchan.R
import com.zen.alchan.databinding.FragmentTextEditorBinding
import com.zen.alchan.helper.enums.TextEditorType
import com.zen.alchan.helper.extensions.applyBottomSidePaddingInsets
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.pojo.TextInputSetting
import com.zen.alchan.helper.utils.DeepLink
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class TextEditorFragment : BaseFragment<FragmentTextEditorBinding, TextEditorViewModel>() {

    override val viewModel: TextEditorViewModel by viewModel()

    private lateinit var rangeMarkdowns: List<Pair<AppCompatImageView, String>>
    private lateinit var startOnlyMarkdowns: List<Pair<AppCompatImageView, String>>
    private lateinit var dialogMarkdowns: List<Triple<AppCompatImageView, String, Int>>

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTextEditorBinding {
        return FragmentTextEditorBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        with(binding) {
            rangeMarkdowns = listOf(
                textEditorFormatBold to "____",
                textEditorFormatItalic to "__",
                textEditorFormatStrikeThrough to "~~~~",
                textEditorFormatSpoiler to "~!!~",
                textEditorFormatCenter to "~~~~~~",
                textEditorFormatCode to "``"
            )

            startOnlyMarkdowns = listOf(
                textEditorFormatOrderedList to "1. ",
                textEditorFormatUnorderedList to "- ",
                textEditorFormatHeader to "# ",
                textEditorFormatQuote to "> "
            )

            dialogMarkdowns = listOf(
                Triple(textEditorFormatLink, "[link]", R.string.type_the_link_here),
                Triple(textEditorFormatImage, "img220", R.string.type_the_image_link_here),
                Triple(textEditorFormatYoutube, "youtube", R.string.type_the_youtube_video_link_here),
                Triple(textEditorFormatVideo, "webm", R.string.type_the_video_link_here)
            )

            textEditorClose.clicks {
                textEditorEditText.clearFocus()
                goBack()
            }

            textEditorPostButton.clicks {
                textEditorEditText.clearFocus()
                viewModel.loadPostDialogDetail(textEditorEditText.text.toString())
            }

            textEditorPreviewButton.clicks {
                textEditorEditText.clearFocus()
                dialog.showSpoilerDialog(textEditorEditText.text.toString()) {
                    handleSpoilerOnLinkClick(it)
                }
            }

            rangeMarkdowns.forEach { (icon, markdown) ->
                icon.clicks {
                    val start = textEditorEditText.selectionStart
                    val end = textEditorEditText.selectionEnd
                    if (start == end)
                        textEditorEditText.text?.insert(start, markdown)
                    else {
                        textEditorEditText.text?.insert(end, markdown.substring(markdown.length / 2))
                        textEditorEditText.text?.insert(start, markdown.substring(0, markdown.length / 2))
                    }
                }
            }

            startOnlyMarkdowns.forEach { (icon, markdown) ->
                icon.clicks {
                    val start = textEditorEditText.selectionStart
                    textEditorEditText.text?.insert(start, markdown)
                }
            }

            dialogMarkdowns.forEach { (icon, markdown, hint) ->
                icon.clicks {
                    dialog.showTextInputDialog("", TextInputSetting(InputType.TYPE_TEXT_VARIATION_URI, false, 200, hint)) {
                        if (it.isNotBlank()) {
                            val start = textEditorEditText.selectionStart
                            textEditorEditText.text?.insert(start, "$markdown($it)")
                        }
                    }
                }
            }

            textEditorEditText.setOnFocusChangeListener { view, hasFocus ->
                toggleKeyboard(hasFocus)
            }

            textEditorEditText.requestFocus()
        }
    }

    override fun setUpInsets() {
        binding.textEditorFormatLayout.applyBottomSidePaddingInsets()
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.loading.subscribe {
                binding.loadingLayout.loadingLayout.show(it)
            },
            viewModel.success.subscribe {
                dialog.showToast(it)
                goBack()
            },
            viewModel.error.subscribe {
                dialog.showToast(it)
            },
            viewModel.postDialog.subscribe {
                dialog.showConfirmationDialog(
                    it.first.first,
                    it.first.second,
                    it.second.first,
                    {
                        viewModel.postActivity(binding.textEditorEditText.text.toString(), false)
                    },
                    it.second.second,
                    {},
                    it.second.third,
                    {
                        viewModel.postActivity(binding.textEditorEditText.text.toString(), true)
                    }
                )
            },
            viewModel.mentionUser.subscribe {
                binding.textEditorEditText.text?.insert(0, "$it ")
            }
        )

        arguments?.let {
            val activityId = it.getInt(ACTIVITY_ID, 0)
            val activityReplyId = it.getInt(ACTIVITY_REPLY_ID, 0)
            val recipientId = it.getInt(RECIPIENT_ID, 0)
            viewModel.loadData(
                TextEditorParam(
                    TextEditorType.valueOf(it.getString(TEXT_EDITOR_TYPE) ?: TextEditorType.TEXT_ACTIVITY.name),
                    if (activityId == 0) null else activityId,
                    if (activityReplyId == 0) null else activityReplyId,
                    if (recipientId == 0) null else recipientId,
                    it.getString(USERNAME)
                )
            )
        }
    }

    private fun handleSpoilerOnLinkClick(link: String) {
        try {
            val deepLink = DeepLink(Uri.parse(link))
            if (deepLink.isSpoiler()) {
                dialog.showSpoilerDialog(deepLink.getQueryParamOfOrNull(DeepLink.QUERY_PARAM_DATA) ?: "") {
                    handleSpoilerOnLinkClick(it)
                }
            } else
                navigation.openWebView(link)
        } catch (e: Exception) {
            // do nothing
        }
    }

    companion object {
        private const val ACTIVITY_ID = "activityId"
        private const val ACTIVITY_REPLY_ID = "activityReplyId"
        private const val RECIPIENT_ID = "recipientId"
        private const val USERNAME = "username"
        private const val TEXT_EDITOR_TYPE = "textEditorType"
        @JvmStatic
        fun newInstance(textEditorType: TextEditorType, activityId: Int?, activityReplyId: Int?, recipientId: Int?, username: String?) =
            TextEditorFragment().apply {
                arguments = Bundle().apply {
                    putString(TEXT_EDITOR_TYPE, textEditorType.name)
                    activityId?.let { putInt(ACTIVITY_ID, activityId) }
                    activityReplyId?.let { putInt(ACTIVITY_REPLY_ID, activityReplyId) }
                    recipientId?.let { putInt(RECIPIENT_ID, recipientId) }
                    username?.let { putString(USERNAME, username) }
                }
            }
    }
}