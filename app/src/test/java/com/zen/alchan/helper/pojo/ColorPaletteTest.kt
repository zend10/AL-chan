package com.zen.alchan.helper.pojo

import com.zen.alchan.R
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class ColorPaletteTest {

    @Test
    internal fun isColorPalettesEqual() {
        val colorPalette1 = ColorPalette(R.color.yellow, R.color.cyan, R.color.magentaDark)
        val colorPalette2 = ColorPalette(R.color.yellow, R.color.cyan, R.color.magentaDark)

        assertEquals(colorPalette1, colorPalette2)
        println("Color palettes with similar color are equal.")

        val colorPalette3 = ColorPalette(R.color.green, R.color.cyan, R.color.magentaDark)
        val colorPalette4 = ColorPalette(R.color.yellow, R.color.cyan, R.color.magentaDark)

        assertNotEquals(colorPalette3, colorPalette4)
        println("Color palettes with at least one different color are different.")
    }
}