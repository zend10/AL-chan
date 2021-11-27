package com.zen.alchan.ui.character

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.zen.alchan.R
import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.databinding.LayoutTitleAndTextBinding
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.pojo.CharacterItem
import com.zen.alchan.helper.utils.MarkdownUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class CharacterRvAdapter(
    private val context: Context,
    list: List<CharacterItem>,
    private val appSetting: AppSetting,
    private val width: Int,
    private val listener: CharacterListener
) : BaseRecyclerViewAdapter<CharacterItem, ViewBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            CharacterItem.VIEW_TYPE_BIO -> {
                val view = LayoutTitleAndTextBinding.inflate(inflater, parent, false)
                return BioViewHolder(view)
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
            }
        }
    }
}