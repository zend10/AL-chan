package com.zen.alchan.helper.libs

import com.zen.alchan.BuildConfig
import com.zen.alchan.helper.Constant
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.SpannableBuilder
import io.noties.markwon.core.CorePlugin
import io.noties.markwon.core.CoreProps
import org.commonmark.node.Link
import java.util.regex.Pattern

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
        return "${Constant.ANILIST_URL}user/$user"
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