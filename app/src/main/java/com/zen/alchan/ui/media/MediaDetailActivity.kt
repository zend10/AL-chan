package com.zen.alchan.ui.media

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zen.alchan.R

class MediaDetailActivity : AppCompatActivity() {

    companion object {
        const val MEDIA_ID = "mediaId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_detail)
    }
}
