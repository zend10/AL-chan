package com.zen.alchan.ui.common

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.gson.Gson
import com.zen.alchan.R
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.genericType
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import kotlinx.android.synthetic.main.dialog_chart.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.github.mikephil.charting.data.BarDataSet as BarDataSet1

class ChartDialog : DialogFragment() {

    private val viewModel by viewModel<ChartViewModel>()

    private val pieEntries = ArrayList<PieEntry>()
    private val barEntries = ArrayList<BarEntry>()

    companion object {
        const val PIE_ENTRIES = "pieEntries"
        const val BAR_ENTRIES = "barEntries"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        val dialogView = activity!!.layoutInflater.inflate(R.layout.dialog_chart, null, false)

        if (arguments?.getString(PIE_ENTRIES) != null) {
            pieEntries.addAll(viewModel.gson.fromJson(arguments?.getString(PIE_ENTRIES), genericType<List<PieEntry>>()))
        }

        if (arguments?.getString(BAR_ENTRIES) != null) {
            barEntries.addAll(viewModel.gson.fromJson(arguments?.getString(BAR_ENTRIES), genericType<List<BarEntry>>()))
        }

        if (!pieEntries.isNullOrEmpty()) {
            try {
                val pieDataSet = PieDataSet(pieEntries, "Pie Chart")
                pieDataSet.colors = Constant.STATUS_COLOR_LIST

                val pieData = PieData(pieDataSet)
                pieData.setDrawValues(false)

                dialogView.statsPieChart.apply {
                    setHoleColor(ContextCompat.getColor(activity!!, android.R.color.transparent))
                    setDrawEntryLabels(false)
                    setTouchEnabled(false)
                    description.isEnabled = false
                    legend.isEnabled = false
                    data = pieData
                    invalidate()
                }

                dialogView.statsPieChart.visibility = View.VISIBLE
            } catch (e: Exception) {
                DialogUtility.showToast(activity, e.localizedMessage)
                dismiss()
            }
        }

        if (!barEntries.isNullOrEmpty()) {
            try {
                val valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return value.toInt().toString()
                    }
                }

                val barDataSet = BarDataSet1(barEntries, "Bar Chart")
                barDataSet.colors = Constant.SCORE_COLOR_LIST

                val barData = BarData(barDataSet)
                barData.setValueTextColor(AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeContentColor))
                barData.barWidth = 3F
                barData.setValueFormatter(valueFormatter)

                dialogView.statsBarChart.axisLeft.apply {
                    setDrawGridLines(false)
                    setDrawAxisLine(false)
                    setDrawLabels(false)
                }

                dialogView.statsBarChart.axisRight.apply {
                    setDrawGridLines(false)
                    setDrawAxisLine(false)
                    setDrawLabels(false)
                }

                dialogView.statsBarChart.xAxis.apply {
                    setDrawGridLines(false)
                    position = XAxis.XAxisPosition.BOTTOM
                    setLabelCount(barEntries.size, true)
                    textColor = AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeContentColor)
                }

                dialogView.statsBarChart.apply {
                    setTouchEnabled(false)
                    description.isEnabled = false
                    legend.isEnabled = false
                    data = barData
                    invalidate()
                }

                dialogView.statsBarChart.visibility = View.VISIBLE
            } catch (e: Exception) {
                DialogUtility.showToast(activity, e.localizedMessage)
                dismiss()
            }
        }

        builder.setView(dialogView)
        return builder.create()
    }
}