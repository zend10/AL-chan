package com.zen.alchan.data.localstorage

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.zen.alchan.helper.pojo.NullableItem
import io.reactivex.Observable
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import kotlin.math.max

abstract class BaseFileStorageHandler(
    private val context: Context
) {
    protected fun saveUri(uri: Uri?, fileName: String): Observable<Unit> {
        return Observable.create {
            if (uri == null) {
                it.onNext(Unit)
                it.onComplete()
                return@create
            }

            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null

            try {
                val targetFolder = File(context.getExternalFilesDir(null)?.path)

                if (!targetFolder.exists()) {
                    targetFolder.mkdirs()
                }

                val targetFile = File(context.getExternalFilesDir(null), fileName)

                if (targetFile.exists()) {
                    targetFile.delete()
                    targetFile.createNewFile()
                }

                inputStream = context.contentResolver.openInputStream(uri)!!
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

                it.onNext(Unit)
                it.onComplete()
            }
        }
    }

    protected fun getUri(fileName: String): Observable<NullableItem<Uri>> {
        return Observable.just(NullableItem(File(context.getExternalFilesDir(null), fileName).toUri()))
    }
}