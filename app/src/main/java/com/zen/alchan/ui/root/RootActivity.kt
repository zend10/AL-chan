package com.zen.alchan.ui.root

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.zen.alchan.R
import com.zen.alchan.ui.base.*
import com.zen.alchan.ui.main.MainFragment
import kotlinx.android.synthetic.main.activity_root.*

class RootActivity : BaseActivity(R.layout.activity_root), ViewContract {

    val navigationManager: NavigationManager = NavigationManagerImpl(supportFragmentManager, R.id.rootLayout)
    val dialogManager: DialogManager = DialogManagerImpl(this)

    override fun setupObserver() {

    }

    override fun setupLayout() {
        navigationManager.navigate(NavigationManager.Page.PAGE_MAIN)
    }
}