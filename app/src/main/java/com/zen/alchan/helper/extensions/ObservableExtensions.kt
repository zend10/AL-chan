package com.zen.alchan.helper.extensions

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun <T> Observable<T>.applyScheduler(
    ioScheduler: Scheduler = Schedulers.io(),
    uiScheduler: Scheduler = AndroidSchedulers.mainThread()
): Observable<T> {
    return this.subscribeOn(ioScheduler).observeOn(uiScheduler)
}