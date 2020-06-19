package com.zen.alchan.helper.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import com.zen.alchan.R
import com.zen.alchan.data.network.Resource
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.libs.AlignTagHandler
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.libs.SingleLiveEvent
import com.zen.alchan.helper.pojo.ColorPalette
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonPlugin
import io.noties.markwon.SoftBreakAddsNewLinePlugin
import io.noties.markwon.core.MarkwonTheme
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.image.gif.GifMediaDecoder
import io.noties.markwon.image.glide.GlideImagesPlugin
import io.reactivex.CompletableObserver
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import type.MediaSeason
import type.MediaType
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URLEncoder
import java.util.*
import kotlin.math.max


object AndroidUtility {

    fun getResValueFromRefAttr(context: Context?, attrResId: Int): Int {
        val typedValue = TypedValue()
        context?.theme?.resolveAttribute(attrResId, typedValue, true)
        return typedValue.data
    }

    fun getAppColorThemeRes(appColorTheme: AppColorTheme?): Int {
        return when (appColorTheme ?: Constant.DEFAULT_THEME) {
            AppColorTheme.YELLOW -> R.style.AppTheme_ThemeYellow
            AppColorTheme.GREEN -> R.style.AppTheme_ThemeGreen
            AppColorTheme.BLUE -> R.style.AppTheme_ThemeBlue
            AppColorTheme.PINK -> R.style.AppTheme_ThemePink
            AppColorTheme.RED -> R.style.AppTheme_ThemeRed
        }
    }

    fun getColorPalette(appColorTheme: AppColorTheme?): ColorPalette {
        return when (appColorTheme ?: Constant.DEFAULT_THEME) {
            AppColorTheme.YELLOW -> ColorPalette(R.color.yellow, R.color.cyan, R.color.magentaDark)
            AppColorTheme.GREEN -> ColorPalette(R.color.green, R.color.lavender, R.color.brown)
            AppColorTheme.BLUE -> ColorPalette(R.color.blue, R.color.cream, R.color.gold)
            AppColorTheme.PINK -> ColorPalette(R.color.pink, R.color.sunshine, R.color.jade)
            AppColorTheme.RED -> ColorPalette(R.color.red, R.color.aloevera, R.color.purple)
        }
    }

    fun getSmileyFromScore(score: Double?): Int? {
        return when (score) {
            1.0 -> R.drawable.ic_sad
            2.0 -> R.drawable.ic_neutral
            3.0 -> R.drawable.ic_happy
            else -> R.drawable.ic_puzzled
        }
    }

    fun saveUriToFolder(context: Context?, uri: Uri, mediaType: MediaType, action: () -> Unit) {
        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null

        try {
            val targetFolder = File(context?.getExternalFilesDir(null)?.path)

            if (!targetFolder.exists()) {
                targetFolder.mkdirs()
            }

            val targetFile = File(
                context?.getExternalFilesDir(null),
                if (mediaType == MediaType.ANIME) Constant.ANIME_LIST_BACKGROUND_FILENAME else Constant.MANGA_LIST_BACKGROUND_FILENAME
            )

            if (targetFile.exists()) {
                targetFile.delete()
                targetFile.createNewFile()
            }

            inputStream = context?.contentResolver?.openInputStream(uri)!!
            outputStream = FileOutputStream(targetFile, false)

            val data = ByteArray(1024)
            var bytesRead: Int
            while (inputStream.read(data).also { bytesRead = it } > 0) {
                outputStream.write(data.copyOfRange(0, max(0, bytesRead)))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
            outputStream?.close()

            action()
        }
    }

    fun getImageFileFromFolder(context: Context?, mediaType: MediaType): File {
        return File(
            context?.getExternalFilesDir(null),
            if (mediaType == MediaType.ANIME) Constant.ANIME_LIST_BACKGROUND_FILENAME else Constant.MANGA_LIST_BACKGROUND_FILENAME
        )
    }

    fun copyToClipboard(context: Context?, textToCopy: String) {
        val clipboardManager = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.primaryClip = ClipData.newPlainText(textToCopy, textToCopy)
    }

    fun getGenreHexColor(genre: String): String {
        return when (genre) {
            "Action" -> "#24687B"
            "Adventure" -> "#014037"
            "Comedy" -> "#E6977E"
            "Drama" -> "#7E1416"
            "Ecchi" -> "#7E174A"
            "Fantasy" -> "#989D60"
            "Hentai" -> "#37286B"
            "Horror" -> "#5B1765"
            "Mahou Shoujo" -> "#BF5264"
            "Mecha" -> "#542437"
            "Music" -> "#329669"
            "Mystery" -> "#3D3251"
            "Psychological" -> "#D85C43"
            "Romance" -> "#C02944"
            "Sci-Fi" -> "#85B14B"
            "Slice of Life" -> "#D3B042"
            "Sports" -> "#6B9145"
            "Supernatural" -> "#338074"
            "Thriller" -> "#224C80"
            else -> "#727272"
        }
    }

    fun getCurrentSeason(): MediaSeason {
        val calendar = Calendar.getInstance()
        return when(calendar.get(Calendar.MONTH)) {
            Calendar.DECEMBER, Calendar.JANUARY, Calendar.FEBRUARY -> MediaSeason.WINTER
            in Calendar.MARCH..Calendar.MAY -> MediaSeason.SPRING
            in Calendar.JUNE..Calendar.AUGUST -> MediaSeason.SUMMER
            in Calendar.SEPTEMBER..Calendar.NOVEMBER -> MediaSeason.FALL
            else -> MediaSeason.WINTER
        }
    }

    fun <T> apiCallback(observer: SingleLiveEvent<Resource<T>>) = object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            try {
                observer.postValue(Resource.Success(response.body()!!))
            } catch (e: Exception) {
                observer.postValue(Resource.Error(e.localizedMessage ?: ""))
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            observer.postValue(Resource.Error(t.localizedMessage ?: ""))
        }
    }

    fun <T> rxApolloCallback(observer: SingleLiveEvent<Resource<T>>) = object : Observer<com.apollographql.apollo.api.Response<T>> {
        override fun onSubscribe(d: Disposable) { }

        override fun onNext(t: com.apollographql.apollo.api.Response<T>) {
            if (t.hasErrors()) {
                observer.postValue(Resource.Error(t.errors!![0].message))
            } else {
                observer.postValue(Resource.Success(t.data!!))
            }
        }

        override fun onError(e: Throwable) {
            observer.postValue(Resource.Error(e.localizedMessage))
        }

        override fun onComplete() { }
    }

    fun rxApolloCompletable(observer: SingleLiveEvent<Resource<Boolean>>) = object : CompletableObserver {
        override fun onSubscribe(d: Disposable) { }

        override fun onError(e: Throwable) {
            observer.postValue(Resource.Error(e.localizedMessage))
        }

        override fun onComplete() {
            observer.postValue(Resource.Success(true))
        }
    }

    fun getScreenWidth(activity: Activity?): Int {
        val metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
        return metrics.widthPixels
    }

    fun initMarkwon(context: Context): Markwon {
        return Markwon.builder(context)
            .usePlugin(GlideImagesPlugin.create(GlideApp.with(context)))
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
    }

    fun convertMarkdown(context: Context, textView: AppCompatTextView, aboutText: String?, maxWidth: Int, markwon: Markwon) {
        if (aboutText.isNullOrBlank()) {
            textView.text = context.getString(R.string.no_description)
            return
        }

        // anilist flavor youtube and video
        // val youtubeIdRegex = "(?<=(\\?v=)).+(?=\\))".toRegex()
        val youtubeRegex = "youtube(?=\\()".toRegex()
        val webmRegex = "webm(?=\\()".toRegex()

        // anilist flavor image
        val imageUrlRegex = "img([0-9%]+)?\\(.+?\\)".toRegex()

        // plain url need to be wrapped with change to markdown []() or else it'll be treated like plain text
        val rogueUrlRegex = "((?<=\\s)|^)(http|ftp|https):\\/\\/([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:\\/~+#-]*[\\w@?^=%&\\/~+#-])?".toRegex()

        // unicode need to be wrapped with <uni></uni> or else it'll be treated like plain text
        val unicodeRegex = "(&#(x)?)[0-9a-fA-F]+;".toRegex()

        // anilist flavor spoiler
        val spoilerRegex = "(~!)[\\s\\S]+?(!~)".toRegex()

        // center align need to be changed to <align center></align>
        val centerAlignRegex = "(~{3})[\\s\\S]+?(~{3})".toRegex()
        val centerAlignTagRegex = "<center>[\\s\\S]+?<\\/center>".toRegex()
        val centerAlignPTagRegex = "<p align=\"center\">[\\s\\S]+?<\\/p>".toRegex()
        val centerAlignDivTagRegex = "<div align=\"center\">[\\s\\S]+?<\\/div>".toRegex()

        // right align need to be changed to <align end></align>
        val rightAlignTagRegex = "<p align=\"right\">[\\s\\S]+?<\\/p>".toRegex()
        val rightAlignDivTagRegex = "<div align=\"right\">[\\s\\S]+?<\\/div>".toRegex()

        // hr need to be changed to ***
        val hrRegex = "(<hr>)".toRegex()

        val urlRegex = "\\[.+?\\]\\(.+?\\)".toRegex()

        try {
            // spoilers need to be handle first and foremost and then replace the markdown last
            val spoilerContent = spoilerRegex.findAll(aboutText).toList()
            var aboutString = aboutText.replace(spoilerRegex, "SPOILER_CONTENT")

            // store images, plain urls, unicode into list
            // val youtubeIds = youtubeIdRegex.findAll(aboutText).toList()
            val imageUrls = imageUrlRegex.findAll(aboutText).toList()
            val rogueUrls = rogueUrlRegex.findAll(aboutText).toList()
            val unicodeText = unicodeRegex.findAll(aboutText).toList()

            // replace youtube, webm, image, plain url, unicode all at once because they won't overlap with other markdown
            aboutString = aboutString
                .replace(youtubeRegex, "[![youtube](${Constant.YOUTUBE_THUMBNAIL_URL})]")
                .replace(webmRegex, "[![webm](${Constant.VIDEO_THUMBNAIL_URL})]")
                .replace(imageUrlRegex, "IMAGE_URL")
                .replace(rogueUrlRegex, "ROGUE_URL")
                .replace(unicodeRegex, "UNICODE_TEXT")
                .replace(hrRegex, "---")

            // TODO: create a service to overlay youtube video thumbnail with play button and such
            // handle youtube thumbnail
            // youtubeIds.forEach {
            //     aboutString = aboutString?.replaceFirst("YOUTUBE_THUMBNAIL", "http://img.youtube.com/vi/${it.value}/0.jpg")
            // }

            // handle anilist flavor image
            imageUrls.forEach {
                val imageUrl = it.value.substring(it.value.indexOf("(") + 1, it.value.length - 1)
                if (it.value.startsWith("img(")) {
                    aboutString = aboutString.replaceFirst("IMAGE_URL", "<img src=\"${imageUrl}\">")
                } else {
                    var imageSize = it.value.substring("img".length, it.value.indexOf("("))
                    if (!imageSize.contains("%")) {
                        try {
                            if (imageSize.toInt() > maxWidth - 80) {
                                imageSize = "100%"
                            } else {
                                imageSize += "px"
                            }
                        } catch (e: NumberFormatException) {
                            imageSize = "100%"
                        }
                    }

                    aboutString = aboutString.replaceFirst("IMAGE_URL", "<img src=\"${imageUrl}\" width=\"${imageSize}\">")
                }
            }

            // handle plain url
            rogueUrls.forEach {
                aboutString = aboutString.replaceFirst("ROGUE_URL", "[${it.value}](${it.value})")
            }

            // handle unicode text
            unicodeText.forEach {
                aboutString = aboutString.replaceFirst("UNICODE_TEXT", "<uni>${it.value}</uni>")
            }

            // handle these from below one by one to avoid overlap with other markdown
            // handle center align
            val centerAlignText = centerAlignRegex.findAll(aboutString).toList()
            aboutString = aboutString.replace(centerAlignRegex, "CENTER_ALIGN")
            centerAlignText.forEach {
                val alignText = it.value.substring("~~~".length, it.value.lastIndexOf("~~~"))
                aboutString = aboutString.replaceFirst("CENTER_ALIGN", "<align center>${alignText}</align>")
            }

            val centerAlignTagText = centerAlignTagRegex.findAll(aboutString).toList()
            aboutString = aboutString.replace(centerAlignTagRegex, "CENTER_ALIGN")
            centerAlignTagText.forEach {
                val alignText = it.value.substring("<center>".length, it.value.lastIndexOf("</center>"))
                aboutString = aboutString.replaceFirst("CENTER_ALIGN", "<align center>${alignText}</align>")
            }

            val centerAlignPTagText = centerAlignPTagRegex.findAll(aboutString).toList()
            aboutString = aboutString.replace(centerAlignPTagRegex, "CENTER_ALIGN")
            centerAlignPTagText.forEach {
                val alignText = it.value.substring("<p align=\"center\">".length, it.value.lastIndexOf("</p>"))
                aboutString = aboutString.replaceFirst("CENTER_ALIGN", "<align center>${alignText}</align>")
            }

            val centerAlignDivTagText = centerAlignDivTagRegex.findAll(aboutString).toList()
            aboutString = aboutString.replace(centerAlignDivTagRegex, "CENTER_ALIGN")
            centerAlignDivTagText.forEach {
                val alignText = it.value.substring("<div align=\"center\">".length, it.value.lastIndexOf("</div>"))
                aboutString = aboutString.replaceFirst("CENTER_ALIGN", "<align center>${alignText}</align>")
            }

            // handle right align
            val rightAlignTagText = rightAlignTagRegex.findAll(aboutString).toList()
            aboutString = aboutString.replace(rightAlignTagRegex, "RIGHT_ALIGN")
            rightAlignTagText.forEach {
                val alignText = it.value.substring("<p align=\"right\">".length, it.value.lastIndexOf("</p>"))
                aboutString = aboutString.replaceFirst("RIGHT_ALIGN", "<align end>${alignText}</align>")
            }

            val rightAlignDivTagText = rightAlignDivTagRegex.findAll(aboutString).toList()
            aboutString = aboutString.replace(rightAlignDivTagRegex, "RIGHT_ALIGN")
            rightAlignDivTagText.forEach {
                val alignText = it.value.substring("<div align=\"right\">".length, it.value.lastIndexOf("</div>"))
                aboutString = aboutString.replaceFirst("RIGHT_ALIGN", "<align end>${alignText}</align>")
            }

            // handle url
            val urls = urlRegex.findAll(aboutString).toList()
            aboutString = aboutString.replace(urlRegex, "URL_PLACEHOLDER")
            urls.forEach {
                val url = it.value.substring(it.value.lastIndexOf("(") + 1, it.value.length - 1)
                val placeholder = it.value.substring(1, it.value.lastIndexOf("]"))
                var replacementText = ""
                if (placeholder.contains("<img src")) {
                    val placeHolderList = placeholder.split("\\s+(?=[^\\>]*([\\<]|\$))".toRegex())
                    placeHolderList.forEach { item ->
                        if (item.isBlank()) {
                            replacementText += item
                        } else {
                            replacementText += if (item.startsWith("<img src=")) {
                                "[![image](${item.substring("<img src=\"".length, item.indexOf("\"", "<img src=\"".length))})]($url) "
                            } else {
                                "[$item]($url) "
                            }
                        }
                    }
                } else {
                    replacementText = "[$placeholder]($url)"
                }


                aboutString = aboutString.replaceFirst("URL_PLACEHOLDER", replacementText)
            }

            // handle spoiler last because we don't want it get overlapped by other markdown yet
            spoilerContent.forEach {
                aboutString = aboutString.replaceFirst("SPOILER_CONTENT", "\n[[Spoiler]](alchan://spoiler?data=${URLEncoder.encode(it.value, "utf-8")})")
            }

            val node = markwon.parse(aboutString)
            val markdown = markwon.render(node)
            markwon.setParsedMarkdown(textView, markdown)
        } catch (e: Exception) {
            textView.text = context.getString(R.string.failed_to_render)
        }
    }
}