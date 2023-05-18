package com.zen.alchan.ui.character

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewbinding.ViewBinding
import com.google.android.material.resources.MaterialResources.getDimensionPixelSize
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.StaffRoleType
import com.zen.alchan.databinding.LayoutHorizontalListBinding
import com.zen.alchan.databinding.LayoutTitleAndListBinding
import com.zen.alchan.databinding.LayoutTitleAndTextBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.pojo.CharacterItem
import com.zen.alchan.helper.utils.GridSpacingItemDecoration
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.helper.utils.MarkdownUtil
import com.zen.alchan.helper.utils.SpaceItemDecoration
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import com.zen.alchan.type.MediaSort

class CharacterRvAdapter(
    private val context: Context,
    list: List<CharacterItem>,
    private val appSetting: AppSetting,
    private val width: Int,
    private val listener: CharacterListener
) : BaseRecyclerViewAdapter<CharacterItem, ViewBinding>(list) {

    companion object {
        private const val MEDIA_LIMIT = 9
    }

    private var voiceActorAdapter: CharacterVoiceActorRvAdapter? = null
    private var characterMediaAdapter: CharacterMediaRvAdapter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            CharacterItem.VIEW_TYPE_BIO -> {
                val view = LayoutTitleAndTextBinding.inflate(inflater, parent, false)
                return BioViewHolder(view)
            }
            CharacterItem.VIEW_TYPE_STAFF -> {
                val view = LayoutHorizontalListBinding.inflate(inflater, parent, false)
                voiceActorAdapter = CharacterVoiceActorRvAdapter(context, listOf(), appSetting, width, listener)
                view.horizontalListRecyclerView.adapter = voiceActorAdapter
                view.horizontalListRecyclerView.addItemDecoration(SpaceItemDecoration(right = context.resources.getDimensionPixelSize(R.dimen.marginPageNormal)))
                return VoiceActorViewHolder(view)
            }
            CharacterItem.VIEW_TYPE_MEDIA -> {
                val view = LayoutTitleAndListBinding.inflate(inflater, parent, false)
                characterMediaAdapter = CharacterMediaRvAdapter(context, listOf(), appSetting, null, listener.characterMediaListener)
                view.listRecyclerView.layoutManager = GridLayoutManager(context, context.resources.getInteger(R.integer.gridSpan))
                view.listRecyclerView.adapter = characterMediaAdapter
                view.listRecyclerView.addItemDecoration(GridSpacingItemDecoration(context.resources.getInteger(R.integer.gridSpan), context.resources.getDimensionPixelSize(R.dimen.marginNormal), false))
                view.listRecyclerView.isNestedScrollingEnabled = false
                return MediaViewHolder(view)
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

    inner class BioViewHolder(private val binding: LayoutTitleAndTextBinding) : ViewHolder(binding) {
        override fun bind(item: CharacterItem, index: Int) {
            binding.apply {
                itemTitle.show(item.character.name.alternative.isNotEmpty())
                itemTitle.text = item.character.name.alternative.joinToString(", ")
                MarkdownUtil.applyMarkdown(context, itemText, item.character.description)

                if (item.showFullDescription) {
                    itemGradientLayer.show(false)
                    ImageUtil.loadImage(context, R.drawable.ic_chevron_up, itemArrowIcon)
                } else {
                    itemGradientLayer.show(true)
                    ImageUtil.loadImage(context, R.drawable.ic_chevron_down, itemArrowIcon)
                }

                itemArrowIcon.show(true)
                itemArrowIcon.clicks {
                    listener.toggleShowMore(!item.showFullDescription)
                }
            }
        }
    }

    inner class VoiceActorViewHolder(private val binding: LayoutHorizontalListBinding) : ViewHolder(binding) {
        override fun bind(item: CharacterItem, index: Int) {
            binding.apply {
                horizontalListTitle.text = context.getString(R.string.voice_actors)
                horizontalListSeeMore.show(false)
                horizontalListFootnoteText.show(true)
                horizontalListFootnoteText.text = context.getString(R.string.long_press_to_view_series_they_feature_in)
                voiceActorAdapter?.updateData(item.voiceActors)
            }
        }
    }

    inner class MediaViewHolder(private val binding: LayoutTitleAndListBinding) : ViewHolder(binding) {
        override fun bind(item: CharacterItem, index: Int) {
            binding.apply {
                titleText.text = context.getString(R.string.series)
                seeMoreText.show(item.character.media.edges.size > MEDIA_LIMIT)
                seeMoreText.clicks { listener.navigateToCharacterMedia() }
                characterMediaAdapter?.updateData(item.character.media.edges.take(MEDIA_LIMIT))
            }
        }
    }
}