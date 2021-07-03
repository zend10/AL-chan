package com.zen.alchan.ui.bio

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.color
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.zen.alchan.R
import com.zen.alchan.databinding.LayoutBioAboutBinding
import com.zen.alchan.databinding.LayoutBioAffinityBinding
import com.zen.alchan.databinding.LayoutBioTendencyBinding
import com.zen.alchan.helper.extensions.getAttrValue
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.pojo.BioItem
import com.zen.alchan.helper.pojo.Tendency
import com.zen.alchan.helper.utils.MarkdownUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class BioRvAdapter(
    private val context: Context,
    list: List<BioItem>
) : BaseRecyclerViewAdapter<BioItem, ViewBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            BioItem.VIEW_TYPE_AFFINITY -> {
                val view = LayoutBioAffinityBinding.inflate(inflater, parent, false)
                return AffinityViewHolder(view)
            }
            BioItem.VIEW_TYPE_ABOUT -> {
                val view = LayoutBioAboutBinding.inflate(inflater, parent, false)
                return AboutViewHolder(view)
            }
            BioItem.VIEW_TYPE_TENDENCY -> {
                val view = LayoutBioTendencyBinding.inflate(inflater, parent, false)
                return TendencyViewHolder(view)
            }
            else -> {
                val view = LayoutBioAboutBinding.inflate(inflater, parent, false)
                return AboutViewHolder(view)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].viewType
    }

    inner class AffinityViewHolder(private val binding: LayoutBioAffinityBinding) : ViewHolder(binding) {
        override fun bind(item: BioItem, index: Int) {

        }
    }

    inner class AboutViewHolder(private val binding: LayoutBioAboutBinding) : ViewHolder(binding) {
        override fun bind(item: BioItem, index: Int) {
            MarkdownUtil.applyMarkdown(context, binding.bioAboutText, item.bioText)
        }
    }

    inner class TendencyViewHolder(private val binding: LayoutBioTendencyBinding) : ViewHolder(binding) {
        override fun bind(item: BioItem, index: Int) {
            val animeTendency = item.animeTendency
            val mangaTendency = item.mangaTendency
            binding.apply {
                tendencyAnimeLayout.show(animeTendency != null)
                tendencyAnimeMostFavoriteGenre.text = getSpanned(context, R.string.seems_to_love_x, animeTendency?.mostFavoriteGenres)
                tendencyAnimeLeastFavoriteGenre.text = getSpanned(context, R.string.seems_to_hate_x, animeTendency?.leastFavoriteGenre)
                tendencyAnimeMostFavoriteTag.text = getSpanned(context, R.string.tends_to_like_x, animeTendency?.mostFavoriteTags)
                tendencyAnimeMostFavoriteYear.text = getSpanned(context, R.string.love_x_series, animeTendency?.mostFavoriteYear)
                tendencyAnimeStartYear.text = getSpanned(context, R.string.first_recorded_watching_anime_in_x, animeTendency?.startYear)
                tendencyAnimeCompletedSeriesPercentage.text = getSpanned(context, R.string.ends_up_completing_x_of_anime_started, animeTendency?.completedSeriesPercentage)

                tendencyMangaLayout.show(mangaTendency != null)
                tendencyMangaMostFavoriteGenre.text = getSpanned(context, R.string.seems_to_love_x, mangaTendency?.mostFavoriteGenres)
                tendencyMangaLeastFavoriteGenre.text = getSpanned(context, R.string.seems_to_hate_x, mangaTendency?.leastFavoriteGenre)
                tendencyMangaMostFavoriteTag.text = getSpanned(context, R.string.tends_to_like_x, mangaTendency?.mostFavoriteTags)
                tendencyMangaMostFavoriteYear.text = getSpanned(context, R.string.love_x_series, mangaTendency?.mostFavoriteYear)
                tendencyMangaStartYear.text = getSpanned(context, R.string.first_recorded_reading_manga_in_x, mangaTendency?.startYear)
                tendencyMangaCompletedSeriesPercentage.text = getSpanned(context, R.string.ends_up_completing_x_of_manga_started, mangaTendency?.completedSeriesPercentage)

                tendencyGapView.show(animeTendency != null && mangaTendency != null)
            }
        }

        // this is a pretty limited function, will only work well with only 1 tag in string
        private fun getSpanned(context: Context, stringId: Int, value: String?): Spanned {
            val text = context.getString(stringId, value)
            val textWithoutValue = text.split(value ?: "")
            val spannableStringBuilder = SpannableStringBuilder()
            textWithoutValue.forEachIndexed { index, it ->
                spannableStringBuilder.append(it)
                if (index == 0)
                    spannableStringBuilder.color(context.getAttrValue(R.attr.themeSecondaryColor)) { append(value) }
            }
            return spannableStringBuilder
        }
    }
}