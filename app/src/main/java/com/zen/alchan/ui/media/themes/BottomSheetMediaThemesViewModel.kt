package com.zen.alchan.ui.media.themes

import com.zen.R
import com.zen.alchan.data.repository.BrowseRepository
import com.zen.alchan.data.response.TrackSearch
import com.zen.alchan.data.response.VideoSearch
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.helper.pojo.ThemeItem
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

class BottomSheetMediaThemesViewModel(
    private val browseRepository: BrowseRepository
) : BaseViewModel<BottomSheetMediaThemesParam>() {

    private val _themeItems = BehaviorSubject.createDefault<List<ListItem<ThemeItem>>>(listOf())
    val themeItems: Observable<List<ListItem<ThemeItem>>>
        get() = _themeItems

    private val _youTubeVideo = PublishSubject.create<VideoSearch>()
    val youTubeVideo: Observable<VideoSearch>
        get() = _youTubeVideo

    private val _spotifyTrack = PublishSubject.create<TrackSearch>()
    val spotifyTrack: Observable<TrackSearch>
        get() = _spotifyTrack

    override fun loadData(param: BottomSheetMediaThemesParam) {
        loadOnce {
            val items = ArrayList<ListItem<ThemeItem>>()
            if (param.animeThemeEntry != null) {
                param.animeThemeEntry.videos.forEach {
                    val titleStringBuilder = StringBuilder()
                    if (it.resolution != 0) titleStringBuilder.append("${it.resolution}P ")
                    if (it.source.isNotBlank()) titleStringBuilder.append("${it.source} ")
                    if (it.nc) titleStringBuilder.append("NC")
                    val title = titleStringBuilder.toString()
                    items.add(ListItem(title, ThemeItem(url = it.link, viewType = ThemeItem.VIEW_TYPE_VIDEO)))
                    if (it.audio.link.isNotBlank())
                        items.add(ListItem(title, ThemeItem(url = it.audio.link, viewType = ThemeItem.VIEW_TYPE_AUDIO)))
                }

                items.add(ListItem(R.string.credits_to_animethemes_moe_for_providing_the_music_video_above, ThemeItem(viewType = ThemeItem.VIEW_TYPE_TEXT)))
            }

            val searchQuery = if (param.animeThemeEntry != null) {
                "${param.animeTheme.song.title} ${param.animeTheme.song.artists.firstOrNull()?.name ?: ""}"
            } else {
                val trackTitle = param.animeTheme.song.title
                var title = trackTitle.substring(trackTitle.indexOf("\"") + 1, trackTitle.lastIndexOf("\"")).trim()
                var artist = trackTitle.substring(trackTitle.indexOf("by", trackTitle.lastIndexOf("\"")) + 3).trim()
                if (title.endsWith(")")) {
                    title = title.substring(0, title.lastIndexOf("(")).trim()
                }
                if (artist.contains("(ep")) {
                    artist = artist.substring(0, artist.indexOf("(ep")).trim()
                }
                "$title $artist"
            }

            items.add(ListItem(R.string.play_on_youtube, ThemeItem(searchQuery = "${param.media.title.romaji} $searchQuery", viewType = ThemeItem.VIEW_TYPE_YOUTUBE)))
            items.add(ListItem(R.string.play_on_spotify, ThemeItem(searchQuery = searchQuery, viewType = ThemeItem.VIEW_TYPE_SPOTIFY)))
            items.add(ListItem(R.string.al_chan_has_no_affiliation_with_the_above_players_and_can_end_up_opening_the_wrong_track, ThemeItem(viewType = ThemeItem.VIEW_TYPE_TEXT)))
            _themeItems.onNext(items)
        }
    }

    fun getYouTubeVideo(searchQuery: String) {
        _loading.onNext(true)
        disposables.add(
            browseRepository.getYouTubeVideo(searchQuery)
                .applyScheduler()
                .doFinally {
                    _loading.onNext(false)
                }
                .subscribe(
                    {
                        _youTubeVideo.onNext(it)
                    },
                    {
                        it.printStackTrace()
                    }
                )
        )
    }

    fun getSpotifyTrack(searchQuery: String) {
        _loading.onNext(true)
        disposables.add(
            browseRepository.getSpotifyTrack(searchQuery)
                .applyScheduler()
                .doFinally {
                    _loading.onNext(false)
                }
                .subscribe(
                    {
                        _spotifyTrack.onNext(it)
                    },
                    {
                        it.printStackTrace()
                    }
                )
        )
    }
}