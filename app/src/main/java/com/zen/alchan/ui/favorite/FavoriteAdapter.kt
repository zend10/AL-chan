package com.zen.alchan.ui.favorite

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.databinding.ListCardTextBinding
import com.zen.alchan.databinding.ListLoadingBinding
import com.zen.alchan.databinding.ListRectangleBinding
import com.zen.alchan.helper.enums.Favorite
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.pojo.FavoriteItem
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class FavoriteAdapter(
    private val context: Context,
    list: List<FavoriteItem?>,
    private val appSetting: AppSetting,
    private val listener: FavoriteListener
) : BaseRecyclerViewAdapter<FavoriteItem? , ViewBinding>(list) {

    companion object {
        private const val VIEW_TYPE_CARD_TEXT = -3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_LOADING -> {
                val view = ListLoadingBinding.inflate(inflater, parent, false)
                LoadingViewHolder(view)
            }
            VIEW_TYPE_CARD_TEXT -> {
                val view = ListCardTextBinding.inflate(inflater, parent, false)
                CardTextViewHolder(view)
            }
            else -> {
                val view = ListRectangleBinding.inflate(inflater, parent, false)
                RectangleViewHolder(view)
            }
        }
    }

    inner class RectangleViewHolder(private val binding: ListRectangleBinding) : ViewHolder(binding) {
        override fun bind(item: FavoriteItem?, index: Int) {
            if (item == null)
                return

            binding.apply {
                val image = when (item.favorite) {
                    Favorite.ANIME -> item.anime?.getCoverImage(appSetting)
                    Favorite.MANGA -> item.manga?.getCoverImage(appSetting)
                    Favorite.CHARACTERS -> item.character?.getImage(appSetting)
                    Favorite.STAFF -> item.staff?.getImage(appSetting)
                    else -> null
                }

                if (image != null)
                    ImageUtil.loadImage(context, image, rectangleItemImage)

                rectangleItemText.show(false)

                root.clicks {
                    when (item.favorite) {
                        Favorite.ANIME -> listener.navigateToAnime(item.anime?.getId() ?: 0)
                        Favorite.MANGA -> listener.navigateToManga(item.manga?.getId() ?: 0)
                        Favorite.CHARACTERS -> listener.navigateToCharacter(item.character?.id ?: 0)
                        Favorite.STAFF -> listener.navigateToStaff(item.staff?.id ?: 0)
                        else -> Unit
                    }
                }
            }
        }
    }

    inner class CardTextViewHolder(private val binding: ListCardTextBinding) : ViewHolder(binding) {
        override fun bind(item: FavoriteItem?, index: Int) {
            if (item == null)
                return

            binding.apply {
                cardIcon.show(false)
                cardText.text = item.studio?.name ?: ""
                root.clicks { listener.navigateToStudio(item.studio?.id ?: 0) }
            }
        }
    }

    inner class LoadingViewHolder(private val binding: ListLoadingBinding) : ViewHolder(binding) {
        override fun bind(item: FavoriteItem?, index: Int) {
            // do nothing
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] == null)
            VIEW_TYPE_LOADING
        else if (list[position]?.favorite == Favorite.STUDIOS)
            VIEW_TYPE_CARD_TEXT
        else
            VIEW_TYPE_CONTENT
    }

    interface FavoriteListener {
        fun navigateToAnime(id: Int)
        fun navigateToManga(id: Int)
        fun navigateToCharacter(id: Int)
        fun navigateToStaff(id: Int)
        fun navigateToStudio(id: Int)
    }
}