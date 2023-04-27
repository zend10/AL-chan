package com.zen.alchan.helper.extensions

import com.apollographql.apollo.exception.ApolloHttpException
import com.zen.alchan.R
import com.zen.alchan.helper.utils.NotInStorageException

fun Throwable.getStringResource(): Int {
    return when(this) {
        is ApolloHttpException -> {
            when (this.code()) {
                400 -> R.string.your_session_has_ended
                401 -> R.string.your_session_has_ended
                529 -> R.string.something_went_wrong_please_try_again_in_few_minutes
                404 -> R.string.this_page_does_not_exist_its_either_set_to_private_or_removed
                else -> R.string.something_went_wrong_please_try_again
            }
        }
        is NotInStorageException -> R.string.failed_to_load_from_cache
        else -> R.string.something_went_wrong_please_try_again
    }
}