package com.zen.alchan.ui.staff

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.zen.alchan.R
import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.databinding.LayoutTitleAndTextBinding
import com.zen.alchan.helper.pojo.StaffItem
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class StaffRvAdapter(
    private val context: Context,
    list: List<StaffItem>,
    private val appSetting: AppSetting,
    private val width: Int,
    private val listener: StaffListener
) : BaseRecyclerViewAdapter<StaffItem, ViewBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            StaffItem.VIEW_TYPE_BIO -> {
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
        override fun bind(item: StaffItem, index: Int) {
            binding.apply {
                itemTitle.text = context.getString(R.string.bio)
                itemText.text = item.staff.description
            }
        }
    }
}