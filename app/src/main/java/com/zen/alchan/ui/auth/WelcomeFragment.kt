package com.zen.alchan.ui.auth


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding

import com.zen.alchan.R
import com.zen.alchan.helper.doOnApplyWindowInsets
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.updateAllPadding
import com.zen.alchan.ui.base.BaseListener
import kotlinx.android.synthetic.main.fragment_welcome.*

class WelcomeFragment : Fragment() {

    private var listener: BaseListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseListener) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        contentLayout.doOnApplyWindowInsets { view, windowInsets, initialPadding ->
            view.updateAllPadding(windowInsets, initialPadding)
        }

        GlideApp.with(this).load(R.drawable.welcome_background).into(welcomeBackgroundImage)

        getStartedLayout.setOnClickListener {
            listener?.changeFragment(LoginFragment())
        }
    }
}
