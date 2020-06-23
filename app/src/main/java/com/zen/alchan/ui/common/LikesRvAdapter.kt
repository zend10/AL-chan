package com.zen.alchan.ui.common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.zen.alchan.R
import com.zen.alchan.data.response.User
import com.zen.alchan.helper.libs.GlideApp
import kotlinx.android.synthetic.main.list_likes.view.*

class LikesRvAdapter(private val context: Context,
                     private val list: List<User>,
                     private val listener: LikesListener
) : RecyclerView.Adapter<LikesRvAdapter.ViewHolder>() {

    interface LikesListener {
        fun passSelectedUser(userId: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_likes, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        GlideApp.with(context).load(item.avatar?.medium).apply(RequestOptions.circleCropTransform()).into(holder.avatarImage)
        holder.nameText.text = item.name

        holder.itemView.setOnClickListener {
            listener.passSelectedUser(item.id)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatarImage = view.avatarImage!!
        val nameText = view.nameText!!
    }
}