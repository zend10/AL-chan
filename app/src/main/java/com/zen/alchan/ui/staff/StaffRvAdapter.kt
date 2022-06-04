package com.zen.alchan.ui.staff

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.databinding.LayoutHorizontalListBinding
import com.zen.alchan.databinding.LayoutTitleAndTextBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.pojo.StaffItem
import com.zen.alchan.helper.utils.MarkdownUtil
import com.zen.alchan.helper.utils.SpaceItemDecoration
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class StaffRvAdapter(
    private val context: Context,
    list: List<StaffItem>,
    private val appSetting: AppSetting,
    private val width: Int,
    private val listener: StaffListener
) : BaseRecyclerViewAdapter<StaffItem, ViewBinding>(list) {

    companion object {
        private const val ITEM_LIMIT = 9
    }

    private var characterAdapter: StaffCharacterRvAdapter? = null
    private var mediaAdapter: StaffMediaRvAdapter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            StaffItem.VIEW_TYPE_BIO -> {
                val view = LayoutTitleAndTextBinding.inflate(inflater, parent, false)
                return BioViewHolder(view)
            }
            StaffItem.VIEW_TYPE_CHARACTER -> {
                val view = LayoutHorizontalListBinding.inflate(inflater, parent, false)
                characterAdapter = StaffCharacterRvAdapter(context, listOf(), appSetting, width, listener.staffCharacterListener)
                view.horizontalListRecyclerView.adapter = characterAdapter
                view.horizontalListRecyclerView.addItemDecoration(SpaceItemDecoration(right = context.resources.getDimensionPixelSize(R.dimen.marginPageNormal)))
                return CharacterViewHolder(view)
            }
            StaffItem.VIEW_TYPE_MEDIA -> {
                val view = LayoutHorizontalListBinding.inflate(inflater, parent, false)
                mediaAdapter = StaffMediaRvAdapter(context, listOf(), appSetting, width, listener.staffMediaListener)
                view.horizontalListRecyclerView.adapter = mediaAdapter
                view.horizontalListRecyclerView.addItemDecoration(SpaceItemDecoration(right = context.resources.getDimensionPixelSize(R.dimen.marginPageNormal)))
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
        override fun bind(item: StaffItem, index: Int) {
            binding.apply {
                itemTitle.show(true)
                itemTitle.text = context.getString(R.string.bio)
                MarkdownUtil.applyMarkdown(context, itemText, item.staff.description)
            }
        }
    }

    inner class CharacterViewHolder(private val binding: LayoutHorizontalListBinding) : ViewHolder(binding) {
        override fun bind(item: StaffItem, index: Int) {
            binding.apply {
                horizontalListTitle.text = context.getString(R.string.characters)
                horizontalListSeeMore.show(true)
                horizontalListSeeMore.clicks {
                    listener.navigateToStaffCharacter()
                }
                horizontalListFootnoteText.show(false)
                characterAdapter?.updateData(item.staff.characters.edges)

            }
        }
    }

    inner class MediaViewHolder(private val binding: LayoutHorizontalListBinding) : ViewHolder(binding) {
        override fun bind(item: StaffItem, index: Int) {
            binding.apply {
                horizontalListTitle.text = context.getString(R.string.media)
                horizontalListSeeMore.show(item.staff.staffMedia.edges.size > ITEM_LIMIT)
                horizontalListSeeMore.clicks {
                    listener.navigateToStaffMedia()
                }
                horizontalListFootnoteText.show(false)
                mediaAdapter?.updateData(item.media)
            }
        }
    }
}