package com.zen.alchan.ui.social

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.BestFriend
import com.zen.alchan.helper.utils.AndroidUtility
import kotlinx.android.synthetic.main.list_best_friends.view.*

class BestFriendRvAdapter(private val context: Context,
                          private val list: List<BestFriend>,
                          private val itemWidth: Int,
                          private val listener: BestFriendListener
): RecyclerView.Adapter<BestFriendRvAdapter.ViewHolder>() {

    interface BestFriendListener {
        fun passSelectedBestFriend(position: Int, id: Int? = null)
        fun openUserPage(id: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_best_friends, parent, false)
        view.layoutParams.width = itemWidth
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        if (item.id == null) {
            holder.nameText.text = context.getString(R.string.add)
            GlideApp.with(context).load(0).apply(RequestOptions.circleCropTransform()).into(holder.avatarImage)
            holder.addIcon.visibility = View.VISIBLE
            holder.itemView.setOnClickListener { listener.passSelectedBestFriend(position) }
        } else {
            holder.nameText.text = item.name
            GlideApp.with(context).load(item.avatar).apply(RequestOptions.circleCropTransform()).into(holder.avatarImage)
            holder.addIcon.visibility = View.GONE
            holder.itemView.setOnClickListener { listener.passSelectedBestFriend(position, item.id) }
            holder.itemView.setOnLongClickListener {
                listener.openUserPage(item.id!!)
                true
            }
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