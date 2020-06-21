package com.zen.alchan.ui.social.global

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.BestFriend
import kotlinx.android.synthetic.main.list_best_friends.view.*

class GlobalFeedFilterRvAdapter(private val context: Context,
                                private val list: List<BestFriend>,
                                private val itemWidth: Int,
                                private val listener: BestFriendListener
): RecyclerView.Adapter<GlobalFeedFilterRvAdapter.ViewHolder>() {

    interface BestFriendListener {
        fun passSelectedBestFriend(position: Int, id: Int? = null)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_best_friends, parent, false)
        view.layoutParams.width = itemWidth
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        if (position == 0) {
            holder.nameText.text = context.getString(R.string.global)
            GlideApp.with(context).load(0).apply(RequestOptions.circleCropTransform()).into(holder.avatarImage)
            GlideApp.with(context).load(R.drawable.ic_staff).apply(RequestOptions.circleCropTransform()).into(holder.addIcon)
            holder.addIcon.visibility = View.VISIBLE
            holder.itemView.setOnClickListener { listener.passSelectedBestFriend(position) }
        } else if (position == 1) {
            holder.nameText.text = context.getString(R.string.following)
            GlideApp.with(context).load(0).apply(RequestOptions.circleCropTransform()).into(holder.avatarImage)
            GlideApp.with(context).load(R.drawable.ic_people).apply(RequestOptions.circleCropTransform()).into(holder.addIcon)
            holder.addIcon.visibility = View.VISIBLE
            holder.itemView.setOnClickListener { listener.passSelectedBestFriend(position) }
        } else {
            holder.nameText.text = item.name
            GlideApp.with(context).load(item.avatar).apply(RequestOptions.circleCropTransform()).into(holder.avatarImage)
            holder.addIcon.visibility = View.GONE
            holder.itemView.setOnClickListener { listener.passSelectedBestFriend(position, item.id) }
        }

        holder.selectedOverlay.visibility = if (item.isSelected) View.VISIBLE else View.GONE
    }

    override fun getItemCount(): Int {
        return list.size
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val addIcon = view.addIcon!!
        val selectedOverlay = view.selectedOverlay!!
        val avatarImage = view.avatarImage!!
        val nameText = view.nameText!!
    }
}