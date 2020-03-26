package com.zen.alchan.ui.general

import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.zen.alchan.R
import com.zen.alchan.data.response.MediaList
import com.zen.alchan.helper.genericType
import com.zen.alchan.helper.pojo.AdvancedScoresItem
import com.zen.alchan.helper.removeTrailingZero
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import kotlinx.android.synthetic.main.dialog_set_score.*
import kotlinx.android.synthetic.main.dialog_set_score.view.*
import type.ScoreFormat

class SetScoreDialog : DialogFragment() {

    interface SetScoreListener {
        fun passScore(newScore: Double, newAdvancedScores: List<Double>?)
    }

    private var listener: SetScoreListener? = null
    private val gson = Gson()

    private lateinit var scoreFormat: ScoreFormat
    private lateinit var advancedScoring: ArrayList<String>

    private lateinit var dialogView: View
    private var currentScore: Double? = null
    private var currentAdvancedScores: ArrayList<Double>? = null

    private var advancedScoresList = ArrayList<AdvancedScoresItem>()
    private var smileyList = ArrayList<ImageView>()

    companion object {
        const val BUNDLE_SCORE_FORMAT = "scoreFormat"
        const val BUNDLE_ADVANCED_SCORING = "advancedScoring"
        const val BUNDLE_CURRENT_SCORE = "currentScore"
        const val BUNDLE_ADVANCED_SCORES_LIST = "advancedScoresList"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        dialogView = activity!!.layoutInflater.inflate(R.layout.dialog_set_score, setScoreDialogLayout, false)

        scoreFormat = ScoreFormat.valueOf(arguments?.getString(BUNDLE_SCORE_FORMAT)!!)
        advancedScoring = arguments?.getStringArrayList(BUNDLE_ADVANCED_SCORING)!!
        currentScore = arguments?.getDouble(BUNDLE_CURRENT_SCORE)
        advancedScoresList = gson.fromJson(arguments?.getString(BUNDLE_ADVANCED_SCORES_LIST), genericType<List<AdvancedScoresItem>>())

        if (scoreFormat == ScoreFormat.POINT_100 || scoreFormat == ScoreFormat.POINT_10_DECIMAL) {
            currentAdvancedScores = ArrayList()
            advancedScoresList.forEach {
                currentAdvancedScores?.add(it.score)
            }
        }

        initLayout()

        builder.setTitle(R.string.set_score)
        builder.setPositiveButton(R.string.set, null)
        builder.setNegativeButton(R.string.cancel, null)
        builder.setView(dialogView)

        val alertDialog = builder.create()
        alertDialog.setOnShowListener {
            val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                if (currentScore == null) {
                    dismiss()
                    return@setOnClickListener
                }

                if (handleScore()) {
                    dismiss()
                    listener?.passScore(currentScore!!, currentAdvancedScores)
                }
            }
        }

        if (listener == null) {
            dismiss()
        }

        return alertDialog
    }

    private fun initLayout() {
        when (scoreFormat) {
            ScoreFormat.POINT_100 -> {
                dialogView.score100Layout.visibility = View.VISIBLE
                dialogView.setScore100Field.setText(if (currentScore == null || currentScore == 0.0) "" else currentScore?.toInt().toString())

                if (!advancedScoring.isNullOrEmpty()) {
                    dialogView.score100AdvancedScoringLayout.visibility = View.VISIBLE
                }

                dialogView.advancedScoring100RecyclerView.adapter = assignAdvancedScoringAdapter()
            }
            ScoreFormat.POINT_10_DECIMAL -> {
                dialogView.scoreDecimalLayout.visibility = View.VISIBLE
                dialogView.setScoreDecimalField.setText(if (currentScore == null || currentScore == 0.0) "" else currentScore?.removeTrailingZero())

                if (!advancedScoring.isNullOrEmpty()) {
                    dialogView.scoreDecimalAdvancedScoringLayout.visibility = View.VISIBLE
                }

                dialogView.advancedScoringDecimalRecyclerView.adapter = assignAdvancedScoringAdapter()
            }
            ScoreFormat.POINT_10 -> {
                dialogView.score10Layout.visibility = View.VISIBLE
                dialogView.setScore10Field.apply {
                    minValue = 0
                    maxValue = 10
                    displayedValues = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
                    wrapSelectorWheel = false
                    value = if (currentScore == null) 0 else currentScore?.toInt()!!
                }
            }
            ScoreFormat.POINT_5 -> {
                dialogView.score5Layout.visibility = View.VISIBLE
                dialogView.setScore5Field.rating = currentScore?.toFloat() ?: 0F
            }
            ScoreFormat.POINT_3 -> {
                dialogView.score3Layout.visibility = View.VISIBLE

                smileyList = arrayListOf(dialogView.setScore3Sad, dialogView.setScore3Neutral, dialogView.setScore3Happy)
                val scoreList = listOf(1.0, 2.0, 3.0)

                smileyList.forEach {  emotes ->
                    emotes.setOnClickListener {
                        if (currentScore == scoreList[smileyList.indexOf(it)]) {
                            currentScore = 0.0
                            handleSmiley(null)
                        } else {
                            currentScore = scoreList[smileyList.indexOf(it)]
                            handleSmiley(it.id)
                        }
                    }
                }

                if (currentScore != null && currentScore != 0.0) {
                    handleSmiley(smileyList[scoreList.indexOf(currentScore!!)].id)
                }
            }
        }
    }

    private fun assignAdvancedScoringAdapter(): AdvancedScoringRvAdapter {
        return AdvancedScoringRvAdapter(advancedScoresList, object : AdvancedScoringRvAdapter.AdvancedScoringListener {
            override fun passScores(index: Int, newScore: Double) {
                if (scoreFormat == ScoreFormat.POINT_100) {
                    currentAdvancedScores!![index] = if (newScore > 100) {
                        100.0
                    } else {
                        newScore.toInt().toDouble()
                    }
                    var counter = 0
                    var sum = 0
                    currentAdvancedScores?.forEach {
                        if (it != 0.0) {
                            counter += 1
                            sum += it.toInt()
                        }
                    }
                    currentScore = if (counter != 0) {
                        (sum / counter).toDouble()
                    } else {
                        0.0
                    }
                    dialogView.setScore100Field.setText(currentScore?.removeTrailingZero())
                } else if (scoreFormat == ScoreFormat.POINT_10_DECIMAL) {
                    currentAdvancedScores!![index] = if (newScore > 10) {
                        10.0
                    } else {
                        newScore
                    }
                    var counter = 0
                    var sum = 0.0
                    currentAdvancedScores?.forEach {
                        if (it != 0.0) {
                            counter += 1
                            sum += it
                        }
                    }
                    currentScore = if (counter != 0) {
                        sum / counter.toDouble()
                    } else {
                        0.0
                    }
                    dialogView.setScoreDecimalField.setText(currentScore?.removeTrailingZero())
                }
            }
        })
    }

    private fun handleSmiley(smileyId: Int?) {
        smileyList.forEach {
            if (it.id == smileyId) {
                it.imageTintList = ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(context, R.attr.themePrimaryColor))
            } else {
                it.imageTintList = ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(context, R.attr.themeContentColor))
            }
        }
    }

    private fun handleScore(): Boolean {
        when (scoreFormat) {
            ScoreFormat.POINT_100 -> {
                return try {
                    currentScore = dialogView.setScore100Field.text.toString().toDouble()
                    if (currentScore!! > 100) {
                        throw Exception()
                    }
                    true
                } catch (e: Exception) {
                    DialogUtility.showToast(activity, "Please use valid number between 0-100.")
                    false
                }
            }
            ScoreFormat.POINT_10_DECIMAL -> {
                return try {
                    currentScore = dialogView.setScoreDecimalField.text.toString().toDouble()
                    if (currentScore!! > 10) {
                        throw Exception()
                    }
                    true
                } catch (e: Exception) {
                    DialogUtility.showToast(activity, "Please use valid number between 0-10.")
                    false
                }
            }
            ScoreFormat.POINT_10 -> {
                currentScore = dialogView.setScore10Field.value.toDouble()
                return true
            }
            ScoreFormat.POINT_5 -> {
                currentScore = dialogView.setScore5Field.rating.toDouble()
                return true
            }
            ScoreFormat.POINT_3 -> {
                return true
            }
            else -> return false
        }
    }

    fun setListener(setScoreListener: SetScoreListener) {
        listener = setScoreListener
    }
}