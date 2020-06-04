package com.zen.alchan.ui.profile.bio


import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import com.zen.alchan.R
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.libs.*
import com.zen.alchan.ui.browse.user.UserFragment
import io.noties.markwon.*
import io.noties.markwon.core.MarkwonTheme
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.image.gif.GifMediaDecoder
import io.noties.markwon.image.glide.GlideImagesPlugin
import kotlinx.android.synthetic.main.fragment_bio.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.NumberFormatException

class BioFragment : Fragment() {

    private val viewModel by viewModel<BioViewModel>()

//    private val youtubeIdRegex = "(?<=(\\?v=)).+(?=\\))".toRegex()
    private val youtubeRegex = "youtube(?=\\()".toRegex()
    private val imageUrlRegex = "img[0-9]?.+?\\)".toRegex()
    private val spoilerRegex = "(~!|!~)".toRegex()
    private val webmRegex = "webm(?=\\()".toRegex()
    private val centerAlignRegex = "(~{3})[\\s\\S]+?(~{3})".toRegex()
    private val urlRegex = "\\[.+?\\]\\(.+?\\)".toRegex()

    private val rogueUrlRegex = "((?<=\\s)|^)(http|ftp|https):\\/\\/([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:\\/~+#-]*[\\w@?^=%&\\/~+#-])?".toRegex()
    private val rogueUrlTagRegex = "(<a href=.+?>|<\\/a>|<a>)".toRegex()
    private val unicodeRegex = "(&#(x)?)[0-9a-fA-F]+;".toRegex()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bio, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (arguments != null && arguments?.getInt(UserFragment.USER_ID) != null && arguments?.getInt(UserFragment.USER_ID) != 0) {
            viewModel.otherUserId = arguments?.getInt(UserFragment.USER_ID)
        }

        setupObserver()
        initLayout()
    }

    private fun setupObserver() {
        if (viewModel.otherUserId != null) {
            viewModel.userData.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    initLayout()
                }
            })
        } else {
            viewModel.viewerData.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    initLayout()
                }
            })
        }
    }

    private fun initLayout() {
        try {
            val aboutText = if (viewModel.otherUserId != null) {
                viewModel.userData.value?.user?.about
            } else {
                viewModel.viewerData.value?.about
            }

            if (aboutText.isNullOrBlank()) {
                bioTextView.text = getString(R.string.no_description)
                return
            }

            val imageUrls = imageUrlRegex.findAll(aboutText).toList()
//        val youtubeIds = youtubeIdRegex.findAll(aboutText).toList()
            val rogueUrls = rogueUrlRegex.findAll(aboutText).toList()
            val unicodeText = unicodeRegex.findAll(aboutText).toList()

            val metrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
            val width = metrics.widthPixels

            var aboutString = aboutText
                .replace(webmRegex, "[![webm](${Constant.VIDEO_THUMBNAIL_URL})]")
                .replace(youtubeRegex, "[![youtube](${Constant.YOUTUBE_THUMBNAIL_URL})]")
                .replace(imageUrlRegex, "IMAGE_URL")
                .replace(rogueUrlRegex, "ROGUE_URL")
                .replace(unicodeRegex, "UNICODE_TEXT")
                .replace(spoilerRegex, "")
                .replace(rogueUrlTagRegex, "")

            // handle image size
            imageUrls.forEach {
                val imageUrl = it.value.substring(it.value.indexOf("(") + 1, it.value.length - 1)
                if (it.value.startsWith("img(")) {
                    aboutString = aboutString.replaceFirst("IMAGE_URL", "<img src=\"${imageUrl}\">")
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
                    aboutString = aboutString.replaceFirst("IMAGE_URL", "<img src=\"${imageUrl}\" width=\"${imageSize}\">")
                }
            }

            // TODO: create a service to overlay youtube video thumbnail with play button and such
            // handle youtube thumbnail
//        youtubeIds.forEach {
//            aboutString = aboutString?.replaceFirst("YOUTUBE_THUMBNAIL", "http://img.youtube.com/vi/${it.value}/0.jpg")
//        }

            // handle rogue url
            rogueUrls.forEach {
                aboutString = aboutString.replaceFirst("ROGUE_URL", "[${it.value}](${it.value})")
            }

            // handle unicode text
            unicodeText.forEach {
                aboutString = aboutString.replaceFirst("UNICODE_TEXT", "<uni>${it.value}</uni>")
            }

            // handle center align
            val centerAlignText = centerAlignRegex.findAll(aboutString).toList()
            aboutString = aboutString.replace(centerAlignRegex, "CENTER_ALIGN")
            centerAlignText.forEach {
                val alignText = it.value.substring("~~~".length, it.value.lastIndexOf("~~~"))
                aboutString = aboutString.replaceFirst("CENTER_ALIGN", "<align center>${alignText}</align>")
            }

            // handle url
            val urls = urlRegex.findAll(aboutString).toList()
            aboutString = aboutString.replace(urlRegex, "URL_PLACEHOLDER")
            urls.forEach {
                val url = it.value.substring(it.value.lastIndexOf("(") + 1, it.value.length - 1)
                val placeholder = it.value.substring(1, it.value.lastIndexOf("]")).trim().split("\\s+(?=[^\\>]*([\\<]|\$))".toRegex())
                var replacementText = ""
                placeholder.forEach { item ->
                    replacementText += if (item.startsWith("<img src=")) {
                        "\n\n[![image](${item.substring("<img src=\"".length, item.indexOf("\"", "<img src=\"".length))})]($url)"
                    } else {
                        "[$item]($url) "
                    }
                }
                aboutString = aboutString.replaceFirst("URL_PLACEHOLDER", replacementText)
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
                })
                .usePlugin(HtmlPlugin.create())
                .build()

            val node = markwon.parse(aboutString)
            val markdown = markwon.render(node)
            markwon.setParsedMarkdown(bioTextView, markdown)
        } catch (e: Exception) {
            bioTextView.text = getString(R.string.failed_to_render_bio)
        }
    }
}
