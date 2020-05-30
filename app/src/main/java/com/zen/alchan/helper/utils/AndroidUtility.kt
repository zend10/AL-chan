package com.zen.alchan.helper.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.util.TypedValue
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.text.HtmlCompat
import com.zen.alchan.R
import com.zen.alchan.data.network.Resource
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.libs.SingleLiveEvent
import com.zen.alchan.helper.pojo.ColorPalette
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import type.MediaSeason
import type.MediaType
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
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
}