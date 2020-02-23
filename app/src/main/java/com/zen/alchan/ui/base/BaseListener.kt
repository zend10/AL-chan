package com.zen.alchan.ui.base

import androidx.fragment.app.Fragment

interface BaseListener {
    fun changeFragment(targetFragment: Fragment, addToBackStack: Boolean = true)
}