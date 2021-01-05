package com.zen.alchan.ui.browse.character

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zen.alchan.R
import com.zen.alchan.helper.replaceUnderscore
import kotlinx.android.synthetic.main.bottomsheet_filter_character_media.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaFormat
import type.MediaSort
import java.util.*
import kotlin.collections.ArrayList

class FilterCharacterMediaBottomSheet : BottomSheetDialogFragment() {

    interface FilterCharacterMediaListener {
        fun passFilterData(sortBy: MediaSort?, orderByDescending: Boolean, selectedFormats: ArrayList<MediaFormat>?, showOnlyOnList: Boolean?)
    }

    private val viewModel by viewModel<FilterCharacterMediaViewModel>()
    private lateinit var listener: FilterCharacterMediaListener

    private lateinit var dialogView: View

    companion object {
        const val SORT_BY = "sortBy"
        const val ORDER_BY_DESCENDING = "orderByDescending"
        const val SELECTED_FORMATS = "selectedFormats"
        const val SHOW_ONLY_ON_LIST = "showOnlyOnList"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialogView = inflater.inflate(R.layout.bottomsheet_filter_character_media, container, false)

        if (!this::listener.isInitialized) {
            dismiss()
        }

        if (arguments?.getString(SORT_BY) != null) {
            viewModel.sortBy = MediaSort.valueOf(arguments?.getString(SORT_BY) ?: MediaSort.POPULARITY.name)
        }

        viewModel.orderByDescending = arguments?.getBoolean(ORDER_BY_DESCENDING, true) ?: true

        if (arguments?.getString(SELECTED_FORMATS) != null) {
            viewModel.selectedFormats = viewModel.deserializeSelectedFormats(arguments?.getString(SELECTED_FORMATS))
        }

        if (arguments?.containsKey(SHOW_ONLY_ON_LIST) == true) {
            viewModel.showOnlyOnList = arguments?.getBoolean(SHOW_ONLY_ON_LIST)
        }

        initLayout()

        return dialogView
    }

    private fun initLayout() {
        if (viewModel.sortBy == null) {
            dialogView.filterResetButton.visibility = View.GONE
        } else {
            dialogView.filterResetButton.visibility = View.VISIBLE
        }

        dialogView.sortText.text = if (viewModel.sortBy == null) {
            getString(R.string.popularity).toUpperCase(Locale.US)
        } else {
            getString(viewModel.mediaSortMap[viewModel.sortBy ?: MediaSort.POPULARITY] ?: R.string.popularity).toUpperCase(Locale.US)
        }
        dialogView.sortLayout.setOnClickListener {
            val stringArray = viewModel.mediaSortMap.map { sort -> getString(sort.value).toUpperCase(Locale.US) }.toTypedArray()
            AlertDialog.Builder(requireContext())
                .setItems(stringArray) { _, which ->
                    viewModel.sortBy = viewModel.mediaSortMap.toList()[which].first
                    dialogView.sortText.text = stringArray[which]
                }
                .show()
        }

        dialogView.orderByText.text = getString(if (viewModel.orderByDescending) {
            R.string.descending
        } else {
            R.string.ascending
        }).toUpperCase(Locale.US)
        dialogView.orderByLayout.setOnClickListener {
            val stringArray = viewModel.orderByList.map { order -> getString(order).toUpperCase(Locale.US) }.toTypedArray()
            AlertDialog.Builder(requireContext())
                .setItems(stringArray) { _, which ->
                    // 0 is index for ascending
                    viewModel.orderByDescending = which != 0
                    dialogView.orderByText.text = stringArray[which]
                }
                .show()
        }

        dialogView.filterFormatText.text = if (viewModel.selectedFormats.isNullOrEmpty()) {
            "-"
        } else {
            viewModel.selectedFormats?.joinToString { it.name.replaceUnderscore() }
        }
        dialogView.filterFormatLayout.setOnClickListener {
            val stringBooleanPair = viewModel.getMediaFormatArrayPair()
            AlertDialog.Builder(requireContext())
                .setMultiChoiceItems(stringBooleanPair.first, stringBooleanPair.second) { _, index, isChecked ->
                    viewModel.passMediaFormatFilterValue(index, isChecked)
                    dialogView.filterFormatText.text = if (viewModel.selectedFormats.isNullOrEmpty()) {
                        "-"
                    } else {
                        viewModel.selectedFormats?.joinToString { it.name.replaceUnderscore() }
                    }
                }
                .setPositiveButton(R.string.close, null)
                .show()
        }

        dialogView.filterHideMediaCheckBox.isChecked = viewModel.showOnlyOnList == false
        dialogView.filterHideMediaCheckBox.setOnClickListener {
            if (dialogView.filterHideMediaCheckBox.isChecked) {
                viewModel.showOnlyOnList = false
                dialogView.filterOnlyShowMediaCheckBox.isChecked = false
            } else {
                viewModel.showOnlyOnList = null
            }
        }
        dialogView.filterHideMediaText.setOnClickListener {
            dialogView.filterHideMediaCheckBox.performClick()
        }

        dialogView.filterOnlyShowMediaCheckBox.isChecked = viewModel.showOnlyOnList == true
        dialogView.filterOnlyShowMediaCheckBox.setOnClickListener {
            if (dialogView.filterOnlyShowMediaCheckBox.isChecked) {
                viewModel.showOnlyOnList = true
                dialogView.filterHideMediaCheckBox.isChecked = false
            } else {
                viewModel.showOnlyOnList = null
            }
        }
        dialogView.filterOnlyShowMediaText.setOnClickListener {
            dialogView.filterOnlyShowMediaCheckBox.performClick()
        }

        dialogView.filterApplyButton.setOnClickListener {
            if (viewModel.sortBy == null) {
                viewModel.sortBy = MediaSort.POPULARITY
            }
            listener.passFilterData(viewModel.sortBy, viewModel.orderByDescending, viewModel.selectedFormats, viewModel.showOnlyOnList)
            dismiss()
        }

        dialogView.filterResetButton.setOnClickListener {
            listener.passFilterData(null, true, null, null)
            dismiss()
        }
    }

    fun setListener(filterCharacterMediaListener: FilterCharacterMediaListener) {
        listener = filterCharacterMediaListener
    }
}