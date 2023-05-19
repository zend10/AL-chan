package com.zen.alchan.ui.media.mediastats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.databinding.FragmentMediaStatsBinding
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class MediaStatsFragment : BaseFragment<FragmentMediaStatsBinding, MediaStatsViewModel>() {

    private lateinit var media: Media

    override val viewModel: MediaStatsViewModel by viewModel()

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMediaStatsBinding {
        return FragmentMediaStatsBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {

    }

    override fun setUpObserver() {



        viewModel.loadData(MediaStatsParam(media))
    }

    companion object {
        @JvmStatic
        fun newInstance(media: Media) =
            MediaStatsFragment().apply {
                this.media = media
            }
    }
}