package com.zen.alchan.data.network

import ViewerQuery
import com.zen.alchan.data.response.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ConverterTest {

    companion object {
        private const val USER_ID = 1
        private const val USER_NAME = "Jim Bob"
        private const val USER_ABOUT = "Anime fan."
        private const val USER_BANNER_IMAGE = ""
        private const val USER_UNREAD_NOTIFICATION_COUNT = 0
        private const val USER_DONATOR_TIER = 0
        private const val USER_DONATOR_BADGE = "Donator"
        private const val USER_MODERATOR_STATUS = "Mod"
        private const val USER_SITE_URL = ""
    }

    private val defaultViewer = ViewerQuery.Viewer(
        id = USER_ID,
        name = USER_NAME,
        about = USER_ABOUT,
        avatar = null,
        bannerImage = USER_BANNER_IMAGE,
        options = null,
        mediaListOptions = null,
        statistics = null,
        unreadNotificationCount = USER_UNREAD_NOTIFICATION_COUNT,
        donatorTier = USER_DONATOR_TIER,
        donatorBadge = USER_DONATOR_BADGE,
        moderatorStatus = USER_MODERATOR_STATUS,
        siteUrl = USER_SITE_URL
    )

    private val defaultUser = User(
        id = USER_ID,
        name = USER_NAME,
        about = USER_ABOUT,
        avatar = null,
        bannerImage = USER_BANNER_IMAGE,
        options = null,
        mediaListOptions = null,
        statistics = null,
        unreadNotificationCount = USER_UNREAD_NOTIFICATION_COUNT,
        donatorTier = USER_DONATOR_TIER,
        donatorBadge = USER_DONATOR_BADGE,
        moderatorStatus = USER_MODERATOR_STATUS,
        siteUrl = USER_SITE_URL
    )

    @Test
    internal fun convertUser_isCorrect() {
        val viewer = defaultViewer
        val expectedUser = defaultUser

        val convertedUser = Converter.convertUser(viewer)

        assertEquals(expectedUser, convertedUser)
        println("Converter.convertUser is good.")
    }
}