package com.zen.alchan.helper

import kotlin.time.Clock

object TimeUtils {
    fun getCurrentMillis(): Long {
        return Clock.System.now().toEpochMilliseconds()
    }
}