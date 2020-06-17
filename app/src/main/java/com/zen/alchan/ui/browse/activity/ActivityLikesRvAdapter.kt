package com.zen.alchan.ui.browse.activity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.zen.alchan.R
import com.zen.alchan.data.response.User
import com.zen.alchan.helper.libs.GlideApp
import kotlinx.android.synthetic.main.list_activity_likes.view.*

class ActivityLikesRvAdapter(private val context: Context,
                             private val list: List<User>,
                             private val itemWidth: Int,
                             private val listener: ActivityLikesListener
) : RecyclerView.Adapter<ActivityLikesRvAdapter.ViewHolder>() {

    interface ActivityLikesListener {
        fun openUserPage(userId: Int)
        fun showUsername(name: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_activity_likes, parent, false)
        view.layoutParams.width = itemWidth
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        GlideApp.with(context).load(item.avatar?.medium).apply(RequestOptions.circleCropTransform()).into(holder.userAvatar)

        holder.userAvatar.setOnClickListener {
            listener.openUserPage(item.id)
        }

        holder.userAvatar.setOnLongClickListener {
            listener.showUsername(item.name)
            true
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userAvatar = view.userAvatar!!
    }
}