package com.zen.alchan.data.datasource

import com.apollographql.apollo.exception.ApolloHttpException
import io.reactivex.Observable

abstract class BaseDataSource {

    protected fun <T> handleApolloError(throwable: Throwable) : Observable<T> {
        return if (throwable is ApolloHttpException) {
            Observable.error(throwable)
        } else {
            Observable.error(throwable)
        }
    }
}