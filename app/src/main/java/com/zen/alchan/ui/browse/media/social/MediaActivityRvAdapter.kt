package com.zen.alchan.ui.browse.media.social

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.secondsToDateTime
import kotlinx.android.synthetic.main.list_media_activity.view.*

class MediaActivityRvAdapter(private val context: Context,
                             private val list: List<MediaActivityQuery.Activity?>,
                             private val listener: MediaActivityListener
) : RecyclerView.Adapter<MediaActivityRvAdapter.ViewHolder>() {

    interface MediaActivityListener {
        fun passSelectedActivity(id: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_media_activity, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]?.fragments?.onListActivity

        GlideApp.with(context).load(item?.user?.avatar?.medium).apply(RequestOptions.circleCropTransform()).into(holder.avatarImage)
        holder.nameText.text = item?.user?.name
        holder.dateText.text = item?.createdAt?.secondsToDateTime()
        holder.statusText.text = "${item?.status?.capitalize()}${if (item?.progress != null) " ${item.progress}" else ""}"

        holder.itemView.setOnClickListener {
            if (item?.userId != null) {
                listener.passSelectedActivity(item.id)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatarImage = view.avatarImage!!
        val nameText = view.nameText!!
        val dateText = view.dateText!!
        val statusText = view.statusText!!
    }
}