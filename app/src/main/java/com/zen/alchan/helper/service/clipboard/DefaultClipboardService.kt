package com.zen.alchan.helper.service.clipboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.zen.BuildConfig
import io.reactivex.rxjava3.core.Observable

class DefaultClipboardService(private val context: Context) : ClipboardService {

    override fun copyPlainText(text: String): Observable<Unit> {
        return Observable.create {
            try {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(BuildConfig.APPLICATION_ID, text)
                clipboard.setPrimaryClip(clip)
                it.onNext(Unit)
                it.onComplete()
            } catch (e: Exception) {
                it.onError(e)
            }
        }
    }
}