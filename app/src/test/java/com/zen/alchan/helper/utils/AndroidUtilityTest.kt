package com.zen.alchan.helper.utils

import com.zen.alchan.R
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class AndroidUtilityTest {

    @Test
    fun getSmileyFromScore_returnCorrectSmiley() {
        assertEquals(R.drawable.ic_sad, AndroidUtility.getSmileyFromScore(1.0))
        println("Score 1.0 returns sad icon.")

        assertEquals(R.drawable.ic_neutral, AndroidUtility.getSmileyFromScore(2.0))
        println("Score 2.0 returns neutral icon.")

        assertEquals(R.drawable.ic_happy, AndroidUtility.getSmileyFromScore(3.0))
        println("Score 3.0 returns happy icon.")

        assertEquals(R.drawable.ic_puzzled, AndroidUtility.getSmileyFromScore(null))
        println("Score null returns puzzled icon.")
    }
}