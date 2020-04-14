package com.zen.alchan.ui.browse.media.overview

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.MediaLinks
import com.zen.alchan.helper.utils.AndroidUtility
import kotlinx.android.synthetic.main.list_media_links.view.*

class OverviewLinksRvAdapter(private val context: Context,
                             private val list: List<MediaLinks>,
                             private val listener: OverviewLinksListener
) : RecyclerView.Adapter<OverviewLinksRvAdapter.ViewHolder>() {

    interface OverviewLinksListener {
        fun openUrl(url: String)
        fun copyUrl(url: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_media_links, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val siteTheme = getSiteTheme(item.site)
        GlideApp.with(context).load(siteTheme.first).into(holder.mediaLinkIcon)
        holder.mediaLinkCard.setCardBackgroundColor(siteTheme.second)
        holder.mediaLinkWeb.text = item.site
        holder.itemView.setOnClickListener { listener.openUrl(item.url) }
        holder.itemView.setOnLongClickListener {
            listener.copyUrl(item.url)
            true
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun getSiteTheme(site: String): Pair<Int, Int> {
        return when (site.toLowerCase()) {
            "anilist" -> Pair(R.drawable.ic_anilist, Color.parseColor("#324760"))
            "twitter" -> Pair(R.drawable.ic_twitter, Color.parseColor("#03A9F4"))
            "netflix" -> Pair(R.drawable.ic_website, Color.parseColor("#F44335"))
            "hulu" -> Pair(R.drawable.ic_website, Color.parseColor("#8AC34A"))
            "crunchyroll" -> Pair(R.drawable.ic_crunchyroll, Color.parseColor("#FF9100"))
            "funimation" -> Pair(R.drawable.ic_website, Color.parseColor("#452C8A"))
            "animelab" -> Pair(R.drawable.ic_website, Color.parseColor("#3B0087"))
            "vrv" -> Pair(R.drawable.ic_website, Color.parseColor("#FEDD01"))
            "viz" -> Pair(R.drawable.ic_website, Color.parseColor("#FF0000"))
            "manga plus" -> Pair(R.drawable.ic_website, Color.parseColor("#DC0812"))
            "pocket magazine (jp)" -> Pair(R.drawable.ic_website, Color.parseColor("#0C2F89"))
            else -> Pair(R.drawable.ic_website, AndroidUtility.getResValueFromRefAttr(context, R.attr.themeCardColor))
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mediaLinkCard = view.mediaLinkCard!!
        val mediaLinkIcon = view.mediaLinkIcon!!
        val mediaLinkWeb = view.mediaLinkWeb!!
    }
}