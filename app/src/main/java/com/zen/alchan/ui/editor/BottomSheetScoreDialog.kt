package com.zen.alchan.ui.editor

import android.content.res.ColorStateList
import android.text.InputFilter
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.addTextChangedListener
import com.zen.R
import com.zen.databinding.DialogBottomSheetScoreBinding
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.ui.base.BaseDialogFragment
import com.zen.alchan.type.ScoreFormat

class BottomSheetScoreDialog : BaseDialogFragment<DialogBottomSheetScoreBinding>() {

    private var scoreFormat = ScoreFormat.POINT_100
    private var currentScore = 0.0
    private var advancedScores: LinkedHashMap<String, Double>? = null
    private var listener: BottomSheetScoreListener? = null
    private var advancedScoringAdapter: AdvancedScoringRvAdapter? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogBottomSheetScoreBinding {
        return DialogBottomSheetScoreBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        when (scoreFormat) {
            ScoreFormat.POINT_3 -> setUpScoreFormatSmileyLayout()
            ScoreFormat.POINT_5 -> setUpScoreFormatStarLayout()
            ScoreFormat.POINT_10 -> setUpScoreFormatPickerLayout()
            ScoreFormat.POINT_10_DECIMAL -> setUpScoreFormatInputDecimalLayout()
            else -> setUpScoreFormatInput100Layout()
        }

        if ((scoreFormat == ScoreFormat.POINT_100 || scoreFormat == ScoreFormat.POINT_10_DECIMAL) && !advancedScores.isNullOrEmpty()) {
            binding.scoreAdvancedScoringRecyclerView.show(true)
            advancedScoringAdapter = AdvancedScoringRvAdapter(advancedScores!!.toList(), scoreFormat, false, object : AdvancedScoringRvAdapter.AdvancedScoringListener {
                override fun getNewAdvancedScore(newScore: Pair<String, Double>) {
                    advancedScores!![newScore.first] = newScore.second

                    val nonZeroAdvancedScores = advancedScores!!.filterValues { it != 0.0 }.toList()
                    currentScore = if (nonZeroAdvancedScores.isNotEmpty())
                        nonZeroAdvancedScores.sumOf { it.second } / nonZeroAdvancedScores.size
                    else
                        0.0

                    binding.scoreInputEditText.setText(
                        when (scoreFormat) {
                            ScoreFormat.POINT_100 -> currentScore.toInt().toString()
                            ScoreFormat.POINT_10_DECIMAL -> currentScore.roundToOneDecimal()
                            else -> ""
                        }
                    )
                }
            })
            binding.scoreAdvancedScoringRecyclerView.adapter = advancedScoringAdapter
        }

        binding.scoreSetButton.clicks {
            val maxScore = when (scoreFormat) {
                ScoreFormat.POINT_5 -> 5.0
                ScoreFormat.POINT_3 -> 3.0
                ScoreFormat.POINT_10_DECIMAL, ScoreFormat.POINT_10 -> 10.0
                else -> 100.0
            }

            if (currentScore > maxScore)
                currentScore = maxScore

            listener?.getNewScore(currentScore, advancedScores)
            dismiss()
        }
    }

    private fun setUpScoreFormatSmileyLayout() {
        binding.apply {
            scoreSmileyLayout.show(true)
            val scoreList = listOf(1.0, 2.0, 3.0)
            val smileyList = listOf(scoreSmileySadIcon, scoreSmileyNeutralIcon, scoreSmileyHappyIcon)

            smileyList.forEach { smiley ->
                smiley.setOnClickListener {
                    val smileyIndex = smileyList.indexOf(smiley)
                    val selectedScore = scoreList[smileyIndex]
                    if (currentScore == selectedScore) {
                        currentScore = 0.0
                        handleSmileyTint(smileyList, null)
                    } else {
                        currentScore = selectedScore
                        handleSmileyTint(smileyList, smileyIndex)
                    }
                }
            }

            handleSmileyTint(smileyList, scoreList.indexOf(currentScore))
        }
    }

    private fun handleSmileyTint(smileyList: List<AppCompatImageView>, selectedIndex: Int?) {
        smileyList.forEachIndexed { index, appCompatImageView ->
            appCompatImageView.imageTintList = ColorStateList.valueOf(requireContext().getAttrValue(
                if (index == selectedIndex) R.attr.themePrimaryColor else R.attr.themeContentColor
            ))
        }
    }

    private fun setUpScoreFormatStarLayout() {
        binding.apply {
            scoreStarLayout.show(true)
            scoreStarRatingBar.rating = currentScore.toFloat()
            scoreStarRatingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
                currentScore = rating.toDouble()
            }
        }
    }

    private fun setUpScoreFormatPickerLayout() {
        binding.apply {
            scorePickerLayout.show(true)
            scorePickerNumberPicker.apply {
                minValue = 0
                maxValue = 10
                displayedValues = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
                wrapSelectorWheel = false
                value = currentScore.toInt()
            }
            scorePickerNumberPicker.setOnValueChangedListener { numberPicker, oldValue, newValue ->
                currentScore = newValue.toDouble()
            }
        }
    }

    private fun setUpScoreFormatInputDecimalLayout() {
        binding.apply {
            scoreInputLayout.show(true)
            scoreInputMaxScore.text = "/ 10"
            scoreInputEditText.apply {
                inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
                filters = arrayOf(InputFilter.LengthFilter(3))
                keyListener = DigitsKeyListener.getInstance("0123456789.")
                setText(if (currentScore == 0.0) "" else currentScore.roundToOneDecimal())
                setSelection(0, scoreInputEditText.text?.length ?: 0)
            }
            scoreInputEditText.addTextChangedListener {
                currentScore = it.toString().toDoubleOrNull() ?: 0.0
            }
            scoreInputEditText.requestFocus()
            openKeyboard()
        }
    }

    private fun setUpScoreFormatInput100Layout() {
        binding.apply {
            scoreInputLayout.show(true)
            scoreInputMaxScore.text = "/ 100"
            scoreInputEditText.apply {
                inputType = InputType.TYPE_CLASS_NUMBER
                filters = arrayOf(InputFilter.LengthFilter(3))
                keyListener = DigitsKeyListener.getInstance("0123456789")
                setText(if (currentScore == 0.0) "" else currentScore.roundToOneDecimal())
                setSelection(0, scoreInputEditText.text?.length ?: 0)
            }
            scoreInputEditText.addTextChangedListener {
                currentScore = it.toString().toDoubleOrNull() ?: 0.0
            }
            scoreInputEditText.requestFocus()
            openKeyboard()
        }
    }

    override fun setUpObserver() {
        // do nothing
    }

    override fun onDestroyView() {
        super.onDestroyView()
        advancedScoringAdapter = null
    }

    companion object {
        fun newInstance(
            scoreFormat: ScoreFormat,
            currentScore: Double,
            advancedScores: LinkedHashMap<String, Double>?,
            listener: BottomSheetScoreListener
        ) = BottomSheetScoreDialog().apply {
            this.scoreFormat = scoreFormat
            this.currentScore = currentScore
            this.advancedScores = advancedScores
            this.listener = listener
        }
    }

    interface BottomSheetScoreListener {
        fun getNewScore(newScore: Double, newAdvancedScores: LinkedHashMap<String, Double>?)
    }
}