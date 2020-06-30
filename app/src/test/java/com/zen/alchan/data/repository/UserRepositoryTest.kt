package com.zen.alchan.data.repository

import SessionQuery
import ViewerQuery
import android.support.v4.media.session.PlaybackStateCompat
import com.apollographql.apollo.api.Error
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.OperationName
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.api.ScalarTypeAdapters
import com.apollographql.apollo.api.internal.ResponseFieldMapper
import com.apollographql.apollo.exception.ApolloHttpException
import com.apollographql.apollo.rx2.Rx2Apollo
import com.zen.alchan.CommonData
import com.zen.alchan.InstantExecutorExtension
import com.zen.alchan.LiveDataTestUtil
import com.zen.alchan.data.datasource.UserDataSource
import com.zen.alchan.data.localstorage.UserManager
import com.zen.alchan.data.network.Resource
import io.reactivex.Observable
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.internal.http2.ErrorCode
import okio.BufferedSource
import okio.ByteString
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito

@ExtendWith(InstantExecutorExtension::class)
class UserRepositoryTest {

    private lateinit var userRepository: UserRepository
    private lateinit var userDataSource: UserDataSource
    private lateinit var userManager: UserManager

    @BeforeEach
    fun initEach() {
        userDataSource = Mockito.mock(UserDataSource::class.java)
        userManager = Mockito.mock(UserManager::class.java)
        userRepository = UserRepositoryImpl(userDataSource, userManager)
    }

    @Test
    fun retrieveViewerData_containsTrue() {
        val liveDataTestUtil = LiveDataTestUtil<Resource<Boolean>>()
        val viewerQuery = ViewerQuery.Data(viewer = CommonData.DEFAULT_VIEWER)

        Mockito
            .`when`(userDataSource.getViewerData())
            .thenReturn(Observable.just(Response.builder<ViewerQuery.Data>(CommonData.APOLLO_RESPONSE).data(viewerQuery).build()))

        userRepository.retrieveViewerData()
        val value = liveDataTestUtil.getValue(userRepository.viewerDataResponse)

        assertEquals(Resource.Success(true), value)
        println("viewerDataResponse LiveData contains the correct value.")
    }
}