package com.zen.alchan.ui.common

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zen.alchan.R
import com.zen.alchan.helper.enums.EditorType
import com.zen.alchan.ui.base.BaseActivity
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TextEditorActivity : BaseActivity() {

    private val viewModel by viewModel<TextEditorViewModel>()

    companion object {
        const val EDITOR_TYPE = "editorType"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_editor)

        viewModel.editorType = EditorType.valueOf(intent.getStringExtra(EDITOR_TYPE))

        setSupportActionBar(toolbarLayout)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_delete)
        supportActionBar?.title = getString(R.string.text_editor)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
