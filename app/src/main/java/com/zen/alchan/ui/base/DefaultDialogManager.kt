package com.zen.alchan.ui.base

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.zen.alchan.data.response.AnimeTheme
import com.zen.alchan.data.response.AnimeThemeEntry
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.MediaList
import com.zen.alchan.data.response.anilist.MediaTag
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.helper.pojo.SliderItem
import com.zen.alchan.helper.pojo.TextInputSetting
import com.zen.alchan.type.ScoreFormat
import com.zen.alchan.ui.common.*
import com.zen.alchan.ui.common.BottomSheetTagDialog
import com.zen.alchan.ui.editor.BottomSheetProgressDialog
import com.zen.alchan.ui.editor.BottomSheetScoreDialog
import com.zen.alchan.ui.media.MediaListener
import com.zen.alchan.ui.media.themes.BottomSheetMediaThemesDialog
import com.zen.alchan.ui.medialist.BottomSheetMediaListQuickDetailDialog
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

class DefaultDialogManager(private val context: Context) : DialogManager {

    private var bottomSheetListDialog: BottomSheetListDialog? = null
    private var bottomSheetTextInputDialog: BottomSheetTextInputDialog? = null
    private var bottomSheetSliderDialog: BottomSheetSliderDialog? = null
    private var bottomSheetTagDialog: BottomSheetTagDialog? = null
    private var bottomSheetProgressDialog: BottomSheetProgressDialog? = null
    private var bottomSheetScoreDialog: BottomSheetScoreDialog? = null
    private var bottomSheetSpoilerDialog: BottomSheetSpoilerDialog? = null
    private var bottomSheetMediaQuickDetailDialog: BottomSheetMediaQuickDetailDialog? = null
    private var bottomSheetMediaListQuickDetailDialog: BottomSheetMediaListQuickDetailDialog? = null
    private var bottomSheetMediaThemesDialog: BottomSheetMediaThemesDialog? = null

    private var datePickerDialog: DatePickerDialog? = null

    private var isToastShowing = false

    override fun showToast(message: Int) {
        if (!isToastShowing) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            startToastTimer()
        }
    }

    override fun showToast(message: String) {
        if (!isToastShowing) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            startToastTimer()
        }
    }

    private fun startToastTimer() {
        isToastShowing = true

        Single.timer(2, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    isToastShowing = false
                },
                {

                }
            )
    }

    override fun showMessageDialog(title: Int, message: Int, positiveButton: Int) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButton, null)
            .setCancelable(false)
            .show()
    }

    override fun showMessageDialog(title: String, message: String, positiveButton: Int) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButton, null)
            .setCancelable(false)
            .show()
    }

    override fun showActionDialog(
        title: String,
        message: String,
        positiveButton: Int,
        positiveAction: () -> Unit
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButton) { _, _ -> positiveAction() }
            .setCancelable(false)
            .show()
    }

    override fun showConfirmationDialog(
        title: Int,
        message: Int,
        positiveButton: Int,
        positiveAction: () -> Unit,
        negativeButton: Int,
        negativeAction: () -> Unit,
        thirdButton: Int?,
        thirdAction: (() -> Unit)?
    ) {
        val builder = AlertDialog.Builder(context)
        builder.apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(positiveButton) { _, _ -> positiveAction() }
            setNegativeButton(negativeButton) { _, _ -> negativeAction() }
            if (thirdButton != null) setNeutralButton(thirdButton) { _, _ -> thirdAction?.invoke() }
            setCancelable(false)
            show()
        }

    }

    override fun showConfirmationDialog(
        title: String,
        message: String,
        positiveButton: Int,
        positiveAction: () -> Unit,
        negativeButton: Int,
        negativeAction: () -> Unit,
        thirdButton: Int?,
        thirdAction: (() -> Unit)?
    ) {
        val builder = AlertDialog.Builder(context)
        builder.apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(positiveButton) { _, _ -> positiveAction() }
            setNegativeButton(negativeButton) { _, _ -> negativeAction() }
            if (thirdButton != null) setNeutralButton(thirdButton) { _, _ -> thirdAction?.invoke() }
            setCancelable(false)
            show()
        }

    }

    override fun <T> showListDialog(
        list: List<ListItem<T>>,
        action: (data: T, index: Int) -> Unit
    ) {
        val adapter = BottomSheetListRvAdapter(context, list, object : BottomSheetListRvAdapter.BottomSheetListListener<T> {
            override fun getSelectedItem(data: T, index: Int) {
                dismissListDialog()
                action(data, index)
            }
        })
        bottomSheetListDialog = BottomSheetListDialog.newInstance(adapter)
        bottomSheetListDialog?.dialog?.setOnCancelListener {
            bottomSheetListDialog = null
        }
        (context as? AppCompatActivity)?.supportFragmentManager?.let {
            bottomSheetListDialog?.show(it, null)
        }
    }

    override fun showListDialog(adapter: BaseRecyclerViewAdapter<*, *>) {
        bottomSheetListDialog = BottomSheetListDialog.newInstance(adapter)
        bottomSheetListDialog?.dialog?.setOnCancelListener {
            bottomSheetListDialog = null
        }
        (context as? AppCompatActivity)?.supportFragmentManager?.let {
            bottomSheetListDialog?.show(it, null)
        }
    }

    override fun dismissListDialog() {
        bottomSheetListDialog?.dismiss()
        bottomSheetListDialog = null
    }

    override fun showTextInputDialog(
        currentText: String,
        textInputSetting: TextInputSetting,
        action: (newText: String) -> Unit
    ) {
        bottomSheetTextInputDialog = BottomSheetTextInputDialog.newInstance(currentText, textInputSetting, object : BottomSheetTextInputDialog.BottomSheetTextInputListener {
            override fun getNewText(newText: String) {
                action(newText)
            }
        })
        bottomSheetTextInputDialog?.dialog?.setOnCancelListener {
            bottomSheetTextInputDialog = null
        }
        (context as? AppCompatActivity)?.supportFragmentManager?.let {
            bottomSheetTextInputDialog?.show(it, null)
        }
    }

    override fun showSliderDialog(
        sliderItem: SliderItem,
        useSingleSlider: Boolean,
        action: (minValue: Int?, maxValue: Int?) -> Unit
    ) {
        bottomSheetSliderDialog = BottomSheetSliderDialog.newInstance(sliderItem, useSingleSlider, object : BottomSheetSliderDialog.BottomSheetSliderListener {
            override fun getNewValues(minValue: Int?, maxValue: Int?) {
                action(minValue, maxValue)
            }
        })
        bottomSheetSliderDialog?.dialog?.setOnCancelListener {
            bottomSheetSliderDialog = null
        }
        (context as? AppCompatActivity)?.supportFragmentManager?.let {
            bottomSheetSliderDialog?.show(it, null)
        }
    }

    override fun <T> showMultiSelectDialog(
        list: List<ListItem<T>>,
        selectedIndex: ArrayList<Int>,
        action: (data: List<T>) -> Unit
    ) {
        val adapter = BottomSheetMultiSelectRvAdapter(context, list, selectedIndex, object : BottomSheetMultiSelectRvAdapter.BottomSheetMultiSelectListener<T> {
            override fun getSelectedItems(data: List<T>, index: List<Int>) {
                action(data)
            }
        })
        bottomSheetListDialog = BottomSheetListDialog.newInstance(adapter)
        bottomSheetListDialog?.dialog?.setOnCancelListener {
            bottomSheetListDialog = null
        }
        (context as? AppCompatActivity)?.supportFragmentManager?.let {
            bottomSheetListDialog?.show(it, null)
        }
    }

    override fun showTagDialog(
        list: List<ListItem<MediaTag?>>,
        selectedIndex: ArrayList<Int>,
        action: (data: List<MediaTag>) -> Unit
    ) {
        bottomSheetTagDialog = BottomSheetTagDialog.newInstance(list, selectedIndex, object : BottomSheetTagDialog.TagDialogListener {
            override fun getSelectedTags(list: List<MediaTag>) {
                action(list)
            }
        })
        bottomSheetTagDialog?.dialog?.setOnCancelListener {
            bottomSheetTagDialog = null
        }
        (context as? AppCompatActivity)?.supportFragmentManager?.let {
            bottomSheetTagDialog?.show(it, null)
        }
    }

    override fun showProgressDialog(
        mediaType: MediaType,
        currentProgress: Int,
        maxProgress: Int?,
        isProgressVolume: Boolean,
        action: (newProgress: Int) -> Unit
    ) {
        bottomSheetProgressDialog = BottomSheetProgressDialog.newInstance(mediaType, currentProgress, maxProgress, isProgressVolume, object : BottomSheetProgressDialog.BottomSheetProgressListener {
            override fun getNewProgress(newProgress: Int) {
                action(newProgress)
            }
        })
        bottomSheetProgressDialog?.dialog?.setOnCancelListener {
            bottomSheetProgressDialog = null
        }
        (context as? AppCompatActivity)?.supportFragmentManager?.let {
            bottomSheetProgressDialog?.show(it, null)
        }
    }

    override fun showScoreDialog(
        scoreFormat: ScoreFormat,
        currentScore: Double,
        advancedScores: LinkedHashMap<String, Double>?,
        action: (newScore: Double, newAdvancedScores: LinkedHashMap<String, Double>?) -> Unit
    ) {
        bottomSheetScoreDialog = BottomSheetScoreDialog.newInstance(scoreFormat, currentScore, advancedScores, object : BottomSheetScoreDialog.BottomSheetScoreListener {
            override fun getNewScore(newScore: Double, newAdvancedScores: LinkedHashMap<String, Double>?) {
                action(newScore, newAdvancedScores)
            }
        })
        bottomSheetScoreDialog?.dialog?.setOnCancelListener {
            bottomSheetScoreDialog = null
        }
        (context as? AppCompatActivity)?.supportFragmentManager?.let {
            bottomSheetScoreDialog?.show(it, null)
        }
    }

    override fun showDatePicker(calendar: Calendar, action: (year: Int, month: Int, dayOfMonth: Int) -> Unit) {
        datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                action(year, month + 1, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
//        datePickerDialog?.datePicker?.minDate =
        datePickerDialog?.setOnCancelListener {
            datePickerDialog = null
        }
        datePickerDialog?.show()
    }


    override fun showSpoilerDialog(
        spoilerText: String,
        onLinkClickAction: ((link: String) -> Unit)?
    ) {
        if (onLinkClickAction != null) {
            bottomSheetSpoilerDialog = BottomSheetSpoilerDialog.newInstance(spoilerText, object : BottomSheetSpoilerDialog.SpoilerListener {
                override fun onLinkClick(link: String) {
                    onLinkClickAction(link)
                }
            })
        } else {
            bottomSheetSpoilerDialog = BottomSheetSpoilerDialog.newInstance(spoilerText, null)
        }

        bottomSheetSpoilerDialog?.dialog?.setOnCancelListener {
            bottomSheetSpoilerDialog = null
        }
        (context as? AppCompatActivity)?.supportFragmentManager?.let {
            bottomSheetSpoilerDialog?.show(it, null)
        }
    }

    override fun showShareSheet(text: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

    override fun showMediaQuickDetailDialog(media: Media) {
        bottomSheetMediaQuickDetailDialog = BottomSheetMediaQuickDetailDialog.newInstance(media)
        bottomSheetMediaQuickDetailDialog?.dialog?.setOnCancelListener {
            bottomSheetMediaQuickDetailDialog = null
        }
        (context as? AppCompatActivity?)?.supportFragmentManager?.let {
            bottomSheetMediaQuickDetailDialog?.show(it, null)
        }
    }

    override fun showMediaListQuickDetailDialog(userId: Int, mediaList: MediaList) {
        bottomSheetMediaListQuickDetailDialog = BottomSheetMediaListQuickDetailDialog.newInstance(userId, mediaList)
        bottomSheetMediaListQuickDetailDialog?.dialog?.setOnCancelListener {
            bottomSheetMediaListQuickDetailDialog = null
        }
        (context as? AppCompatActivity?)?.supportFragmentManager?.let {
            bottomSheetMediaListQuickDetailDialog?.show(it, null)
        }
    }

    override fun showAnimeThemesDialog(
        media: Media,
        animeTheme: AnimeTheme,
        animeThemeEntry: AnimeThemeEntry?,
        action: (url: String, usePlayer: Boolean) -> Unit
    ) {
        bottomSheetMediaThemesDialog = BottomSheetMediaThemesDialog.newInstance(media, animeTheme, animeThemeEntry, object : BottomSheetMediaThemesDialog.BottomSheetMediaThemeListener {
            override fun playWithPlayer(url: String) {
                action(url, true)
            }

            override fun playWithOtherApp(url: String) {
                action(url, false)
            }
        })
        bottomSheetMediaThemesDialog?.dialog?.setOnCancelListener {
            bottomSheetMediaThemesDialog = null
        }
        (context as? AppCompatActivity?)?.supportFragmentManager?.let {
            bottomSheetMediaThemesDialog?.show(it, null)
        }
    }
}