package com.zen.alchan.ui.browse.media.overview

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.Constant
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
        val cardTheme = if (Constant.EXTERNAL_LINK.containsKey(item.site.toLowerCase())) {
            Constant.EXTERNAL_LINK[item.site.toLowerCase()]!!
        } else {
            Pair(null, ContextCompat.getColor(context, R.color.blackLight))
        }
        if (cardTheme.first != null) {
            holder.mediaLinkIcon.visibility = View.VISIBLE
            GlideApp.with(context).load(cardTheme.first).into(holder.mediaLinkIcon)
        } else {
            holder.mediaLinkIcon.visibility = View.GONE
            GlideApp.with(context).load(0).into(holder.mediaLinkIcon)
        }
        holder.mediaLinkCard.setCardBackgroundColor(cardTheme.second)
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

    /*
         anime values: {
            "crunchyroll": "Crunchyroll",
            "funimation": "Funimation",
            "hidive": "Hidive",
            "vrv": "VRV",
            "netflix": "Netflix",
            "amazon": "Amazon",
            "hulu": "Hulu",
            "animelab": "Animelab",
            "viz": "Viz",
            "midnightpulp.com": "Midnight Pulp",
            "tubitv.com": "Tubi TV",
            "contv.com": "CONtv",
        }

        manga English: {
            "mangaplus.shueisha.co.jp": "Manga Plus",
            "viz": "Viz",
            "crunchyroll": "Crunchyroll",
            "manga.club": "Manga.Club",
            "fakku": "Fakku",
            "webtoons.com/en": "Webtoons",
            "lezhin.com/en": "Lezhin",
            "global.toomics.com": "Toomics",
            "webcomicsapp.com": "Web Comics",
        },
        Japanese: {
            "comic-walker": "ComicWalker",
            "comic.pixiv.net": "Pixiv Comic",
            "comico.jp": "Comico",
            "mangabox": "Mangabox",
            "novel.pixiv.net": "Pixiv Novel",
            "piccoma.com": "Piccoma",
            "pocket.shonenmagazine.com": "Pocket Magazine",
            "seiga.nicovideo.jp": "Nico Nico Seiga",
            "shonenjumpplus.com": "Shonen Jump Plus",
        },
        Korean: {
            "lezhin.com/ko": "Lezhin",
            "naver": "Naver",
            "webtoon.daum.net": "Daum Webtoon",
            "toomics.com": "Toomics",
            "bomtoon": "Bomtoon",
            "kakao": "KakaoPage",
        },
        Chinese: {
            "Kuaikanmanhua": "KuaiKan Manhua",
            "ac.qq.com": "QQ",
            "dajiaochongmanhua.com": "Dajiaochong Manhua",
            "manhua.weibo.com": "Weibo Manhua",
            "manmanapp.com": "Manman Manhua",
        }
     */

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mediaLinkCard = view.mediaLinkCard!!
        val mediaLinkIcon = view.mediaLinkIcon!!
        val mediaLinkWeb = view.mediaLinkWeb!!
    }
}