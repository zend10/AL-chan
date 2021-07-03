package com.zen.alchan.helper.extensions

import com.apollographql.apollo.exception.ApolloHttpException
import com.zen.alchan.helper.utils.NotInStorageException

fun Throwable.getStringResource(): Int {
    return when(this) {
        is ApolloHttpException -> {
            when (this.code()) {
                400 -> 0
                401 -> 1
                529 -> 2
                404 -> 3
                else -> 4
            }
        }
        is NotInStorageException -> 5
        else -> 6
    }
}