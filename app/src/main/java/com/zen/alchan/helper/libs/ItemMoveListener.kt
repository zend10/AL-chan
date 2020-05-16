package com.zen.alchan.helper.libs

import com.zen.alchan.ui.base.BaseViewHolder

interface ItemMoveListener {
    fun onRowMoved(fromPosition: Int, toPosition: Int)
    fun onRowSelected(itemViewHolder: BaseViewHolder)
    fun onRowClear(itemViewHolder: BaseViewHolder)
}