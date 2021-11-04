package com.zen.alchan.ui.profile

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.text.color
import androidx.viewbinding.ViewBinding
import com.zen.alchan.R
import com.zen.alchan.databinding.LayoutProfileAffinityBinding
import com.zen.alchan.databinding.LayoutProfileBioBinding
import com.zen.alchan.databinding.LayoutProfileTendencyBinding
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.pojo.ProfileItem
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class ProfileRvAdapter(
    private val context: Context,
    list: List<ProfileItem>,
    private val width: Int,
    private val listener: ProfileListener
) : BaseRecyclerViewAdapter<ProfileItem, ViewBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            ProfileItem.VIEW_TYPE_BIO -> {
                val view = LayoutProfileBioBinding.inflate(inflater, parent, false)
                return BioViewHolder(view)
            }
            ProfileItem.VIEW_TYPE_AFFINITY -> {
                val view = LayoutProfileAffinityBinding.inflate(inflater, parent, false)
                return AffinityViewHolder(view)
            }
            ProfileItem.VIEW_TYPE_TENDENCY -> {
                val view = LayoutProfileTendencyBinding.inflate(inflater, parent, false)
                return TendencyViewHolder(view)
            }
            else -> {
                val view = LayoutProfileBioBinding.inflate(inflater, parent, false)
                return BioViewHolder(view)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].viewType
    }

    inner class BioViewHolder(private val binding: LayoutProfileBioBinding) : ViewHolder(binding) {
        override fun bind(item: ProfileItem, index: Int) {
            binding.profileBioText.text = item.user?.about
        }
    }

    inner class AffinityViewHolder(private val binding: LayoutProfileAffinityBinding) : ViewHolder(binding) {
        override fun bind(item: ProfileItem, index: Int) {
            binding.apply {
                val animeAffinity = item.affinity?.first
                val mangaAffinity = item.affinity?.second

                profileAnimeAffinityLayout.show(animeAffinity != null)
                profileAnimeAffinityText.text = "${animeAffinity?.roundToTwoDecimal()}%"

                profileAnimeAffinityPositiveBar.progress = if (animeAffinity != null && animeAffinity > 0)
                    animeAffinity.roundToInt()
                else
                    0

                profileAnimeAffinityNegativeBar.progress = if (animeAffinity != null && animeAffinity < 0)
                    abs(animeAffinity.roundToInt())
                else
                    0

                profileMangaAffinityLayout.show(mangaAffinity != null)
                profileMangaAffinityText.text = "${mangaAffinity?.roundToTwoDecimal()}%"

                profileMangaAffinityPositiveBar.progress = if (mangaAffinity != null && mangaAffinity > 0)
                    mangaAffinity.roundToInt()
                else
                    0

                profileMangaAffinityNegativeBar.progress = if (mangaAffinity != null && mangaAffinity < 0)
                    abs(mangaAffinity.roundToInt())
                else
                    0
            }
        }
    }

    inner class TendencyViewHolder(private val binding: LayoutProfileTendencyBinding) : ViewHolder(binding) {
        override fun bind(item: ProfileItem, index: Int) {
            binding.apply {
                val animeTendency = item.tendency?.first
                val mangaTendency = item.tendency?.second

                profileAnimeTendencyLayout.show(animeTendency != null)
                profileAnimeTendencyMostFavoriteGenre.text = getSpannedText(R.string.seems_to_love_x, animeTendency?.mostFavoriteGenres)
                profileAnimeTendencyLeastFavoriteGenre.text = getSpannedText(R.string.seems_to_hate_x, animeTendency?.leastFavoriteGenre)
                profileAnimeTendencyMostFavoriteTag.text = getSpannedText(R.string.tends_to_like_x, animeTendency?.mostFavoriteTags)
                profileAnimeTendencyMostFavoriteYear.text = getSpannedText(R.string.love_x_series, animeTendency?.mostFavoriteYear)
                profileAnimeTendencyStartYear.text = getSpannedText(R.string.first_recorded_watching_anime_in_x, animeTendency?.startYear)
                profileAnimeTendencyCompletedSeriesPercentage.text = getSpannedText(R.string.ends_up_completing_x_of_anime_started, animeTendency?.completedSeriesPercentage)

                profileMangaTendencyLayout.show(mangaTendency != null)
                profileMangaTendencyMostFavoriteGenre.text = getSpannedText(R.string.seems_to_love_x, mangaTendency?.mostFavoriteGenres)
                profileMangaTendencyLeastFavoriteGenre.text = getSpannedText(R.string.seems_to_hate_x, mangaTendency?.leastFavoriteGenre)
                profileMangaTendencyMostFavoriteTag.text = getSpannedText(R.string.tends_to_like_x, mangaTendency?.mostFavoriteTags)
                profileMangaTendencyMostFavoriteYear.text = getSpannedText(R.string.love_x_series, mangaTendency?.mostFavoriteYear)
                profileMangaTendencyStartYear.text = getSpannedText(R.string.first_recorded_reading_manga_in_x, mangaTendency?.startYear)
                profileMangaTendencyCompletedSeriesPercentage.text = getSpannedText(R.string.ends_up_completing_x_of_manga_started, mangaTendency?.completedSeriesPercentage)

                profileTendencyGapView.show(animeTendency != null && mangaTendency != null)
            }
        }

        private fun getSpannedText(@StringRes stringId: Int, value: String?): Spanned {
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