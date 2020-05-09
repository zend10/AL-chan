package com.zen.alchan.ui.explore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zen.alchan.R
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_explore.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ExploreActivity : BaseActivity() {

    private val viewModel by viewModel<ExploreViewModel>()

    companion object {
        const val EXPLORE_PAGE = "explorePage"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore)

        if (viewModel.selectedExplorePage == null) {
            viewModel.selectedExplorePage = BrowsePage.valueOf(intent.getStringExtra(EXPLORE_PAGE))
        }

        initLayout()
        setupObserver()
    }

    private fun setupObserver() {

    }

    private fun initLayout() {
        exploreTypeText.text = viewModel.selectedExplorePage?.name

        exploreTypeText.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setItems(viewModel.explorePageArray) { _, which ->
                    viewModel.selectedExplorePage = BrowsePage.valueOf(viewModel.explorePageArray[which])
                    viewModel.search()
                }
                .show()
        }


    }
}
