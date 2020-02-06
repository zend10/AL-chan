package com.zen.alchan.ui

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zen.alchan.R
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(getSavedTheme())
        setContentView(R.layout.activity_main)

        yellowButton.setOnClickListener {
            saveTheme("yellow")
            recreate()
        }

        cyanButton.setOnClickListener {
            saveTheme("cyan")
            recreate()
        }
    }

    private fun saveTheme(value: String) {
        val editor = getPreferences(Activity.MODE_PRIVATE).edit()
        editor.putString("theme", value)
        editor.apply()
        recreate()
    }

    private fun getSavedTheme(): Int {
        val theme = getPreferences(Activity.MODE_PRIVATE).getString("theme", "yellow")
        return when (theme) {
            "yellow" -> R.style.AppTheme_ThemeYellow
            "cyan" -> R.style.AppTheme_ThemeCyan
            else -> R.style.AppTheme_ThemeYellow
        }
    }
}
