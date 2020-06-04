package com.zen.alchan.ui.common

import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.apollographql.apollo.api.CustomTypeValue
import com.google.gson.internal.LinkedTreeMap
import com.zen.alchan.R
import com.zen.alchan.data.response.MediaList
import com.zen.alchan.helper.*
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.AdvancedScoresItem
import com.zen.alchan.helper.pojo.CustomListsItem
import com.zen.alchan.helper.utils.AndroidUtility
import kotlinx.android.synthetic.main.dialog_media_list_detail.*
import kotlinx.android.synthetic.main.dialog_media_list_detail.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaListStatus
import type.MediaType
import type.ScoreFormat

class MediaListDetailDialog : DialogFragment() {

    private val viewModel by viewModel<MediaListDetailDialogViewModel>()

    private lateinit var mediaList: MediaList
    private lateinit var mediaType: MediaType
    private lateinit var scoreFormat: ScoreFormat
    private var useAdvancedScores = false

    companion object {
        const val MEDIA_LIST_ITEM = "mediaListItem"
        const val SCORE_FORMAT = "scoreFormat"
        const val USE_ADVANCED_SCORES = "useAdvancedScores"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        val dialogView = activity!!.layoutInflater.inflate(R.layout.dialog_media_list_detail, mediaListDetailLayout, false)

        mediaList = viewModel.gson.fromJson(arguments?.getString(MEDIA_LIST_ITEM), MediaList::class.java)
        mediaType = mediaList.media?.type!!
        scoreFormat = ScoreFormat.valueOf(arguments?.getString(SCORE_FORMAT)!!)
        useAdvancedScores = arguments?.getBoolean(USE_ADVANCED_SCORES, false)!!

        dialogView.mediaTitleText.text = mediaList.media?.title?.userPreferred
        dialogView.mediaFormatText.text = mediaList.media?.format?.name?.replaceUnderscore()

        if (mediaList.status == MediaListStatus.CURRENT) {
            dialogView.mediaListStatusText.text = getString(if (mediaType == MediaType.ANIME) R.string.watching_caps else R.string.reading_caps)
        } else {
            dialogView.mediaListStatusText.text = mediaList.status?.name.replaceUnderscore()
        }

        if (mediaList.progress != null && mediaList.progress != 0) {
            dialogView.mediaListProgressLayout.visibility = View.VISIBLE
            var progressText = mediaList.progress?.toString()
            progressText += if (mediaType == MediaType.ANIME) {
                " ${getString(R.string.episode).setRegularPlural(mediaList.progress)}"
            } else {
                " ${getString(R.string.chapter).setRegularPlural(mediaList.progress)}"
            }
            dialogView.mediaListProgressText.text = progressText
        }

        if (mediaList.progressVolumes != null && mediaList.progressVolumes != 0) {
            dialogView.mediaListProgressVolumeLayout.visibility = View.VISIBLE
            dialogView.mediaListProgressVolumeText.text = "${mediaList.progressVolumes} ${getString(R.string.volume).setRegularPlural(mediaList.progressVolumes)}"
        }

        if (scoreFormat == ScoreFormat.POINT_3) {
            GlideApp.with(activity!!).load(AndroidUtility.getSmileyFromScore(mediaList.score)).into(dialogView.mediaListScoreIcon)
            dialogView.mediaListScoreIcon.imageTintList = ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(context, R.attr.themeContentColor))
            dialogView.mediaListScoreText.text = ""
        } else {
            GlideApp.with(activity!!).load(R.drawable.ic_star_filled).into(dialogView.mediaListScoreIcon)
            dialogView.mediaListScoreIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(activity!!, R.color.yellowStar))
            dialogView.mediaListScoreText.text = mediaList.score?.removeTrailingZero()
        }

        if (useAdvancedScores && mediaList.advancedScores != null) {
            try {
                val advancedScores = ArrayList<AdvancedScoresItem>()
                val advancedScoressMap = (mediaList.advancedScores as LinkedTreeMap<String, LinkedTreeMap<String, Double>>)["value"]!!
                advancedScoressMap.forEach { (key, value) ->
                    advancedScores.add(AdvancedScoresItem(key, value))
                }
                dialogView.mediaListAdvancedScoresRecyclerView.adapter = AdvancedScoringRvAdapter(advancedScores, null, false)
                dialogView.mediaListAdvancedScoresRecyclerView.visibility = View.VISIBLE
            } catch (e: Exception) {
                dialogView.mediaListAdvancedScoresRecyclerView.visibility = View.GONE
            }
        } else {
            dialogView.mediaListAdvancedScoresRecyclerView.visibility = View.GONE
        }

        dialogView.mediaListStartDateText.text = if (mediaList.startedAt != null) mediaList.startedAt.toStringDateFormat() else "?"
        dialogView.mediaListFinishDateText.text = if (mediaList.completedAt != null) mediaList.completedAt.toStringDateFormat() else "?"
        dialogView.mediaListRepeatLabel.text = getString(if (mediaType == MediaType.ANIME) R.string.total_rewatches else R.string.total_rereads)
        dialogView.mediaListRepeatText.text = mediaList.repeat?.toString() ?: "0"
        dialogView.mediaListNotesText.text = mediaList.notes ?: "-"
        dialogView.mediaListPriorityText.text = if (Constant.PRIORITY_LABEL_MAP.containsKey(mediaList.priority)) {
            Constant.PRIORITY_LABEL_MAP[mediaList.priority]
        } else {
            (mediaList.priority?.toString() ?: "-")
        }

        if (mediaList.customLists != null) {
            try {
                val customLists = ArrayList<CustomListsItem>()
                val customListsMap = (mediaList.customLists as LinkedTreeMap<String, LinkedTreeMap<String, Boolean>>)["value"]!!
                customListsMap.forEach { (key, value) ->
                    customLists.add(CustomListsItem(key, value))
                }
                dialogView.mediaListCustomListsRecyclerView.adapter = CustomListsRvAdapter(customLists, null, false)
                dialogView.mediaListCustomListsRecyclerView.visibility = View.VISIBLE
                dialogView.mediaListCustomListsNoItemText.visibility = View.GONE
            } catch (e: Exception) {
                dialogView.mediaListCustomListsRecyclerView.visibility = View.GONE
                dialogView.mediaListCustomListsNoItemText.visibility = View.VISIBLE
            }
        } else {
            dialogView.mediaListCustomListsNoItemText.visibility = View.VISIBLE
        }

        dialogView.hideFromStatusListsCheckBox.isChecked = mediaList.hiddenFromStatusList == true
        dialogView.hideFromStatusListsCheckBox.isEnabled = false

        dialogView.privateCheckBox.isChecked = mediaList.private == true
        dialogView.privateCheckBox.isEnabled = false

        builder.setView(dialogView)
        return builder.create()
    }
}