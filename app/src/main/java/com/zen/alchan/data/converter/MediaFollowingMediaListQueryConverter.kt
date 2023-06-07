import com.zen.alchan.MediaFollowingMediaListQuery
import com.zen.alchan.data.response.anilist.*

fun MediaFollowingMediaListQuery.Data.convert(): Page<MediaList> {
    return Page(
        pageInfo = PageInfo(
            total = Page?.pageInfo?.total ?: 0,
            perPage = Page?.pageInfo?.perPage ?: 0,
            currentPage = Page?.pageInfo?.currentPage ?: 0,
            lastPage = Page?.pageInfo?.lastPage ?: 0,
            hasNextPage = Page?.pageInfo?.hasNextPage ?: false
        ),
        data = Page?.mediaList?.map {
            MediaList(
                id = it?.id,
                status = it?.status,
                score = it?.score ?: 0.0,
                progress = it?.progress ?: 0,
                progressVolumes = it?.progressVolumes ?: 0,
                user = User(
                    id = it?.user?.id ?: 0,
                    name = it?.user?.name ?: "",
                    avatar = UserAvatar(
                        large = it?.user?.avatar?.large ?: "",
                        medium = it?.user?.avatar?.medium ?: ""
                    ),
                    mediaListOptions = MediaListOptions(
                        scoreFormat = it?.user?.mediaListOptions?.scoreFormat
                    )
                ),
                media = Media(
                    type = it?.media?.type,
                    episodes = it?.media?.episodes ?: 0,
                    chapters = it?.media?.chapters ?: 0,
                    volumes = it?.media?.volumes ?: 0
                )
            )
        } ?: listOf()
    )
}