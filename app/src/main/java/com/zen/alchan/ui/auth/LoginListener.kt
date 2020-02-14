package com.zen.alchan.ui.auth

import androidx.fragment.app.Fragment

interface LoginListener {
    fun changeFragment(targetFragment: Fragment, addToBackStack: Boolean = true)
}