package com.zen.alchan.data.converter

import com.zen.alchan.CharacterQuery
import com.zen.alchan.data.response.anilist.*

fun CharacterQuery.Data.convert(): Character {
    return Character(
        id = Character?.id ?: 0,
        name = CharacterName(
            first = Character?.name?.first ?: "",
            middle = Character?.name?.middle ?: "",
            last = Character?.name?.last ?: "",
            full = Character?.name?.full ?: "",
            native = Character?.name?.native ?: "",
            alternative = Character?.name?.alternative?.filterNotNull()?.filterNot { it.isBlank() } ?: listOf(),
            alternativeSpoiler = Character?.name?.alternativeSpoiler?.filterNotNull()?.filterNot { it.isBlank() } ?: listOf(),
            userPreferred = Character?.name?.userPreferred ?: "",
        ),
        image = CharacterImage(
            large = Character?.image?.large ?: "",
            medium = Character?.image?.medium ?: ""
        ),
        description = Character?.description ?: "",
        gender = Character?.gender ?: "",
        dateOfBirth = if (Character?.dateOfBirth != null)
            FuzzyDate(year = Character.dateOfBirth.year, month = Character.dateOfBirth.month, day = Character.dateOfBirth.day)
        else
            null
        ,
        age = Character?.age ?: "",
        bloodType = Character?.bloodType ?: "",
        isFavourite = Character?.isFavourite ?: false,
        siteUrl = Character?.siteUrl ?: "",
        media = MediaConnection(
            edges = Character?.media?.edges?.filterNotNull()?.map {
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
                                    native = it.voiceActor?.name?.native ?: "",
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
                total = Character?.media?.pageInfo?.total ?: 0,
                perPage = Character?.media?.pageInfo?.perPage ?: 0,
                currentPage = Character?.media?.pageInfo?.currentPage ?: 0,
                lastPage = Character?.media?.pageInfo?.lastPage ?: 0,
                hasNextPage = Character?.media?.pageInfo?.hasNextPage ?: false
            )
        ),
        favourites = Character?.favourites ?: 0
    )
}