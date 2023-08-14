package com.zen.alchan.helper.utils

import android.content.Context
import android.graphics.Color
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.AlignmentSpan
import android.text.style.CharacterStyle
import android.text.style.StrikethroughSpan
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.zen.alchan.helper.Constant
import io.noties.markwon.*
import io.noties.markwon.core.CorePlugin
import io.noties.markwon.core.CoreProps
import io.noties.markwon.core.MarkwonTheme
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.html.HtmlTag
import io.noties.markwon.html.MarkwonHtmlRenderer
import io.noties.markwon.html.TagHandler
import io.noties.markwon.image.DefaultDownScalingMediaDecoder
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.image.gif.GifMediaDecoder
import io.noties.markwon.image.network.OkHttpNetworkSchemeHandler
import org.commonmark.node.Link
import java.net.URLEncoder
import java.util.*
import java.util.regex.Pattern

typealias MarkdownSetup = Markwon

object MarkdownUtil {

    fun getMarkdownSetup(context: Context, maxWidth: Int, linkClickAction: ((String) -> Unit)?): MarkdownSetup {
        return Markwon.builder(context)
            .usePlugin(
                ImagesPlugin.create { plugin ->
                    plugin.defaultMediaDecoder(DefaultDownScalingMediaDecoder.create(maxWidth, maxWidth))
                    plugin.addMediaDecoder(GifMediaDecoder.create())
                    plugin.addSchemeHandler(OkHttpNetworkSchemeHandler.create())
                    plugin.errorHandler { url, throwable ->
                        null
                    }
                }
            )
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(SoftBreakAddsNewLinePlugin.create())
            .usePlugin(AlignCenterPlugin())
            .usePlugin(HtmlPlugin.create())
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureTheme(builder: MarkwonTheme.Builder) {
                    builder.headingBreakHeight(0)
                }

                override fun configure(registry: MarkwonPlugin.Registry) {
                    registry.require(HtmlPlugin::class.java) {
                        it.addHandler(StrikeTagHandler())
                        it.addHandler(AlignmentTagHandler())
                    }
                    registry.require(CorePlugin::class.java) {
                        it.addOnTextAddedListener(MentionTextAddedListener())
                    }
                    registry.require(ImagesPlugin::class.java) {
                        it.addSchemeHandler(OkHttpNetworkSchemeHandler.create())
                    }
                }

                override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
                    linkClickAction?.let {
                        builder.linkResolver { view, link ->
                            linkClickAction(link)
                        }
                    }
                }
            })
            .build()
    }

    fun applyMarkdown(context: Context, markdownSetup: MarkdownSetup?, textView: AppCompatTextView, markdownText: String) {
        try {
            val markdownTextModified = replaceMarkdownText(markdownText)
            val markdownSetup = markdownSetup ?: getMarkdownSetup(context, 0, null)
            val node = markdownSetup.parse(markdownTextModified)
            val markdown = markdownSetup.render(node)
            markdownSetup.setParsedMarkdown(textView, markdown)
            textView.movementMethod = LinkMovementMethod.getInstance()
        } catch (e: Exception) {
            textView.text = "Failed to render"
        }
    }

    fun applyMarkdown(context: Context, maxWidth: Int, textView: AppCompatTextView, markdownText: String) {
        try {
            val markdownTextModified = replaceMarkdownText(markdownText)
            val markdownSetup = getMarkdownSetup(context, maxWidth, null)
            val node = markdownSetup.parse(markdownTextModified)
            val markdown = markdownSetup.render(node)
            markdownSetup.setParsedMarkdown(textView, markdown)
            textView.movementMethod = LinkMovementMethod.getInstance()
        } catch (e: Exception) {
            textView.text = "Failed to render"
        }
    }

    private fun replaceMarkdownText(markdownText: String): String {
        val aniListImageUrlRegex = "[iI]mg([0-9%]+)?\\(.+?\\)".toRegex()
        val rogueUrlRegex = "((?<=\\s)|^)(http|ftp|https):\\/\\/([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:\\/~+#-]*[\\w@?^=%&\\/~+#-])?".toRegex()
        val unicodeRegex = "(&#(x)?)[0-9a-fA-F]+;".toRegex()
        val spoilerRegex = "(~!)[\\s\\S]+?(!~)".toRegex()
        val youtubeRegex = "youtube\\(.+?\\)".toRegex()
        val webmRegex = "webm\\(.+?\\)".toRegex()

        return markdownText
            .replace(spoilerRegex) {
                val spoilerText = it.value.substring(2, it.value.length - 2)
                "[[Spoiler]](alchan://spoiler?data=${URLEncoder.encode(spoilerText, "utf-8")})"
            }
            .replace("~~~", "+++")
            .replace(aniListImageUrlRegex) {
                val value = it.value
                val imageUrl = value.substring(value.indexOf("(") + 1, value.indexOf(")"))
                val size = value.substring("img".length, value.indexOf("("))
                "<img src=\"$imageUrl\" width=\"$size\">"
            }
            .replace(youtubeRegex) {
                val value = it.value
                val youtubeUrl = value.substring(value.indexOf("(") + 1, value.indexOf(")"))
                val isIdOnly = !(youtubeUrl.contains("youtube.com") || youtubeUrl.contains("youtu.be"))
                if (isIdOnly)
                    "[<img alt=\"youtube\" src=\"${Constant.ALCHAN_YOUTUBE_THUMBNAIL_URL}\">](https://www.youtube.com/watch?v=$youtubeUrl)"
                else
                    "[<img alt=\"youtube\" src=\"${Constant.ALCHAN_YOUTUBE_THUMBNAIL_URL}\">]($youtubeUrl)"
            }
            .replace(webmRegex) {
                val value = it.value
                val url = value.substring(value.indexOf("(") + 1, value.indexOf(")"))
                "[<img alt=\"webm\" src=\"${Constant.ALCHAN_VIDEO_THUMBNAIL_URL}\">]($url)"
            }
            .replace(rogueUrlRegex) {
                "<${it.value}>"
            }
            .replace(unicodeRegex) {
                "<uni>${it.value}</uni>"
            }

    }
}

class AlignCenterPlugin : AbstractMarkwonPlugin() {

    private val regex = Pattern.compile("\\+\\+\\+[\\s\\S]+?\\+\\+\\+")

    override fun beforeSetText(textView: TextView, markdown: Spanned) {
        applyAlignCenterSpans(markdown as Spannable)
    }

    private fun applyAlignCenterSpans(spannable: Spannable) {
        val text = spannable.toString()
        val matcher = regex.matcher(text)

        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()
            spannable.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable.setSpan(HideSyntaxSpan(), start, start + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable.setSpan(HideSyntaxSpan(), end - 3, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}

class HideSyntaxSpan : CharacterStyle() {
    override fun updateDrawState(tp: TextPaint) {
        tp.color = Color.TRANSPARENT
    }
}

class StrikeTagHandler : TagHandler() {
    override fun handle(visitor: MarkwonVisitor, renderer: MarkwonHtmlRenderer, tag: HtmlTag) {
        val builder = visitor.builder()
        builder.setSpan(StrikethroughSpan(), tag.start(), tag.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    override fun supportedTags(): MutableCollection<String> {
        return Collections.singleton("strike")
    }
}

class AlignmentTagHandler : TagHandler() {
    override fun handle(visitor: MarkwonVisitor, renderer: MarkwonHtmlRenderer, tag: HtmlTag) {
        val isAlignmentTag = tag.name() == "center" || tag.attributes().containsKey("align")
        if (!isAlignmentTag)
            return

        val builder = visitor.builder()
        val alignmentFromAttribute = if (tag.name() != "center")
            tag.attributes()["align"]
        else
            "center"

        val alignment = when (alignmentFromAttribute) {
            "center" -> Layout.Alignment.ALIGN_CENTER
            "left" -> Layout.Alignment.ALIGN_NORMAL
            "right" -> Layout.Alignment.ALIGN_OPPOSITE
            else -> Layout.Alignment.ALIGN_NORMAL
        }

        builder.setSpan(AlignmentSpan.Standard(alignment), tag.start(), tag.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    override fun supportedTags(): MutableCollection<String> {
        return Collections.checkedList(listOf("center", "p", "div"), String::class.java)
    }
}

class MentionTextAddedListener : CorePlugin.OnTextAddedListener {

    private val mentionRegex = Pattern.compile("\\B@\\w+")

    override fun onTextAdded(visitor: MarkwonVisitor, text: String, start: Int) {
        val matcher = mentionRegex.matcher(text)

        var value: String?
        var url: String?
        var index: Int?

        while (matcher.find()) {
            value = matcher.group(0)
            url = createUserLink(value.substring(1))
            index = start + matcher.start()
            setLink(visitor, url, index, index + value.length)
        }
    }

    private fun createUserLink(user: String) : String {
        return "${Constant.ANILIST_WEBSITE_URL}/user/$user"
    }

    private fun setLink(visitor: MarkwonVisitor, destination: String, start: Int, end: Int) {
        val configuration = visitor.configuration()
        val renderProps = visitor.renderProps()

        CoreProps.LINK_DESTINATION.set(renderProps, destination)

        SpannableBuilder.setSpans(
            visitor.builder(),
            configuration.spansFactory().require(Link::class.java).getSpans(configuration, renderProps),
            start,
            end
        )
    }
}