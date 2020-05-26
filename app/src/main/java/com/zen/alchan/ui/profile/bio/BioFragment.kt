package com.zen.alchan.ui.profile.bio


import android.os.Bundle
import android.text.*
import android.text.style.ClickableSpan
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.text.toSpanned
import androidx.lifecycle.Observer

import com.zen.alchan.R
import com.zen.alchan.helper.libs.*
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import io.noties.markwon.*
import io.noties.markwon.core.CorePlugin
import io.noties.markwon.core.MarkwonTheme
import io.noties.markwon.core.spans.BlockQuoteSpan
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.AsyncDrawableScheduler
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.image.gif.GifMediaDecoder
import io.noties.markwon.image.glide.GlideImagesPlugin
import io.noties.markwon.inlineparser.MarkwonInlineParser
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin
import io.noties.markwon.inlineparser.NewLineInlineProcessor
import kotlinx.android.synthetic.main.fragment_bio.*
import org.commonmark.node.Text
import org.commonmark.parser.Parser
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.NumberFormatException

/**
 * A simple [Fragment] subclass.
 */
class BioFragment : Fragment() {

    private val viewModel by viewModel<BioViewModel>()

    private val youtubeIdRegex = "(?<=(\\?v=)).+(?=\\))".toRegex()
    private val youtubeRegex = "youtube(?=\\()".toRegex()
    private val imageUrlRegex = "img[0-9]?.+?\\)".toRegex()
    private val spoilerRegex = "(~!|!~)".toRegex()
    private val webmRegex = "webm(?=\\()".toRegex()
    private val centerAlignRegex = "(~{3})[\\s\\S]+?(~{3})".toRegex()
    private val rogueUrl = "(<a href=.+?>|<\\/a>)".toRegex()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bio, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupObserver()
        initLayout()
    }

    private fun setupObserver() {
        viewModel.viewerData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                initLayout()
            }
        })
    }

    private fun initLayout() {
        if (viewModel.viewerData.value?.about.isNullOrBlank()) {
            bioTextView.text = getString(R.string.no_description)
            return
        }

        val aboutText = viewModel.viewerData.value?.about ?: getString(R.string.no_description)
        val imageUrls = imageUrlRegex.findAll(aboutText).toList()
        val youtubeIds = youtubeIdRegex.findAll(aboutText).toList()

        val metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
        val width = metrics.widthPixels

        // TODO: use default thumbnail for youtube and webm

        var aboutString = viewModel.viewerData.value?.about
            ?.replace(imageUrlRegex, "IMAGE_URL")
            ?.replace(webmRegex, "[![webm](https://toppng.com/uploads/preview/lay-icon-play-icon-11563266312mklxafh8gy.png)]")
            ?.replace(youtubeRegex, "[![youtube](YOUTUBE_THUMBNAIL)]")
            ?.replace(spoilerRegex, "")
            ?.replace(rogueUrl, "")

        // handle image size
        imageUrls.forEach {
            val imageUrl = it.value.substring(it.value.indexOf("(") + 1, it.value.length - 1)
            if (it.value.startsWith("img(")) {
                aboutString = aboutString?.replaceFirst("IMAGE_URL", "<img src=\"${imageUrl}\">")
            } else {
                var imageSize = it.value.substring("img".length, it.value.indexOf("("))
                try {
                    if (imageSize.toInt() > width - 80) {
                        imageSize = "100%"
                    } else {
                        imageSize += "px"
                    }
                } catch (e: NumberFormatException) {
                    imageSize = "100%"
                }
                aboutString = aboutString?.replaceFirst("IMAGE_URL", "<img src=\"${imageUrl}\" width=\"${imageSize}\">")
            }
        }

        // handle youtube thumbnail
        youtubeIds.forEach {
            aboutString = aboutString?.replaceFirst("YOUTUBE_THUMBNAIL", "http://img.youtube.com/vi/${it.value}/0.jpg")
        }

        // handle center align
        val centerAlignText = centerAlignRegex.findAll(aboutString!!).toList()
        aboutString = aboutString?.replace(centerAlignRegex, "CENTER_ALIGN")
        centerAlignText.forEach {
            val alignText = it.value.substring("~~~".length, it.value.lastIndexOf("~~~"))
            aboutString = aboutString?.replaceFirst("CENTER_ALIGN", "<align center>${alignText}</align>")
        }

        val markwon = Markwon.builder(activity!!)
            .usePlugin(GlideImagesPlugin.create(GlideApp.with(activity!!)))
            .usePlugin(ImagesPlugin.create(ImagesPlugin.ImagesConfigure {
                it.addMediaDecoder(GifMediaDecoder.create(true))
            }))
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(SoftBreakAddsNewLinePlugin.create())
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureTheme(builder: MarkwonTheme.Builder) {
                    builder.headingBreakHeight(0)
                }
                override fun configure(registry: MarkwonPlugin.Registry) {
                    registry.require(HtmlPlugin::class.java) {
                        it.addHandler(AlignTagHandler())
                    }
                }

                override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
                    builder.setFactory(Text::class.java) { configuration, props ->  

                    }
                }
            })
            .usePlugin(HtmlPlugin.create())
            .build()

        val node = markwon.parse(aboutString!!)
        val markdown = markwon.render(node)
        markwon.setParsedMarkdown(bioTextView, markdown)
    }
}
