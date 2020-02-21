package com.zen.alchan.ui.profile.bio


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer

import com.zen.alchan.R
import kotlinx.android.synthetic.main.fragment_bio.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
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
        setupObserver()
        initLayout()

    }

    private fun setupObserver() {
        viewModel.viewerData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                initLayout()
            }
        })
    }

    private fun initLayout() {
        val aboutText = "<font color=\"white\">" + viewModel.viewerData.value?.about + "</font>"
        bioWebView.webChromeClient = WebChromeClient()
        bioWebView.setBackgroundColor(ContextCompat.getColor(activity!!, android.R.color.transparent))
        bioWebView.settings.javaScriptEnabled = false
        bioWebView.loadData(aboutText, "text/html", "utf-8")
    }
}
