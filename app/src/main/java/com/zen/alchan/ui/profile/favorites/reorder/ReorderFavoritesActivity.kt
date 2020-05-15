package com.zen.alchan.ui.profile.favorites.reorder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zen.alchan.R
import com.zen.alchan.ui.base.BaseActivity

class ReorderFavoritesActivity : BaseActivity() {

    companion object {
        const val FAVORITE_DATA = "favoriteData"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reorder_favorites)


    }
}
