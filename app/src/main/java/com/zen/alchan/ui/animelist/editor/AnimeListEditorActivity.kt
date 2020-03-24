package com.zen.alchan.ui.animelist.editor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zen.alchan.R
import com.zen.alchan.ui.base.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class AnimeListEditorActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anime_list_editor)
    }
}
