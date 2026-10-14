package com.zen.alchan.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.MediaList
import com.zen.alchan.databinding.ListMediaListGridBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class ContinueWatchingRvAdapter(
    private val context: Context,
    list: List<MediaList>,
    private val appSetting: AppSetting,
    private val listener: HomeListener.ReleasingTodayListener
) : BaseRecyclerViewAdapter<MediaList, ListMediaListGridBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ItemViewHolder(ListMediaListGridBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class ItemViewHolder(private val binding: ListMediaListGridBinding) : ViewHolder(binding) {
        override fun bind(item: MediaList, index: Int) {
            with(binding) {
                val media = item.media
                ImageUtil.loadImage(context, media.getCoverImage(appSetting), mediaListCoverImage)
                mediaListTitleText.text = media.getTitle(appSetting)
                mediaListFormatText.text = context.getString(R.string.continue_watching_next_episode, item.progress + 1)
                mediaListProgressText.text = "${item.progress} / ${media.episodes ?: "?"}"

                mediaListAiringRootLayout.show(false)
                mediaListScoreLayout.show(false)
                mediaListProgressVolumeLayout.show(false)

                root.clicks { listener.navigateToListEditor(item) }
                mediaListTitleLayout.clicks { listener.navigateToMedia(media) }
                mediaListProgressLayout.clicks { listener.showProgressDialog(item) }
            }
        }
    }
}
