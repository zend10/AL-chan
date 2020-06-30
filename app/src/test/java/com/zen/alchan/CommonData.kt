package com.zen.alchan

import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.OperationName
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.api.ScalarTypeAdapters
import com.apollographql.apollo.api.internal.ResponseFieldMapper
import com.zen.alchan.data.response.User
import okio.BufferedSource
import okio.ByteString

object CommonData {

    private const val USER_ID = 1
    private const val USER_NAME = "Jim Bob"
    private const val USER_ABOUT = "Anime fan."
    private const val USER_BANNER_IMAGE = ""
    private const val USER_UNREAD_NOTIFICATION_COUNT = 0
    private const val USER_DONATOR_TIER = 0
    private const val USER_DONATOR_BADGE = "Donator"
    private const val USER_MODERATOR_STATUS = "Mod"
    private const val USER_SITE_URL = ""

    val DEFAULT_VIEWER = ViewerQuery.Viewer(
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

    val DEFAULT_USER = User(
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

    val APOLLO_RESPONSE = object : Operation<Operation.Data, String, Operation.Variables> {
        override fun composeRequestBody(): ByteString {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun composeRequestBody(scalarTypeAdapters: ScalarTypeAdapters): ByteString {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun name(): OperationName {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun operationId(): String {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun parse(source: BufferedSource): Response<String> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun parse(
            source: BufferedSource,
            scalarTypeAdapters: ScalarTypeAdapters
        ): Response<String> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun parse(byteString: ByteString): Response<String> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun parse(
            byteString: ByteString,
            scalarTypeAdapters: ScalarTypeAdapters
        ): Response<String> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun queryDocument(): String {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun responseFieldMapper(): ResponseFieldMapper<Operation.Data> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun variables(): Operation.Variables {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun wrapData(data: Operation.Data?): String? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}