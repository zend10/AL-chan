package com.zen.alchan.ui.base

import androidx.fragment.app.Fragment
import com.zen.alchan.helper.enums.BrowsePage

// listener for BaseFragment, to navigate between fragment
interface BaseListener {
    fun changeFragment(targetFragment: Fragment, addToBackStack: Boolean = true)
    fun changeFragment(browsePage: BrowsePage, id: Int, addToBackStack: Boolean = true) { }
}