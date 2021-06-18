package com.zen.alchan.ui.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.helper.pojo.ListStyle

abstract class BaseRecyclerViewAdapter<T>(
    protected var list: List<T>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected var appSetting = AppSetting.EMPTY_APP_SETTING

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateData(list: List<T>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun applyAppSetting(appSetting: AppSetting) {
        this.appSetting = appSetting
    }
}