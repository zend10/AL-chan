package com.zen.alchan.data.network

import ViewerQuery
import com.zen.alchan.CommonData
import com.zen.alchan.data.response.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ConverterTest {

    @Test
    internal fun convertUser_isCorrect() {
        val viewer = CommonData.DEFAULT_VIEWER
        val expectedUser = CommonData.DEFAULT_USER

        val convertedUser = Converter.convertUser(viewer)

        assertEquals(expectedUser, convertedUser)
        println("Converter.convertUser is good.")
    }
}