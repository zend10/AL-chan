package com.zen.alchan.ui.browse.media.overview

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.zen.alchan.R
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.utils.DialogUtility
import kotlinx.android.synthetic.main.dialog_themes_player.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ThemesPlayerDialog : DialogFragment() {

    private val viewModel by viewModel<ThemesPlayerViewModel>()

    private lateinit var dialogView: View

    companion object {
        const val MEDIA_TITLE = "mediaTitle"
        const val TRACK_TITLE = "trackTitle"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        dialogView = requireActivity().layoutInflater.inflate(R.layout.dialog_themes_player, null)

        viewModel.mediaTitle = arguments?.getString(MEDIA_TITLE) ?: ""
        viewModel.trackTitle = arguments?.getString(TRACK_TITLE) ?: ""

        setupObserver()

        dialogView.playOnYoutubeLayout.setOnClickListener {
            viewModel.getYouTubeVideo()
        }

        dialogView.playOnSpotifyLayout.setOnClickListener {

        }

        builder.setView(dialogView)
        builder.setTitle(R.string.select_player)
        builder.setNegativeButton(R.string.close, null)
        return builder.create()
    }

    private fun setupObserver() {
        viewModel.youTubeVideoResponse.observe(this, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> {
                    dialogView.themesPlayerLoadingLayout.visibility = View.VISIBLE
                }
                ResponseStatus.SUCCESS -> {
                    dialogView.themesPlayerLoadingLayout.visibility = View.GONE
                    if (!it.data?.items.isNullOrEmpty()) {
                        dismiss()
                        CustomTabsIntent.Builder()
                            .build()
                            .launchUrl(requireActivity(), Uri.parse("https://www.youtube.com/watch?v=${it.data?.items!![0].id.videoId}"))
                    } else {
                        DialogUtility.showToast(activity, R.string.sorry_failed_to_find_this_song_on_youtube)
                    }
                }
                ResponseStatus.ERROR -> {
                    dialogView.themesPlayerLoadingLayout.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                }
            }
        })
    }
}