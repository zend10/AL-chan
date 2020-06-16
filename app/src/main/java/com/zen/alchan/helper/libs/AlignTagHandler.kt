package com.zen.alchan.helper.libs

import android.content.res.ColorStateList
import android.text.Layout
import android.text.style.AlignmentSpan
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.RenderProps
import io.noties.markwon.html.HtmlTag
import io.noties.markwon.html.tag.SimpleTagHandler
import java.util.*

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