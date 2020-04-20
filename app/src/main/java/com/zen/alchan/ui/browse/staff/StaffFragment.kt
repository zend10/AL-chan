package com.zen.alchan.ui.browse.staff


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.zen.alchan.R
import com.zen.alchan.ui.base.BaseFragment

/**
 * A simple [Fragment] subclass.
 */
class StaffFragment : BaseFragment() {

    companion object {
        const val STAFF_ID = "staffId"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_staff, container, false)
    }


}
