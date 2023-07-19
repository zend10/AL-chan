package com.zen.alchan.helper.pojo

import com.zen.alchan.helper.utils.TimeUtil

class SaveItem<T>(
    val data: T,
    var saveTime: Long = TimeUtil.getCurrentTimeInMillis()
) {
}