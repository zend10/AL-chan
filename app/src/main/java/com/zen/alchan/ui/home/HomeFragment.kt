package com.zen.alchan.ui.home


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.request.RequestOptions

import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.ui.search.SearchActivity
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initLayout()
    }

    private fun initLayout() {
        GlideApp.with(this).load(R.drawable.welcome_background).into(headerImage)
        GlideApp.with(this).load(R.drawable.ic_launcher_foreground).apply(RequestOptions.circleCropTransform()).into(userAvatar)
        greetingsText.text = "Hello, ottermatter."

        searchBar.setOnClickListener { startActivity(Intent(activity, SearchActivity::class.java)) }
    }
}
