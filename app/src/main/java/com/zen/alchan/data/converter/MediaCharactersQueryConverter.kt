package com.zen.alchan.data.converter

import com.zen.alchan.data.response.anilist.*

fun MediaCharactersQuery.Data.convert(): CharacterConnection {
    val characterConnection = media?.characters ?: return CharacterConnection()

    return CharacterConnection(
        edges = characterConnection.edges?.map {
            CharacterEdge(
                node = Character(
                    id = it?.node?.id ?: 0,
                    name = CharacterName(
                        first = it?.node?.name?.first ?: "",
                        middle = it?.node?.name?.middle ?: "",
                        last = it?.node?.name?.last ?: "",
                        full = it?.node?.name?.full ?: "",
                        native = it?.node?.name?.native_ ?: "",
                        alternative = it?.node?.name?.alternative?.filterNotNull() ?: listOf(),
                        alternativeSpoiler = it?.node?.name?.alternativeSpoiler?.filterNotNull() ?: listOf(),
                        userPreferred = it?.node?.name?.userPreferred ?: "",
                    ),
                    image = CharacterImage(
                        large = it?.node?.image?.large ?: "",
                        medium = it?.node?.image?.medium ?: ""
                    )
                ),
                role = it?.role,
                voiceActorRoles = it?.voiceActorRoles?.map { voiceActorRole ->
                    StaffRoleType(
                        voiceActor = Staff(
                            id = voiceActorRole?.voiceActor?.id ?: 0,
                            name = StaffName(
                                first = voiceActorRole?.voiceActor?.name?.first ?: "",
                                middle = voiceActorRole?.voiceActor?.name?.middle ?: "",
                                last = voiceActorRole?.voiceActor?.name?.last ?: "",
                                full = voiceActorRole?.voiceActor?.name?.full ?: "",
                                native = voiceActorRole?.voiceActor?.name?.native_ ?: "",
                                alternative = voiceActorRole?.voiceActor?.name?.alternative?.filterNotNull() ?: listOf(),
                                userPreferred = voiceActorRole?.voiceActor?.name?.userPreferred ?: "",
                            ),
                            image = StaffImage(
                                large = voiceActorRole?.voiceActor?.image?.large ?: "",
                                medium = voiceActorRole?.voiceActor?.image?.medium ?: ""
                            )
                        )
                    )
                } ?: listOf()
            )
        } ?: listOf(),
        pageInfo = PageInfo(
            total = characterConnection.pageInfo?.total ?: 0,
            perPage = characterConnection.pageInfo?.perPage ?: 0,
            currentPage = characterConnection.pageInfo?.currentPage ?: 0,
            lastPage = characterConnection.pageInfo?.lastPage ?: 0,
            hasNextPage = characterConnection.pageInfo?.hasNextPage ?: false
        )
    )
}