package com.zen.alchan.ui.animelist.editor

import android.os.Bundle
import com.zen.alchan.R
import com.zen.alchan.ui.base.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class AnimeListEditorActivity : BaseActivity() {

    private val viewModel by viewModel<AnimeListEditorViewModel>()

    companion object {
        const val INTENT_ENTRY_ID = "entryId"
        const val INTENT_SELECTED_STATUS = "selectedStatus"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anime_list_editor)
    }
}
