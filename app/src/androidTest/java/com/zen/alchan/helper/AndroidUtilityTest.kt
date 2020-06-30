package com.zen.alchan.helper

import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.common.internal.Asserts
import com.zen.alchan.helper.libs.AlignTagHandler
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.utils.AndroidUtility
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
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import type.MediaType
import java.io.File

class AndroidUtilityTest {

    companion object {
        private const val IMAGE_NAME = "test_image.jpg"
    }

    lateinit var appContext: Context

    @Before
    fun init() {
        appContext = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun saveUriToFolder_confirmImageIsSaved() {
        val imageFile = File(appContext.getExternalFilesDir(null)?.path, IMAGE_NAME)
        val uri = Uri.fromFile(imageFile)

        val file = File(appContext.getExternalFilesDir(null), Constant.ANIME_LIST_BACKGROUND_FILENAME)
        if (file.exists()) {
            file.delete()
        }

        AndroidUtility.saveUriToFolder(appContext, uri, MediaType.ANIME) {
            // do nothing
        }

        val newFile = File(appContext.getExternalFilesDir(null), Constant.ANIME_LIST_BACKGROUND_FILENAME)
        Assert.assertNotEquals(null, newFile)
    }

    @Test
    fun getImageFileFromFolder_checkFileRetrieved() {
        val file = AndroidUtility.getImageFileFromFolder(appContext, MediaType.ANIME)
        val expectedFile = File(appContext.getExternalFilesDir(null)?.path, Constant.ANIME_LIST_BACKGROUND_FILENAME)

        Assert.assertEquals(expectedFile, file)
    }

    @Test
    fun copyToClipboard_checkCopiedText() {
        val dummyText = "Hello, World!"
        AndroidUtility.copyToClipboard(appContext, dummyText)
        val clipboard = appContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        Assert.assertEquals(dummyText, clipboard.primaryClip?.getItemAt(0)?.text.toString())
    }
}