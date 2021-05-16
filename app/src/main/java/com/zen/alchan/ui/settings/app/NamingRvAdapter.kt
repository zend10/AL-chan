package com.zen.alchan.ui.settings.app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.enums.*
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import kotlinx.android.synthetic.main.list_text.view.*

class NamingRvAdapter(
    private val context: Context,
    list: List<Naming>,
    private val listener: NamingListener
) : BaseRecyclerViewAdapter<Naming>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_text, parent, false)
        return when (viewType) {
            VIEW_TYPE_CHARACTER_NAMING -> CharacterViewHolder(view)
            VIEW_TYPE_STAFF_NAMING -> StaffViewHolder(view)
            VIEW_TYPE_MEDIA_NAMING -> MediaViewHolder(view)
            else -> MediaViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when {
            holder is CharacterViewHolder && list[position] is CharacterNaming -> holder.bind(list[position] as CharacterNaming)
            holder is StaffViewHolder && list[position] is StaffNaming -> holder.bind(list[position] as StaffNaming)
            holder is MediaViewHolder && list[position] is MediaNaming -> holder.bind(list[position] as MediaNaming)
        }
    }

    inner class CharacterViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(characterNaming: CharacterNaming) {
            view.itemText.text = context.getString(
                when (characterNaming) {
                    CharacterNaming.FOLLOW_ANILIST -> R.string.follow_anilist_setting
                    CharacterNaming.FIRST_MIDDLE_LAST -> R.string.use_character_first_middle_last_name_format
                    CharacterNaming.LAST_MIDDLE_FIRST -> R.string.use_character_last_middle_first_name_format
                    CharacterNaming.NATIVE -> R.string.native_name
                }
            )
            view.itemLayout.clicks {
                listener.getSelectedNaming(characterNaming)
            }
        }
    }

    inner class StaffViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(staffNaming: StaffNaming) {
            view.itemText.text = context.getString(
                when (staffNaming) {
                    StaffNaming.FOLLOW_ANILIST -> R.string.follow_anilist_setting
                    StaffNaming.FIRST_MIDDLE_LAST -> R.string.use_staff_first_middle_last_name_format
                    StaffNaming.LAST_MIDDLE_FIRST -> R.string.use_staff_last_middle_first_name_format
                    StaffNaming.NATIVE -> R.string.native_name
                }
            )
            view.itemLayout.clicks {
                listener.getSelectedNaming(staffNaming)
            }
        }
    }

    inner class MediaViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(mediaNaming: MediaNaming) {
            view.itemText.text = context.getString(
                when (mediaNaming) {
                    MediaNaming.FOLLOW_ANILIST -> R.string.follow_anilist_setting
                    MediaNaming.ENGLISH -> R.string.use_media_english_name_format
                    MediaNaming.ROMAJI -> R.string.use_media_romaji_name_format
                    MediaNaming.NATIVE -> R.string.use_media_native_name_format
                }
            )
            view.itemLayout.clicks {
                listener.getSelectedNaming(mediaNaming)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is CharacterNaming -> VIEW_TYPE_CHARACTER_NAMING
            is StaffNaming -> VIEW_TYPE_STAFF_NAMING
            is MediaNaming -> VIEW_TYPE_MEDIA_NAMING
            else -> VIEW_TYPE_MEDIA_NAMING
        }
    }

    interface NamingListener {
        fun getSelectedNaming(naming: Naming)
    }

    companion object {
        private const val VIEW_TYPE_CHARACTER_NAMING = 100
        private const val VIEW_TYPE_STAFF_NAMING = 200
        private const val VIEW_TYPE_MEDIA_NAMING = 300

    }
}