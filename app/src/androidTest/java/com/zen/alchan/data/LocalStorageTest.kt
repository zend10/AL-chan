package com.zen.alchan.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.gson.Gson
import com.zen.alchan.data.localstorage.LocalStorage
import com.zen.alchan.data.localstorage.LocalStorageImpl
import com.zen.alchan.data.response.User
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class LocalStorageTest {

    private lateinit var appContext: Context
    private lateinit var gson: Gson
    private lateinit var localStorage: LocalStorage

    @Before
    fun init() {
        appContext = ApplicationProvider.getApplicationContext()
        gson = Gson()
        localStorage = LocalStorageImpl(appContext, appContext.packageName + ".Test", gson)
    }

    @Test
    fun bearerToken_checkSavedBearerToken() {
        val token = "Bearer abcdef"

        localStorage.bearerToken = token
        Assert.assertEquals(token, localStorage.bearerToken)

        localStorage.bearerToken = null
        Assert.assertNull(localStorage.bearerToken)
    }

    @Test
    fun viewerData_checkSavedViewerData() {
        val user = User(id = 1, name = "Jim Bob")

        localStorage.viewerData = user
        Assert.assertEquals(user, localStorage.viewerData)

        localStorage.viewerData = null
        Assert.assertNull(localStorage.viewerData)
    }
}