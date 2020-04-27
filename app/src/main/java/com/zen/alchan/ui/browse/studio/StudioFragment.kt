package com.zen.alchan.ui.browse.studio


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils

import com.zen.alchan.R
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class StudioFragment : BaseFragment() {

    private val viewModel by viewModel<StudioViewModel>()

    private lateinit var scaleUpAnim: Animation
    private lateinit var scaleDownAnim: Animation

    private lateinit var adapter: StudioMediaRvAdapter
    private lateinit var itemOpenAniList: MenuItem
    private lateinit var itemCopyLink: MenuItem

    companion object {
        const val STUDIO_ID = "studioId"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_studio, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.studioId = arguments?.getInt(STUDIO_ID)
        scaleUpAnim = AnimationUtils.loadAnimation(activity, R.anim.scale_up)
        scaleDownAnim = AnimationUtils.loadAnimation(activity, R.anim.scale_down)

//        characterToolbar.setNavigationOnClickListener { activity?.finish() }
//        characterToolbar.navigationIcon = ContextCompat.getDrawable(activity!!, R.drawable.ic_delete)
//        characterToolbar.inflateMenu(R.menu.menu_anilist_link)
//        itemOpenAniList = characterToolbar.menu.findItem(R.id.itemOpenAnilist)
//        itemCopyLink = characterToolbar.menu.findItem(R.id.itemCopyLink)
    }
}
