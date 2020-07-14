package com.zen.alchan.ui.browse.media.stats


import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*

import com.zen.alchan.R
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.pojo.StatusDistributionItem
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.browse.media.MediaFragment
import com.zen.alchan.ui.common.ChartDialog
import kotlinx.android.synthetic.main.fragment_media_stats.*
import kotlinx.android.synthetic.main.layout_loading.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class MediaStatsFragment : BaseFragment() {

    private val viewModel by viewModel<MediaStatsViewModel>()

    private var mediaData: MediaStatsQuery.Media? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_media_stats, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.mediaId = arguments?.getInt(MediaFragment.MEDIA_ID)

        setupObserver()
    }

    private fun setupObserver() {
        viewModel.mediaStatsData.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                    viewModel.mediaData = it.data?.media
                    mediaData = it.data?.media
                    initLayout()
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                }
            }
        })

        if (viewModel.mediaData == null) {
            viewModel.getMediaStats()
        } else {
            mediaData = viewModel.mediaData
            initLayout()
        }
    }

    private fun initLayout() {
        handlePerformance()
        handleRankings()
        handleStatusDistribution()
        handleScoreDistribution()
    }

    private fun handlePerformance() {
        mediaAvgScoreText.text = "${mediaData?.averageScore?.toString() ?: "0"}%"
        mediaMeanScoreText.text = "${mediaData?.meanScore?.toString() ?: "0"}%"
        mediaPopularityText.text = mediaData?.popularity?.toString() ?: "0"
        mediaFavoritesText.text = mediaData?.favourites?.toString() ?: "0"
    }

    private fun handleRankings() {
        if (mediaData?.rankings?.isNullOrEmpty() == true) {
            mediaStatsRankingLayout.visibility = View.GONE
            return
        }

        mediaStatsRankingLayout.visibility = View.VISIBLE
        mediaStatsRankingRecyclerView.adapter = MediaStatsRankingRvAdapter(mediaData?.rankings!!)
    }

    private fun handleStatusDistribution() {
        if (mediaData?.stats?.statusDistribution?.isNullOrEmpty() == true) {
            mediaStatsStatusLayout.visibility = View.GONE
            return
        }

        mediaStatsStatusLayout.visibility = View.VISIBLE

        val statusDistributionList = ArrayList<StatusDistributionItem>()

        val pieEntries = ArrayList<PieEntry>()
        mediaData?.stats?.statusDistribution?.forEach {
            val pieEntry = PieEntry(it?.amount!!.toFloat(), it.status?.toString())
            pieEntries.add(pieEntry)
            statusDistributionList.add(StatusDistributionItem(it.status?.name!!, it.amount, Constant.STATUS_COLOR_LIST[statusDistributionList.size]))
        }

        if (!viewModel.showStatsAutomatically) {
            mediaStatsStatusPieChart.visibility = View.GONE
            mediaStatsStatusShowButton.visibility = View.VISIBLE

            mediaStatsStatusShowButton.setOnClickListener {
                val dialog = ChartDialog()
                val bundle = Bundle()
                bundle.putString(ChartDialog.PIE_ENTRIES, viewModel.gson.toJson(pieEntries))
                dialog.arguments = bundle
                dialog.show(childFragmentManager, null)
            }
        } else {
            try {
                val pieDataSet = PieDataSet(pieEntries, "Score Distribution")
                pieDataSet.colors = Constant.STATUS_COLOR_LIST

                val pieData = PieData(pieDataSet)
                pieData.setDrawValues(false)

                mediaStatsStatusPieChart.setHoleColor(ContextCompat.getColor(activity!!, android.R.color.transparent))
                mediaStatsStatusPieChart.setDrawEntryLabels(false)
                mediaStatsStatusPieChart.setTouchEnabled(false)
                mediaStatsStatusPieChart.description.isEnabled = false
                mediaStatsStatusPieChart.legend.isEnabled = false
                mediaStatsStatusPieChart.data = pieData
                mediaStatsStatusPieChart.invalidate()
            } catch (e: Exception) {
                DialogUtility.showToast(activity, e.localizedMessage)
            }

            mediaStatsStatusPieChart.visibility = View.VISIBLE
            mediaStatsStatusShowButton.visibility = View.GONE
        }

        mediaStatsStatusRecyclerView.adapter = MediaStatsStatusRvAdapter(activity!!, statusDistributionList)
    }

    private fun handleScoreDistribution() {
        if (mediaData?.stats?.scoreDistribution?.isNullOrEmpty() == true) {
            mediaStatsScoreLayout.visibility = View.GONE
            return
        }

        mediaStatsScoreLayout.visibility = View.VISIBLE

        val barEntries = ArrayList<BarEntry>()
        mediaData?.stats?.scoreDistribution?.forEach {
            val barEntry = BarEntry(it?.score?.toFloat()!!, it.amount?.toFloat()!!)
            barEntries.add(barEntry)
        }

        if (!viewModel.showStatsAutomatically) {
            mediaStatsScoreBarChart.visibility = View.GONE
            mediaStatsScoreShowButton.visibility = View.VISIBLE

            mediaStatsScoreShowButton.setOnClickListener {
                val dialog = ChartDialog()
                val bundle = Bundle()
                bundle.putString(ChartDialog.BAR_ENTRIES, viewModel.gson.toJson(barEntries))
                dialog.arguments = bundle
                dialog.show(childFragmentManager, null)
            }
        } else {
            try {
                val barDataSet = BarDataSet(barEntries, "Score Distribution")
                barDataSet.colors = Constant.SCORE_COLOR_LIST

                val barData = BarData(barDataSet)
                barData.setValueTextColor(AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeContentColor))
                barData.barWidth = 3F

                mediaStatsScoreBarChart.axisLeft.setDrawGridLines(false)
                mediaStatsScoreBarChart.axisLeft.setDrawAxisLine(false)
                mediaStatsScoreBarChart.axisLeft.setDrawLabels(false)

                mediaStatsScoreBarChart.axisRight.setDrawGridLines(false)
                mediaStatsScoreBarChart.axisRight.setDrawAxisLine(false)
                mediaStatsScoreBarChart.axisRight.setDrawLabels(false)

                mediaStatsScoreBarChart.xAxis.setDrawGridLines(false)
                mediaStatsScoreBarChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                mediaStatsScoreBarChart.xAxis.setLabelCount(barEntries.size, true)
                mediaStatsScoreBarChart.xAxis.textColor = AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeContentColor)

                mediaStatsScoreBarChart.setTouchEnabled(false)
                mediaStatsScoreBarChart.description.isEnabled = false
                mediaStatsScoreBarChart.legend.isEnabled = false
                mediaStatsScoreBarChart.data = barData
                mediaStatsScoreBarChart.invalidate()
            } catch (e: Exception) {
                DialogUtility.showToast(activity, e.localizedMessage)
            }

            mediaStatsScoreBarChart.visibility = View.VISIBLE
            mediaStatsScoreShowButton.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaStatsRankingRecyclerView.adapter = null
        mediaStatsStatusRecyclerView.adapter = null
    }
}
