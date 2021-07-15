package com.zen.alchan.ui.reorder

import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

abstract class BaseReorderRecyclerViewAdapter<T, VB: ViewBinding>(
    list: List<T>
) : BaseRecyclerViewAdapter<T, VB>(list), ItemMoveListener