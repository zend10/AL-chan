package com.zen.alchan.helper.extensions

import com.apollographql.apollo.exception.ApolloHttpException
import com.zen.alchan.helper.utils.StorageException

fun Throwable.sendMessage(): Int {
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
        is StorageException -> 5
        else -> 6
    }
}