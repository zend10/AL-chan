package com.zen.alchan.data.repository

abstract class BaseRepository {

    protected open fun <T> passSavedDataOrThrowable(savedData: T?, throwable: Throwable): T {
        if (savedData != null)
            return savedData
        else
            throw throwable
    }
}