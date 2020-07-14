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
import kotlin.math.round

class ChartDialog : DialogFragment() {

    private val viewModel by viewModel<ChartViewModel>()

    private lateinit var pieDataSet: PieDataSet
    private lateinit var barDataSet: BarDataSet
    private lateinit var lineEntries: List<Entry>

    private var xAxisLabel = ArrayList<String>()

    companion object {
        const val PIE_ENTRIES = "pieEntries"
        const val BAR_ENTRIES = "barEntries"
        const val LINE_ENTRIES = "lineEntries"

        const val XAXIS_LABEL = "xAxisLabel"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        val dialogView = activity!!.layoutInflater.inflate(R.layout.dialog_chart, null, false)

        if (arguments?.getString(PIE_ENTRIES) != null) {
            pieDataSet = viewModel.gson.fromJson(arguments?.getString(PIE_ENTRIES), PieDataSet::class.java)
        }

        if (arguments?.getString(BAR_ENTRIES) != null) {
            barDataSet = viewModel.gson.fromJson(arguments?.getString(BAR_ENTRIES), BarDataSet::class.java)
        }

        if (arguments?.getString(LINE_ENTRIES) != null) {
            lineEntries = viewModel.gson.fromJson(arguments?.getString(LINE_ENTRIES), genericType<List<Entry>>())
        }

        if (arguments?.getStringArrayList(XAXIS_LABEL) != null) {
            xAxisLabel = ArrayList(arguments?.getStringArrayList(XAXIS_LABEL)?.filterNotNull() ?: listOf())
        }

        if (this::pieDataSet.isInitialized) {
            try {
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

        if (this::barDataSet.isInitialized) {
            try {
                val defaultValueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return value.toInt().toString()
                    }
                }

                val barData = BarData(barDataSet)
                barData.setValueTextColor(AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeContentColor))
                barData.barWidth = 3F
                barData.setValueFormatter(defaultValueFormatter)

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
                    setLabelCount(barDataSet.entryCount, true)
                    textColor = AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeContentColor)

                    if (!xAxisLabel.isNullOrEmpty()) {
                        valueFormatter = object : ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                val index = round(value / 10.0) - 1
                                if (index < xAxisLabel.size) {
                                    return xAxisLabel[index.toInt()]
                                }
                                return ""
                            }
                        }
                    }
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

        if (this::lineEntries.isInitialized) {
            try {
                val defaultValueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return value.toInt().toString()
                    }
                }

                val lineDataSet = LineDataSet(lineEntries, "Line Chart")
                lineDataSet.color = AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeSecondaryColor)
                lineDataSet.setDrawFilled(true)
                lineDataSet.fillDrawable = ContextCompat.getDrawable(activity!!, R.drawable.line_chart_fill)

                val lineData = LineData(lineDataSet)
                lineData.setValueTextColor(AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeContentColor))
                lineData.setValueFormatter(defaultValueFormatter)

                dialogView.statsLineChart.axisLeft.apply {
                    setDrawGridLines(false)
                    setDrawAxisLine(false)
                    setDrawLabels(false)
                }

                dialogView.statsLineChart.axisRight.apply {
                    setDrawGridLines(false)
                    setDrawAxisLine(false)
                    setDrawLabels(false)
                }

                dialogView.statsLineChart.xAxis.apply {
                    setDrawGridLines(false)
                    position = XAxis.XAxisPosition.BOTTOM
                    granularity = 1F
                    textColor = AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeContentColor)

                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return value.toInt().toString()
                        }
                    }
                }

                dialogView.statsLineChart.apply {
                    description.isEnabled = false
                    legend.isEnabled = false
                    data = lineData
                    invalidate()
                }

                dialogView.statsLineChart.visibility = View.VISIBLE
            } catch (e: Exception) {
                DialogUtility.showToast(activity, e.localizedMessage)
                dismiss()
            }
        }

        builder.setView(dialogView)
        return builder.create()
    }
}