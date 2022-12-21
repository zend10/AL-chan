package com.zen.alchan.ui.profile

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.text.color
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.android.flexbox.FlexboxLayoutManager
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.databinding.*
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.pojo.Affinity
import com.zen.alchan.helper.pojo.ProfileItem
import com.zen.alchan.helper.utils.MarkdownSetup
import com.zen.alchan.helper.utils.MarkdownUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import kotlin.math.abs
import kotlin.math.roundToInt

class ProfileRvAdapter(
    private val context: Context,
    list: List<ProfileItem>,
    private val appSetting: AppSetting,
    private val width: Int,
    private val listener: ProfileListener
) : BaseRecyclerViewAdapter<ProfileItem, ViewBinding>(list) {

    private var favoriteAnimeAdapter: FavoriteMediaRvAdapter? = null
    private var favoriteMangaAdapter: FavoriteMediaRvAdapter? = null
    private var favoriteCharacterAdapter: FavoriteCharacterRvAdapter? = null
    private var favoriteStaffAdapter: FavoriteStaffRvAdapter? = null
    private var favoriteStudioRvAdapter: FavoriteStudioRvAdapter? = null

    private var markdownSetup: MarkdownSetup? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            ProfileItem.VIEW_TYPE_BIO -> {
                val view = LayoutTitleAndTextBinding.inflate(inflater, parent, false)
                markdownSetup = MarkdownUtil.getMarkdownSetup(context, width, null)
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
            ProfileItem.VIEW_TYPE_FAVORITE_ANIME -> {
                val view = LayoutTitleAndListBinding.inflate(inflater, parent, false)
                favoriteAnimeAdapter = FavoriteMediaRvAdapter(context, listOf(), MediaType.ANIME, appSetting, listener.favoriteMediaListener)
                setUpGridRecyclerView(view.listRecyclerView, favoriteAnimeAdapter)
                return FavoriteAnimeViewHolder((view))
            }
            ProfileItem.VIEW_TYPE_FAVORITE_MANGA -> {
                val view = LayoutTitleAndListBinding.inflate(inflater, parent, false)
                favoriteMangaAdapter = FavoriteMediaRvAdapter(context, listOf(), MediaType.MANGA, appSetting, listener.favoriteMediaListener)
                setUpGridRecyclerView(view.listRecyclerView, favoriteMangaAdapter)
                return FavoriteMangaViewHolder((view))
            }
            ProfileItem.VIEW_TYPE_FAVORITE_CHARACTER -> {
                val view = LayoutTitleAndListBinding.inflate(inflater, parent, false)
                favoriteCharacterAdapter = FavoriteCharacterRvAdapter(context, listOf(), appSetting, listener.favoriteCharacterListener)
                setUpGridRecyclerView(view.listRecyclerView, favoriteCharacterAdapter)
                return FavoriteCharacterViewHolder(view)
            }
            ProfileItem.VIEW_TYPE_FAVORITE_STAFF -> {
                val view = LayoutTitleAndListBinding.inflate(inflater, parent, false)
                favoriteStaffAdapter = FavoriteStaffRvAdapter(context, listOf(), appSetting, listener.favoriteStaffListener)
                setUpGridRecyclerView(view.listRecyclerView, favoriteStaffAdapter)
                return FavoriteStaffViewHolder(view)
            }
            ProfileItem.VIEW_TYPE_FAVORITE_STUDIO -> {
                val view = LayoutTitleAndListBinding.inflate(inflater, parent, false)
                favoriteStudioRvAdapter = FavoriteStudioRvAdapter(context, listOf(), listener.favoriteStudioListener)
                view.listRecyclerView.layoutManager = FlexboxLayoutManager(context)
                view.listRecyclerView.adapter = favoriteStudioRvAdapter
                return FavoriteStudioViewHolder(view)
            }
            ProfileItem.VIEw_TYPE_STATS -> {
                val view = LayoutProfileStatsBinding.inflate(inflater, parent, false)
                return StatsViewHolder(view)
            }
            else -> {
                val view = LayoutTitleAndTextBinding.inflate(inflater, parent, false)
                return BioViewHolder(view)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].viewType
    }

    private fun setUpGridRecyclerView(recyclerView: RecyclerView, adapter: BaseRecyclerViewAdapter<*, *>?) {
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        recyclerView.adapter = adapter
    }

    inner class BioViewHolder(private val binding: LayoutTitleAndTextBinding) : ViewHolder(binding) {
        override fun bind(item: ProfileItem, index: Int) {
            binding.apply {
                itemTitle.show(true)
                itemTitle.text = context.getString(R.string.bio)
                MarkdownUtil.applyMarkdown(context, markdownSetup, itemText, item.bio ?: "")
            }
        }
    }

    inner class AffinityViewHolder(private val binding: LayoutProfileAffinityBinding) : ViewHolder(binding) {
        override fun bind(item: ProfileItem, index: Int) {
            binding.apply {
                val animeAffinity = item.affinity?.first
                val mangaAffinity = item.affinity?.second

                when (animeAffinity?.status) {
                    Affinity.AFFINITY_STATUS_LOADING -> {
                        profileAnimeAffinityPositiveBar.show(false)
                        profileAnimeAffinityNegativeBar.show(false)
                        profileAnimeAffinityText.text = context.getString(R.string.calculating)
                    }
                    Affinity.AFFINITY_STATUS_COMPLETED -> {
                        profileAnimeAffinityPositiveBar.show(true)
                        profileAnimeAffinityNegativeBar.show(true)
                        profileAnimeAffinityText.text = "${animeAffinity.value?.roundToTwoDecimal()}%"
                    }
                    Affinity.AFFINITY_STATUS_FAILED -> {
                        profileAnimeAffinityPositiveBar.show(false)
                        profileAnimeAffinityNegativeBar.show(false)
                        profileAnimeAffinityText.text = context.getString(R.string.not_enough_shared_anime)
                    }
                }

                when (mangaAffinity?.status) {
                    Affinity.AFFINITY_STATUS_LOADING -> {
                        profileMangaAffinityPositiveBar.show(false)
                        profileMangaAffinityNegativeBar.show(false)
                        profileMangaAffinityText.text = context.getString(R.string.calculating)
                    }
                    Affinity.AFFINITY_STATUS_COMPLETED -> {
                        profileMangaAffinityPositiveBar.show(true)
                        profileMangaAffinityNegativeBar.show(true)
                        profileMangaAffinityText.text = "${mangaAffinity.value?.roundToTwoDecimal()}%"
                    }
                    Affinity.AFFINITY_STATUS_FAILED -> {
                        profileMangaAffinityPositiveBar.show(false)
                        profileMangaAffinityNegativeBar.show(false)
                        profileMangaAffinityText.text = context.getString(R.string.not_enough_shared_manga)
                    }
                }

                profileAnimeAffinityPositiveBar.progress = if (animeAffinity != null && (animeAffinity.value ?: 0.0) > 0.0)
                    animeAffinity.value?.roundToInt() ?: 0
                else
                    0

                profileAnimeAffinityNegativeBar.progress = if (animeAffinity != null && (animeAffinity.value ?: 0.0) < 0.0)
                    abs(animeAffinity.value?.roundToInt() ?: 0)
                else
                    0

                profileMangaAffinityPositiveBar.progress = if (mangaAffinity != null && (mangaAffinity.value ?: 0.0) > 0.0)
                    mangaAffinity.value?.roundToInt() ?: 0
                else
                    0

                profileMangaAffinityNegativeBar.progress = if (mangaAffinity != null && (mangaAffinity.value ?: 0.0) < 0.0)
                    abs(mangaAffinity.value?.roundToInt() ?: 0)
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

        private fun getSpannedText(@StringRes stringId: Int, value: String?): Spanned? {
            if (value == null)
                return null

            val text = context.getString(stringId, value)
            val textWithoutValue = text.split(value)
            val spannableStringBuilder = SpannableStringBuilder()
            textWithoutValue.forEachIndexed { index, it ->
                spannableStringBuilder.append(it)
                if (index == 0)
                    spannableStringBuilder.color(context.getAttrValue(R.attr.themeSecondaryColor)) { append(value) }
            }
            return spannableStringBuilder
        }
    }

    inner class FavoriteAnimeViewHolder(private val binding: LayoutTitleAndListBinding) : ViewHolder(binding) {
        override fun bind(item: ProfileItem, index: Int) {
            binding.apply {
                titleText.text = context.getString(R.string.favorite_anime)
                seeMoreText.clicks { listener.favoriteMediaListener.navigateToFavoriteMedia(MediaType.ANIME) }
                favoriteAnimeAdapter?.updateData(item.favoriteMedia ?: listOf())
            }
        }
    }

    inner class FavoriteMangaViewHolder(private val binding: LayoutTitleAndListBinding) : ViewHolder(binding) {
        override fun bind(item: ProfileItem, index: Int) {
            binding.apply {
                titleText.text = context.getString(R.string.favorite_manga)
                seeMoreText.clicks { listener.favoriteMediaListener.navigateToFavoriteMedia(MediaType.MANGA) }
                favoriteMangaAdapter?.updateData(item.favoriteMedia ?: listOf())
            }
        }
    }

    inner class FavoriteCharacterViewHolder(private val binding: LayoutTitleAndListBinding) : ViewHolder(binding) {
        override fun bind(item: ProfileItem, index: Int) {
            binding.apply {
                titleText.text = context.getString(R.string.favorite_characters)
                seeMoreText.clicks { listener.favoriteCharacterListener.navigateToFavoriteCharacter() }
                favoriteCharacterAdapter?.updateData(item.favoriteCharacters ?: listOf())
            }
        }
    }

    inner class FavoriteStaffViewHolder(private val binding: LayoutTitleAndListBinding) : ViewHolder(binding) {
        override fun bind(item: ProfileItem, index: Int) {
            binding.apply {
                titleText.text = context.getString(R.string.favorite_staff)
                seeMoreText.clicks { listener.favoriteStaffListener.navigateToFavoriteStaff() }
                favoriteStaffAdapter?.updateData(item.favoriteStaff ?: listOf())
            }
        }
    }

    inner class FavoriteStudioViewHolder(private val binding: LayoutTitleAndListBinding) : ViewHolder(binding) {
        override fun bind(item: ProfileItem, index: Int) {
            binding.apply {
                titleText.text = context.getString(R.string.favorite_studios)
                seeMoreText.clicks { listener.favoriteStudioListener.navigateToFavoriteStudio() }
                favoriteStudioRvAdapter?.updateData(item.favoriteStudios ?: listOf())
            }
        }
    }

    inner class StatsViewHolder(private val binding: LayoutProfileStatsBinding) : ViewHolder(binding) {
        override fun bind(item: ProfileItem, index: Int) {
            binding.apply {
                profileStatsTotalAnimeText.text = item.animeStats?.count?.toString()
                profileStatsEpisodesWatchedText.text = item.animeStats?.episodesWatched?.toString()
                profileStatsDaysWatchedText.text = ((item.animeStats?.minutesWatched?.toDouble() ?: 0.0) / 60.0 / 24.0).roundToTwoDecimal()
                profileStatsAnimeMeanScoreText.text = item.animeStats?.meanScore?.roundToTwoDecimal()

                profileStatsTotalMangaText.text = item.mangaStats?.count?.toString()
                profileStatsChaptersReadText.text = item.mangaStats?.chaptersRead?.toString()
                profileStatsVolumesReadText.text = item.mangaStats?.volumesRead?.toString()
                profileStatsMangaMeanScoreText.text = item.mangaStats?.meanScore?.roundToTwoDecimal()

                profileStatsDetailText.clicks { listener.statsListener.navigateToStatsDetail() }
            }
        }
    }
}