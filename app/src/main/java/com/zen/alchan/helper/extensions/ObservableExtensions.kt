package com.zen.alchan.helper.extensions

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

fun <T: Any> Observable<T>.applyScheduler(
    ioScheduler: Scheduler = Schedulers.io(),
    uiScheduler: Scheduler = AndroidSchedulers.mainThread()
): Observable<T> {
    return this.subscribeOn(ioScheduler).observeOn(uiScheduler)
}

fun Completable.applyScheduler(
    ioScheduler: Scheduler = Schedulers.io(),
    uiScheduler: Scheduler = AndroidSchedulers.mainThread()
): Completable {
    return this.subscribeOn(ioScheduler).observeOn(uiScheduler)
}