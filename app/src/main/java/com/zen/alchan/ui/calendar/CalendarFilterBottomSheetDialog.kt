package com.zen.alchan.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zen.alchan.R
import kotlinx.android.synthetic.main.bottomsheet_filter_calendar.view.*

class CalendarFilterBottomSheetDialog : BottomSheetDialogFragment() {

    interface CalendarFilterListener {
        fun passFilterData(showOnlyInList: Boolean, showOnlyCurrentSeason: Boolean, showAdultContent: Boolean)
    }

    private lateinit var listener: CalendarFilterListener

    companion object {
        const val SHOW_ONLY_IN_LIST = "showOnlyInList"
        const val SHOW_ONLY_CURRENT_SEASON = "showOnlyCurrentSeason"
        const val SHOW_ADULT_CONTENT = "showAdultContent"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dialogView = inflater.inflate(R.layout.bottomsheet_filter_calendar, container, false)

        if (!this::listener.isInitialized) {
            dismiss()
        }

        dialogView.showWatchingAndPlanningCheckBox.isChecked = arguments?.getBoolean(SHOW_ONLY_IN_LIST, false) == true
        dialogView.showCurrentSeasonCheckBox.isChecked = arguments?.getBoolean(SHOW_ONLY_CURRENT_SEASON, false) == true
        dialogView.showAdultContentCheckBox.isChecked = arguments?.getBoolean(SHOW_ADULT_CONTENT, false) == true

        dialogView.showWatchingAndPlanningText.setOnClickListener { dialogView.showWatchingAndPlanningCheckBox.performClick() }
        dialogView.showCurrentSeasonText.setOnClickListener { dialogView.showCurrentSeasonCheckBox.performClick() }
        dialogView.showAdultContentText.setOnClickListener { dialogView.showAdultContentCheckBox.performClick() }

        dialogView.filterApplyButton.setOnClickListener {
            listener.passFilterData(
                dialogView.showWatchingAndPlanningCheckBox.isChecked,
                dialogView.showCurrentSeasonCheckBox.isChecked,
                dialogView.showAdultContentCheckBox.isChecked
            )
            dismiss()
        }

        return dialogView
    }

    fun setListener(calendarFilterListener: CalendarFilterListener) {
        listener = calendarFilterListener
    }
}