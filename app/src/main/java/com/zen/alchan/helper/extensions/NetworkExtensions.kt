package com.zen.alchan.helper.extensions

import com.apollographql.apollo.exception.ApolloHttpException

fun Throwable.sendMessage(): Int {
    return if (this is ApolloHttpException) {
        when (this.code()) {
            400 -> 0
            401 -> 1
            529 -> 2
            else -> 3
        }
    } else {
        4
    }
}