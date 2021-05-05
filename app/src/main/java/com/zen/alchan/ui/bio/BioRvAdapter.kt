package com.zen.alchan.ui.bio

import android.content.Context
import android.content.res.ColorStateList
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.text.bold
import androidx.core.text.color
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.extensions.getAttrValue
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.pojo.BioItem
import com.zen.alchan.helper.pojo.Tendency
import com.zen.alchan.helper.utils.MarkdownUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import kotlinx.android.synthetic.main.layout_bio_about.view.*
import kotlinx.android.synthetic.main.layout_bio_tendency.view.*

class BioRvAdapter(
    private val context: Context,
    list: List<BioItem>
) : BaseRecyclerViewAdapter<BioItem>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            BioItem.VIEW_TYPE_AFFINITY -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_bio_affinity, parent, false)
                return AffinityViewHolder(view)
            }
            BioItem.VIEW_TYPE_ABOUT -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_bio_about, parent, false)
                return AboutViewHolder(view)
            }
            BioItem.VIEW_TYPE_TENDENCY -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_bio_tendency, parent, false)
                return TendencyViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_bio_about, parent, false)
                return AboutViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        when (holder) {
            is AffinityViewHolder -> holder.bind(item.animeAffinity, item.mangaAffinity)
            is AboutViewHolder -> holder.bind(context, item.bioText)
            is TendencyViewHolder -> holder.bind(context, item.animeTendency, item.mangaTendency)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].viewType
    }

    class AffinityViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(animeAffinity: Double?, mangaAffinity: Double?) {

        }
    }

    class AboutViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(context: Context, bioText: String) {
            MarkdownUtil.applyMarkdown(context, view.bioAboutText, bioText)
        }
    }

    class TendencyViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(context: Context, animeTendency: Tendency?, mangaTendency: Tendency?) {
            view.tendencyAnimeLayout.show(animeTendency != null)
            view.tendencyAnimeMostFavoriteGenre.text = getSpanned(context, R.string.seems_to_love_x, animeTendency?.mostFavoriteGenres)
            view.tendencyAnimeLeastFavoriteGenre.text = getSpanned(context, R.string.seems_to_hate_x, animeTendency?.leastFavoriteGenre)
            view.tendencyAnimeMostFavoriteTag.text = getSpanned(context, R.string.tends_to_like_x, animeTendency?.mostFavoriteTags)
            view.tendencyAnimeMostFavoriteYear.text = getSpanned(context, R.string.love_x_series, animeTendency?.mostFavoriteYear)
            view.tendencyAnimeStartYear.text = getSpanned(context, R.string.first_recorded_watching_anime_in_x, animeTendency?.startYear)
            view.tendencyAnimeCompletedSeriesPercentage.text = getSpanned(context, R.string.ends_up_completing_x_of_anime_started, animeTendency?.completedSeriesPercentage)

            view.tendencyMangaLayout.show(mangaTendency != null)
            view.tendencyMangaMostFavoriteGenre.text = getSpanned(context, R.string.seems_to_love_x, mangaTendency?.mostFavoriteGenres)
            view.tendencyMangaLeastFavoriteGenre.text = getSpanned(context, R.string.seems_to_hate_x, mangaTendency?.leastFavoriteGenre)
            view.tendencyMangaMostFavoriteTag.text = getSpanned(context, R.string.tends_to_like_x, mangaTendency?.mostFavoriteTags)
            view.tendencyMangaMostFavoriteYear.text = getSpanned(context, R.string.love_x_series, mangaTendency?.mostFavoriteYear)
            view.tendencyMangaStartYear.text = getSpanned(context, R.string.first_recorded_reading_manga_in_x, mangaTendency?.startYear)
            view.tendencyMangaCompletedSeriesPercentage.text = getSpanned(context, R.string.ends_up_completing_x_of_manga_started, mangaTendency?.completedSeriesPercentage)

            view.tendencyGapView.show(animeTendency != null && mangaTendency != null)
        }

        // this is a pretty limited function, will only work well with only 1 tag in string
        private fun getSpanned(context: Context, stringId: Int, value: String?): Spanned {
            val text = context.getString(stringId, value)
            val textWithoutValue = text.split(value ?: "")
            val spannableStringBuilder = SpannableStringBuilder()
            textWithoutValue.forEachIndexed { index, it ->
                spannableStringBuilder.append(it)
                if (index == 0)
                    spannableStringBuilder.color(context.getAttrValue(R.attr.themeNegativeColor)) { append(value) }
            }
            return spannableStringBuilder
        }
    }
}