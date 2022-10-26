package com.zen.alchan.ui.search

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.*
import com.zen.alchan.databinding.ListLoadingBinding
import com.zen.alchan.databinding.ListSearchBinding
import com.zen.alchan.helper.enums.SearchCategory
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.pojo.SearchItem
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.helper.utils.TimeUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import type.MediaType

class SearchRvAdapter(
    private val context: Context,
    list: List<SearchItem?>,
    private val appSetting: AppSetting,
    private val listener: SearchListener
) : BaseRecyclerViewAdapter<SearchItem?, ViewBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_LOADING -> {
                val view = ListLoadingBinding.inflate(inflater, parent, false)
                LoadingViewHolder(view)
            }
            else -> {
                val view = ListSearchBinding.inflate(inflater, parent, false)
                ItemViewHolder(view)
            }
        }
    }

    inner class ItemViewHolder(private val binding: ListSearchBinding) : ViewHolder(binding) {
        override fun bind(item: SearchItem?, index: Int) {
            if (item == null)
                return

            binding.apply {
                when (item.searchCategory) {
                    SearchCategory.ANIME, SearchCategory.MANGA -> {
                        ImageUtil.loadImage(context, item.media.getCoverImage(appSetting), searchImage)
                        searchTitle.text = item.media.getTitle(appSetting)
                        searchMediaExtraInfoLayout.show(true)
                        searchMediaYearText.text = item.media.startDate?.year?.toString() ?: "TBA"
                        searchMediaFormatText.text = item.media.getFormattedMediaFormat(true)
                        searchMediaInfoDividerIcon.show(true)
                        searchStatsLayout.show(true)
                        searchScoreText.show(true)
                        searchFavouriteText.show(true)
                        searchScoreText.text = item.media.averageScore.toString()
                        searchFavouriteText.text = item.media.favourites.toString()
                        searchStatsDivider.show(true)
                        searchMediaListStatusLayout.show(item.media.mediaListEntry != null)
                        val statusColor = item.media.mediaListEntry?.status?.getColor()?.let {
                            Color.parseColor(it)
                        } ?: context.getThemeSecondaryColor()
                        searchMediaListStatusIcon.imageTintList = ColorStateList.valueOf(statusColor)
                        searchMediaListStatusText.text = item.media.mediaListEntry?.status?.getString(item.media.type?.getMediaType() ?: com.zen.alchan.helper.enums.MediaType.ANIME)?.uppercase()
                        searchMediaListStatusText.setTextColor(statusColor)
                        root.clicks { listener.navigateToMedia(item.media) }
                    }
                    SearchCategory.CHARACTER -> {
                        ImageUtil.loadImage(context, item.character.getImage(appSetting), searchImage)
                        searchTitle.text = item.character.name.userPreferred
                        searchMediaExtraInfoLayout.show(false)
                        searchStatsLayout.show(true)
                        searchScoreText.show(false)
                        searchFavouriteText.show(true)
                        searchFavouriteText.text = item.character.favourites.toString()
                        searchStatsDivider.show(false)
                        searchMediaListStatusLayout.show(false)
                        root.clicks { listener.navigateToCharacter(item.character) }
                    }
                    SearchCategory.STAFF -> {
                        ImageUtil.loadImage(context, item.staff.getImage(appSetting), searchImage)
                        searchTitle.text = item.staff.name.userPreferred
                        searchMediaExtraInfoLayout.show(false)
                        searchStatsLayout.show(true)
                        searchScoreText.show(false)
                        searchFavouriteText.show(true)
                        searchFavouriteText.text = item.staff.favourites.toString()
                        searchStatsDivider.show(false)
                        searchMediaListStatusLayout.show(false)
                        root.clicks { listener.navigateToStaff(item.staff) }
                    }
                    SearchCategory.STUDIO -> {
                        ImageUtil.loadImage(context, item.studio.media.nodes.firstOrNull()?.getCoverImage(appSetting) ?: "", searchImage)
                        searchTitle.text = item.studio.name
                        searchMediaExtraInfoLayout.show(false)
                        searchStatsLayout.show(true)
                        searchScoreText.show(false)
                        searchFavouriteText.show(true)
                        searchFavouriteText.text = item.studio.favourites.toString()
                        searchStatsDivider.show(false)
                        searchMediaListStatusLayout.show(false)
                        root.clicks { listener.navigateToStudio(item.studio) }
                    }
                    SearchCategory.USER -> {
                        ImageUtil.loadImage(context, item.user.avatar.getImageUrl(appSetting), searchImage)
                        searchTitle.text = item.user.name
                        searchMediaExtraInfoLayout.show(false)
                        searchStatsLayout.show(false)
                        searchMediaListStatusLayout.show(false)
                        root.clicks { listener.navigateToUser(item.user) }
                    }
                }
            }
        }
    }

    inner class LoadingViewHolder(private val binding: ListLoadingBinding) : ViewHolder(binding) {
        override fun bind(item: SearchItem?, index: Int) {
            // do nothing
        }
    }

    interface SearchListener {
        fun navigateToMedia(media: Media)
        fun navigateToCharacter(character: Character)
        fun navigateToStaff(staff: Staff)
        fun navigateToStudio(studio: Studio)
        fun navigateToUser(user: User)
    }
}