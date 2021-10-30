package com.zen.alchan.helper.extensions

import com.apollographql.apollo.exception.ApolloHttpException
import com.zen.alchan.R
import com.zen.alchan.helper.utils.NotInStorageException

fun Throwable.getStringResource(): Int {
    return when(this) {
        is ApolloHttpException -> {
            when (this.code()) {
                400 -> R.string.something_went_wrong_please_try_again
                401 -> R.string.something_went_wrong_please_try_again
                529 -> R.string.something_went_wrong_please_try_again
                404 -> R.string.something_went_wrong_please_try_again
                else -> R.string.something_went_wrong_please_try_again
            }
        }
        is NotInStorageException -> R.string.failed_to_load_from_cache
        else -> R.string.something_went_wrong_please_try_again
    }
}