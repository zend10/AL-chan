package com.zen.alchan.ui.profile.bio


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import com.zen.alchan.R
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.ui.browse.user.UserFragment
import kotlinx.android.synthetic.main.fragment_bio.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class BioFragment : Fragment() {

    private val viewModel by viewModel<BioViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bio, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (arguments != null && arguments?.getInt(UserFragment.USER_ID) != null && arguments?.getInt(UserFragment.USER_ID) != 0) {
            viewModel.otherUserId = arguments?.getInt(UserFragment.USER_ID)
        }

        setupObserver()
        initLayout()
    }

    private fun setupObserver() {
        if (viewModel.otherUserId != null) {
            viewModel.userData.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    initLayout()
                }
            })
        } else {
            viewModel.viewerData.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    initLayout()
                }
            })
        }
    }

    private fun initLayout() {
        val aboutText = if (viewModel.otherUserId != null) {
            viewModel.userData.value?.user?.about
        } else {
            viewModel.viewerData.value?.about
        }

        val maxWidth = AndroidUtility.getScreenWidth(activity)
        val markwon = AndroidUtility.initMarkwon(activity!!)
        AndroidUtility.convertMarkdown(activity!!, bioTextView, aboutText, maxWidth, markwon)
    }
}
