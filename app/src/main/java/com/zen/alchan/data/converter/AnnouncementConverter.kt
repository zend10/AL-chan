package com.zen.alchan.data.converter

import com.zen.alchan.data.response.Announcement
import com.zen.alchan.data.response.github.AnnouncementResponse

fun AnnouncementResponse.convert(): Announcement {
    return Announcement(
        id = id ?: "",
        fromDate = fromDate ?: "",
        untilDate = untilDate?: "",
        message = message ?: "",
        appVersion = appVersion?.toIntOrNull() ?: 0,
        requiredUpdate = requiredUpdate == "1"
    )
}