package com.zen.alchan.ui.seasonal

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import com.zen.alchan.R
import com.zen.alchan.data.response.SeasonalAnime
import com.zen.alchan.helper.replaceUnderscore
import com.zen.alchan.helper.toStringDateFormat
import com.zen.alchan.ui.browse.media.overview.OverviewGenreRvAdapter
import kotlinx.android.synthetic.main.dialog_seasonal_detail.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaListStatus
import type.MediaSource

class SeasonalDialog : DialogFragment() {

    private val viewModel by viewModel<SeasonalDialogViewModel>()

    private lateinit var seasonalAnimeDetail: SeasonalAnime

    companion object {
        const val SEASONAL_ANIME_DETAIL = "seasonalAnimeDetail"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_seasonal_detail, null)

        seasonalAnimeDetail = viewModel.gson.fromJson(arguments?.getString(SEASONAL_ANIME_DETAIL), SeasonalAnime::class.java)

        view.seasonalTitleText.text = seasonalAnimeDetail.title?.userPreferred
        view.seasonalFormatText.text = seasonalAnimeDetail.format?.name.replaceUnderscore()

        if (!seasonalAnimeDetail.genres.isNullOrEmpty()) {
            view.seasonalGenreRecyclerView.adapter = OverviewGenreRvAdapter(seasonalAnimeDetail.genres!!, object : OverviewGenreRvAdapter.OverviewGenreListener {
                override fun passSelectedGenre(genre: String) { }
            })
        } else {
            view.seasonalGenreRecyclerView.visibility = View.GONE
        }

        view.seasonalStartDateText.text = "Starts on ${seasonalAnimeDetail.startDate?.toStringDateFormat()}"

        if (seasonalAnimeDetail.source != null) {
            view.seasonalSourceText.text = if (seasonalAnimeDetail.source == MediaSource.ORIGINAL) {
                getString(R.string.original_anime)
            } else {
                "Adapted from ${seasonalAnimeDetail.source?.name.replaceUnderscore()}"
            }
        } else {
            view.seasonalSourceText.visibility = View.GONE
        }

        view.seasonalScoreText.text = seasonalAnimeDetail.averageScore?.toString() ?: "0"
        view.seasonalFavoriteText.text = seasonalAnimeDetail.favourites?.toString() ?: "0"

        val statusDistribution = seasonalAnimeDetail.stats?.statusDistribution

        view.seasonalCountText.text = statusDistribution?.sumBy { it.amount }?.toString() ?: "0"

        view.seasonalWatchingCount.text = statusDistribution?.find { it.status == MediaListStatus.CURRENT }?.amount?.toString() ?: "0"
        view.seasonalPlanningCount.text = statusDistribution?.find { it.status == MediaListStatus.PLANNING }?.amount?.toString() ?: "0"
        view.seasonalCompletedCount.text = statusDistribution?.find { it.status == MediaListStatus.COMPLETED }?.amount?.toString() ?: "0"
        view.seasonalDroppedCount.text = statusDistribution?.find { it.status == MediaListStatus.DROPPED }?.amount?.toString() ?: "0"
        view.seasonalPausedCount.text = statusDistribution?.find { it.status == MediaListStatus.PAUSED }?.amount?.toString() ?: "0"

        val spanned = HtmlCompat.fromHtml(seasonalAnimeDetail.description ?: getString(R.string.no_description), HtmlCompat.FROM_HTML_MODE_LEGACY)
        view.seasonalDescriptionText.text = spanned

        builder.setView(view)
        return builder.create()
    }
}