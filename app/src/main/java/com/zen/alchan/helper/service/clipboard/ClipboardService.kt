package com.zen.alchan.helper.service.clipboard

import io.reactivex.Observable

interface ClipboardService {
    fun copyPlainText(text: String): Observable<Unit>
}