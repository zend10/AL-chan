package com.zen.alchan.data.converter

import com.zen.alchan.data.response.anilist.*

fun CharacterQuery.Data.convert(): Character {
    return Character(
        id = character?.id ?: 0,
        name = CharacterName(
            first = character?.name?.first ?: "",
            middle = character?.name?.middle ?: "",
            last = character?.name?.last ?: "",
            full = character?.name?.full ?: "",
            native = character?.name?.native_ ?: "",
            alternative = character?.name?.alternative?.filterNotNull()?.filterNot { it.isBlank() } ?: listOf(),
            alternativeSpoiler = character?.name?.alternativeSpoiler?.filterNotNull()?.filterNot { it.isBlank() } ?: listOf(),
            userPreferred = character?.name?.userPreferred ?: "",
        ),
        image = CharacterImage(
            large = character?.image?.large ?: "",
            medium = character?.image?.medium ?: ""
        ),
        description = character?.description ?: "",
        gender = character?.gender ?: "",
        dateOfBirth = if (character?.dateOfBirth != null)
            FuzzyDate(year = character.dateOfBirth.year, month = character.dateOfBirth.month, day = character.dateOfBirth.day)
        else
            null
        ,
        age = character?.age ?: "",
        bloodType = character?.bloodType ?: "",
        isFavourite = character?.isFavourite ?: false,
        siteUrl = character?.siteUrl ?: "",
        media = MediaConnection(
            edges = character?.media?.edges?.filterNotNull()?.map {
                MediaEdge(
                    node = Media(
                        idAniList = it.node?.id ?: 0,
                        title = MediaTitle(
                            romaji = it.node?.title?.romaji ?: "",
                            english = it.node?.title?.romaji ?: "",
                            native = it.node?.title?.romaji ?: "",
                            userPreferred = it.node?.title?.romaji ?: "",
                        ),
                        coverImage = MediaCoverImage(
                            extraLarge = it.node?.coverImage?.extraLarge ?: "",
                            large = it.node?.coverImage?.large ?: "",
                            medium = it.node?.coverImage?.medium ?: "",
                        ),
                        type = it.node?.type,
                        format = it.node?.format,
                        averageScore = it.node?.averageScore ?: 0,
                        meanScore = it.node?.meanScore ?: 0,
                        popularity = it.node?.popularity ?: 0,
                        favourites = it.node?.favourites ?: 0,
                        startDate = if (it.node?.startDate != null)
                            FuzzyDate(year = it.node.startDate.year, month = it.node.startDate.month, day = it.node.startDate.day)
                        else
                            null
                    ),
                    characterRole = it.characterRole,
                    voiceActorRoles = it.voiceActorRoles?.filterNotNull()?.map {
                        StaffRoleType(
                            voiceActor = Staff(
                                id = it.voiceActor?.id ?: 0,
                                name = StaffName(
                                    first = it.voiceActor?.name?.first ?: "",
                                    middle = it.voiceActor?.name?.middle ?: "",
                                    last = it.voiceActor?.name?.last ?: "",
                                    full = it.voiceActor?.name?.full ?: "",
                                    native = it.voiceActor?.name?.native_ ?: "",
                                    alternative = it.voiceActor?.name?.alternative?.filterNotNull() ?: listOf(),
                                    userPreferred = it.voiceActor?.name?.userPreferred ?: "",
                                ),
                                language = it.voiceActor?.languageV2 ?: "",
                                image = StaffImage(
                                    large = it.voiceActor?.image?.large ?: "",
                                    medium = it.voiceActor?.image?.medium ?: ""
                                )
                            ),
                            roleNote = it.roleNotes ?: "",
                            dubGroup = it.dubGroup ?: ""
                        )
                    } ?: listOf()
                )
            } ?: listOf(),
            pageInfo = PageInfo(
                total = character?.media?.pageInfo?.total ?: 0,
                perPage = character?.media?.pageInfo?.perPage ?: 0,
                currentPage = character?.media?.pageInfo?.currentPage ?: 0,
                lastPage = character?.media?.pageInfo?.lastPage ?: 0,
                hasNextPage = character?.media?.pageInfo?.hasNextPage ?: false
            )
        ),
        favourites = character?.favourites ?: 0
    )
}