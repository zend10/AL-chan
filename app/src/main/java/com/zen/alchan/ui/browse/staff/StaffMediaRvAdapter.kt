package com.zen.alchan.ui.browse.staff

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.StaffMedia
import kotlinx.android.synthetic.main.list_one_image.view.*
import type.MediaType

class StaffMediaRvAdapter(private val context: Context,
                          private val list: List<StaffMedia>,
                          private val listener: StaffMediaListener
) : RecyclerView.Adapter<StaffMediaRvAdapter.ViewHolder>() {

    interface StaffMediaListener {
        fun passSelectedMedia(mediaId: Int, mediaType: MediaType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_one_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        GlideApp.with(context).load(item.mediaImage).into(holder.mediaImage)
        holder.mediaTitleText.text = item.mediaTitle
        holder.staffRoleText.text = item.staffRole

        holder.mediaImage.setOnClickListener {
            listener.passSelectedMedia(item.mediaId!!, item.mediaType!!)
        }

        holder.itemView.setOnClickListener {
            listener.passSelectedMedia(item.mediaId!!, item.mediaType!!)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mediaImage = view.leftImage!!
        val mediaTitleText = view.leftText!!
        val staffRoleText = view.leftSubtitleText!!
    }
}