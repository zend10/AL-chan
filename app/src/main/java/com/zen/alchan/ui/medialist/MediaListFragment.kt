package com.zen.alchan.ui.medialist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaType


private const val MEDIA_TYPE = "mediaType"
private const val USER_ID = "userId"

class MediaListFragment : BaseFragment(R.layout.fragment_media_list) {

    private val viewModel by viewModel<MediaListViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            viewModel.mediaType = MediaType.valueOf(it.getString(MEDIA_TYPE) ?: MediaType.ANIME.name)
            viewModel.userId = it.getInt(USER_ID)
        }
    }

    override fun setupObserver() {

    }

    override fun setupLayout() {

    }

    companion object {
        @JvmStatic
        fun newInstance(mediaType: MediaType, userId: Int = 0) =
            MediaListFragment().apply {
                arguments = Bundle().apply {
                    putString(MEDIA_TYPE, mediaType.name)
                    putInt(USER_ID, userId)
                }
            }
    }
}