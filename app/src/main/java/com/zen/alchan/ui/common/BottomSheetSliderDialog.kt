package com.zen.alchan.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.android.material.slider.RangeSlider
import com.zen.alchan.R
import com.zen.alchan.databinding.DialogBottomSheetSliderBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.ui.base.BaseDialogFragment

class BottomSheetSliderDialog : BaseDialogFragment<DialogBottomSheetSliderBinding>() {

    private var listener: BottomSheetSliderListener? = null

    private var sliderMinValue = 0
    private var sliderMaxValue = 0
    private var minValue = 0
    private var maxValue = 0

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogBottomSheetSliderBinding {
        return DialogBottomSheetSliderBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            if (minValue < sliderMinValue)
                minValue = sliderMinValue

            if (maxValue < sliderMaxValue)
                maxValue = sliderMaxValue

            dialogRangeSlider.valueFrom = sliderMinValue.toFloat()
            dialogRangeSlider.valueTo = sliderMaxValue.toFloat()
            dialogRangeSlider.values = listOf(minValue.toFloat(), maxValue.toFloat())

            updateRangeText()

            dialogRangeSlider.addOnChangeListener { slider, value, fromUser ->
                if (!fromUser)
                    return@addOnChangeListener

                if (slider.activeThumbIndex == MIN_THUMB)
                    minValue = value.toInt()

                if (slider.activeThumbIndex == MAX_THUMB)
                    maxValue = value.toInt()

                updateRangeText()
            }

            dialogSaveButton.clicks {
                listener?.getNewValues(minValue, maxValue)
            }
        }
    }

    override fun setUpObserver() {

    }
    
    private fun updateRangeText() {
        if (minValue == sliderMinValue && maxValue == sliderMaxValue)
            binding.dialogRangeText.text = ""
        else
            binding.dialogRangeText.text = getString(R.string.range_min_to_max, minValue, maxValue)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listener = null
    }

    companion object {
        private const val MIN_THUMB = 0
        private const val MAX_THUMB = 1

        fun newInstance(sliderMinValue: Int, sliderMaxValue: Int, minValue: Int, maxValue: Int) = BottomSheetSliderDialog().apply {
            this.sliderMinValue = sliderMinValue
            this.sliderMaxValue = sliderMaxValue
            this.minValue = minValue
            this.maxValue = maxValue
        }
    }

    interface BottomSheetSliderListener {
        fun getNewValues(minValue: Int, maxValue: Int)
    }
}