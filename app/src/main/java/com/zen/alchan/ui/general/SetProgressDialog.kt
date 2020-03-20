package com.zen.alchan.ui.general

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.gson.Gson
import com.zen.alchan.R
import com.zen.alchan.data.response.MediaList
import kotlinx.android.synthetic.main.dialog_set_progress.*
import kotlinx.android.synthetic.main.dialog_set_progress.view.*

class SetProgressDialog : DialogFragment() {

    interface SetProgressListener {
        fun passProgress(newProgress: Int)
    }

    private lateinit var listener: SetProgressListener
    private val gson = Gson()

    companion object {
        const val BUNDLE_MEDIA_LIST = "mediaList"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        val dialogView = activity!!.layoutInflater.inflate(R.layout.dialog_set_progress, setProgressDialogLayout, false)

        val mediaList = gson.fromJson(arguments?.getString(BUNDLE_MEDIA_LIST), MediaList::class.java)

        dialogView.currentProgressText.text = mediaList.progress?.toString()
        dialogView.episodeCountText.text = "/ ${if (mediaList.media?.episodes != null) mediaList.media?.episodes?.toString() else "?"}"

        builder.setTitle(R.string.set_progress)
        builder.setPositiveButton(R.string.set) { _, _ ->
            if (dialogView.setProgressField.text.isNullOrBlank()) {
                dismiss()
                return@setPositiveButton
            }

            var newProgress = dialogView.setProgressField.text.toString().toInt()

            if (newProgress > UShort.MAX_VALUE.toInt()) {
                newProgress = UShort.MAX_VALUE.toInt()
            }

            if (mediaList.media?.episodes != null && newProgress > mediaList.media?.episodes!!) {
                newProgress = mediaList.media?.episodes!!
            }

            listener.passProgress(newProgress)
        }
        builder.setNegativeButton(R.string.cancel, null)
        builder.setView(dialogView)

        return builder.create()
    }

    fun setListener(setProgressListener: SetProgressListener) {
        listener = setProgressListener
    }
}