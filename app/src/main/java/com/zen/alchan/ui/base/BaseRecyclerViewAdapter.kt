package com.zen.alchan.ui.base

import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.helper.libs.ItemMoveListener

// For drag and drop recycler view
abstract class BaseRecyclerViewAdapter : RecyclerView.Adapter<BaseViewHolder>(), ItemMoveListener