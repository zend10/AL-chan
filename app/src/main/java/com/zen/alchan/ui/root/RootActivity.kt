package com.zen.alchan.ui.root

import com.zen.alchan.R
import com.zen.alchan.ui.base.*

class RootActivity : BaseActivity(R.layout.activity_root), ViewContract {

    val navigationManager: NavigationManager = DefaultNavigationManager(supportFragmentManager, R.id.rootLayout)
    val dialogManager: DialogManager = DefaultDialogManager(this)

    override fun setupLayout() {
        navigationManager.navigate(NavigationManager.Page.PAGE_MAIN)
    }

    override fun setupObserver() {

    }
}