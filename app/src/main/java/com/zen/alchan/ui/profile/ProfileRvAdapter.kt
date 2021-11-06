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
import com.zen.alchan.R
import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.databinding.*
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.pojo.ProfileItem
import com.zen.alchan.helper.utils.SpaceItemDecoration
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import kotlin.math.abs
import kotlin.math.roundToInt

class ProfileRvAdapter(
    private val context: Context,
    list: List<ProfileItem>,
    private val appSetting: AppSetting,
    private val listener: ProfileListener
) : BaseRecyclerViewAdapter<ProfileItem, ViewBinding>(list) {

    private var favoriteAnimeAdapter: FavoriteMediaRvAdapter? = null
    private var favoriteMangaAdapter: FavoriteMediaRvAdapter? = null
    private var favoriteCharacterAdapter: FavoriteCharacterRvAdapter? = null
    private var favoriteStaffAdapter: FavoriteStaffRvAdapter? = null

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
                favoriteCharacterAdapter = FavoriteCharacterRvAdapter(context, listOf(), listener.favoriteCharacterListener)
                setUpGridRecyclerView(view.listRecyclerView, favoriteCharacterAdapter)
                return FavoriteCharacterViewHolder(view)
            }
            ProfileItem.VIEW_TYPE_FAVORITE_STAFF -> {
                val view = LayoutTitleAndListBinding.inflate(inflater, parent, false)
                favoriteStaffAdapter = FavoriteStaffRvAdapter(context, listOf(), listener.favoriteStaffListener)
                setUpGridRecyclerView(view.listRecyclerView, favoriteStaffAdapter)
                return FavoriteStaffViewHolder(view)
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

    private fun setUpGridRecyclerView(recyclerView: RecyclerView, adapter: BaseRecyclerViewAdapter<*, *>?) {
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        recyclerView.adapter = adapter
    }

    inner class BioViewHolder(private val binding: LayoutProfileBioBinding) : ViewHolder(binding) {
        override fun bind(item: ProfileItem, index: Int) {
            binding.profileBioText.text = item.bio
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
}