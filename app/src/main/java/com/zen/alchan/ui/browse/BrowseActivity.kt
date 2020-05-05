package com.zen.alchan.ui.browse

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.zen.alchan.R
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.ui.base.BaseActivity
import com.zen.alchan.ui.base.BaseListener
import com.zen.alchan.ui.browse.character.CharacterFragment
import com.zen.alchan.ui.browse.media.MediaFragment
import com.zen.alchan.ui.browse.staff.StaffFragment
import com.zen.alchan.ui.browse.studio.StudioFragment
import kotlinx.android.synthetic.main.activity_browse.*
import type.MediaType

class BrowseActivity : BaseActivity(), BaseListener {

    companion object {
        const val TARGET_PAGE = "targetPage"
        const val LOAD_ID = "loadId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse)

        if (supportFragmentManager.backStackEntryCount == 0) {
            lateinit var targetFragment: Fragment
            val bundle = Bundle()

            when (BrowsePage.valueOf(intent.getStringExtra(TARGET_PAGE))) {
                BrowsePage.ANIME -> {
                    targetFragment = MediaFragment()
                    bundle.putInt(MediaFragment.MEDIA_ID, intent.getIntExtra(LOAD_ID, 0))
                    bundle.putString(MediaFragment.MEDIA_TYPE, MediaType.ANIME.name)
                }
                BrowsePage.MANGA -> {
                    targetFragment = MediaFragment()
                    bundle.putInt(MediaFragment.MEDIA_ID, intent.getIntExtra(LOAD_ID, 0))
                    bundle.putString(MediaFragment.MEDIA_TYPE, MediaType.MANGA.name)
                }
                BrowsePage.CHARACTER -> {
                    targetFragment = CharacterFragment()
                    bundle.putInt(CharacterFragment.CHARACTER_ID, intent.getIntExtra(LOAD_ID, 0))
                }
                BrowsePage.STAFF -> {
                    targetFragment = StaffFragment()
                    bundle.putInt(StaffFragment.STAFF_ID, intent.getIntExtra(LOAD_ID, 0))
                }
                BrowsePage.STUDIO -> {
                    targetFragment = StudioFragment()
                    bundle.putInt(StudioFragment.STUDIO_ID, intent.getIntExtra(LOAD_ID, 0))
                }
            }

            targetFragment.arguments = bundle
            changeFragment(targetFragment, false)
        }
    }

    override fun changeFragment(targetFragment: Fragment, addToBackStack: Boolean) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(browseFrameLayout.id, targetFragment)
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null)
        }
        fragmentTransaction.commit()
    }
}
