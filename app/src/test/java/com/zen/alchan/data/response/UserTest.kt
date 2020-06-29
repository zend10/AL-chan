package com.zen.alchan.data.response

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UserTest {

    @Test
    internal fun isUsersEqual_identicalProperties() {
        val user1 = User(id = 1, name = "Jim Bob")
        val user2 = User(id = 1, name = "Jim Bob")

        assertEquals(user1, user2)
        println("Users are equal.")
    }

    @Test
    internal fun isUsersEqual_differentIds() {
        val user1 = User(id = 1, name = "Jim Bob")
        val user2 = User(id = 2, name = "John Snow")

        assertNotEquals(user1, user2)
        println("Users have different Ids.")
    }
}