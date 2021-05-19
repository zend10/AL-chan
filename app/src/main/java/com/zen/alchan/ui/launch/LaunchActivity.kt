package com.zen.alchan.ui.launch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zen.alchan.R
import com.zen.alchan.ui.base.BaseActivity
import com.zen.alchan.ui.root.RootActivity

class LaunchActivity : BaseActivity(R.layout.activity_launch) {

    override fun setUpLayout() {
        startActivity(Intent(this, RootActivity::class.java))
        overridePendingTransition(0, 0)
        finish()
    }

    override fun setUpObserver() {
        // do nothing
    }
}