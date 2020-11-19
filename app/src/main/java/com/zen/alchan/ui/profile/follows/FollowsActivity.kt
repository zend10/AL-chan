package com.zen.alchan.ui.profile.follows

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zen.alchan.R
import com.zen.alchan.helper.changeStatusBarColor
import com.zen.alchan.helper.doOnApplyWindowInsets
import com.zen.alchan.helper.updateSidePadding
import com.zen.alchan.helper.updateTopPadding
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_follows.*
import kotlinx.android.synthetic.main.layout_toolbar.*

class FollowsActivity : BaseActivity() {

    companion object {
        const val START_POSITION = "startPosition"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_follows)

        changeStatusBarColor(AndroidUtility.getResValueFromRefAttr(this, R.attr.themeCardColor))

        followsLayout.doOnApplyWindowInsets { view, windowInsets, initialPadding ->
            view.updateTopPadding(windowInsets, initialPadding)
            view.updateSidePadding(windowInsets, initialPadding)
        }

        setSupportActionBar(followsToolbar)
        supportActionBar?.apply {
            title = getString(R.string.friends)
            setDisplayHomeAsUpEnabled(true)
        }

        followsViewPager.adapter = FollowsViewPagerAdapter(supportFragmentManager)
        followsTabLayout.setupWithViewPager(followsViewPager)

        followsViewPager.currentItem = intent.getIntExtra(START_POSITION, 0)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
