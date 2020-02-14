package com.zen.alchan.data.network

import com.zen.alchan.helper.enums.ResponseStatus

sealed class Resource<T>(
    val responseStatus: ResponseStatus,
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(ResponseStatus.SUCCESS, data)
    class Loading<T>(data: T? = null) : Resource<T>(ResponseStatus.LOADING, data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(ResponseStatus.ERROR, data, message)
}

