package com.zen.alchan.ui.social


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.zen.alchan.R
import kotlinx.android.synthetic.main.layout_toolbar.*

/**
 * A simple [Fragment] subclass.
 */
class SocialFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_social, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        toolbarLayout.title = getString(R.string.social)
    }
}
