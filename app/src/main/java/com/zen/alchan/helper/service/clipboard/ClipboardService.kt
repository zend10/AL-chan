package com.zen.alchan.helper.service.clipboard

import io.reactivex.rxjava3.core.Observable


interface ClipboardService {
    fun copyPlainText(text: String): Observable<Unit>
}