package com.zen.alchan.helper.utils

import android.content.Context
import android.text.Layout
import android.text.method.LinkMovementMethod
import android.text.style.AlignmentSpan
import androidx.appcompat.widget.AppCompatTextView
import com.zen.alchan.helper.Constant
import io.noties.markwon.*
import io.noties.markwon.core.CorePlugin
import io.noties.markwon.core.CoreProps
import io.noties.markwon.core.MarkwonTheme
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.html.HtmlTag
import io.noties.markwon.html.tag.SimpleTagHandler
import io.noties.markwon.image.DefaultDownScalingMediaDecoder
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.image.gif.GifMediaDecoder
import org.commonmark.node.Link
import java.util.*
import java.util.regex.Pattern

typealias MarkdownSetup = Markwon

object MarkdownUtil {

    fun getMarkdownSetup(context: Context, maxWidth: Int): MarkdownSetup {
        return Markwon.builder(context)
            .usePlugin(
                ImagesPlugin.create { plugin ->
                    plugin.defaultMediaDecoder(DefaultDownScalingMediaDecoder.create(maxWidth, 0))
                    plugin.addMediaDecoder(GifMediaDecoder.create())
                }
            )
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(SoftBreakAddsNewLinePlugin.create())
            .usePlugin(HtmlPlugin.create())
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureTheme(builder: MarkwonTheme.Builder) {
                    builder.headingBreakHeight(0)
                }

                override fun configure(registry: MarkwonPlugin.Registry) {
                    registry.require(HtmlPlugin::class.java) {
                        it.addHandler(AlignTagHandler())
                    }
                    registry.require(CorePlugin::class.java) {
                        it.addOnTextAddedListener(MentionTextAddedListener())
                    }
                }
            })
            .build()
    }

    fun applyMarkdown(context: Context, markdownSetup: MarkdownSetup?, textView: AppCompatTextView, markdownText: String) {
        try {
            val markdownSetup = markdownSetup ?: getMarkdownSetup(context, 0)
            val node = markdownSetup.parse(markdownText)
            val markdown = markdownSetup.render(node)
            markdownSetup.setParsedMarkdown(textView, markdown)
            textView.movementMethod = LinkMovementMethod.getInstance()
        } catch (e: Exception) {
            textView.text = "Failed to render"
        }
    }

    fun applyMarkdown(context: Context, textView: AppCompatTextView, markdownText: String) {
        try {
            val markdownSetup = getMarkdownSetup(context, 0)
            val node = markdownSetup.parse(markdownText)
            val markdown = markdownSetup.render(node)
            markdownSetup.setParsedMarkdown(textView, markdown)
            textView.movementMethod = LinkMovementMethod.getInstance()
        } catch (e: Exception) {
            textView.text = "Failed to render"
        }
    }
}

class AlignTagHandler : SimpleTagHandler() {

    override fun getSpans(
        configuration: MarkwonConfiguration,
        renderProps: RenderProps,
        tag: HtmlTag
    ): Any? {
        val alignment = if (tag.attributes().containsKey("center")) {
            Layout.Alignment.ALIGN_CENTER
        } else if (tag.attributes().containsKey("end")) {
            Layout.Alignment.ALIGN_OPPOSITE
        } else {
            Layout.Alignment.ALIGN_NORMAL
        }

        return AlignmentSpan.Standard(alignment)
    }

    override fun supportedTags(): MutableCollection<String> {
        return Collections.singleton("align")
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