package com.zen.alchan.data.converter

import com.zen.alchan.data.response.anilist.*

fun StaffQuery.Data.convert(): Staff {
    return staff?.let {
        Staff(
            id = it.id,
            name = StaffName(
                first = it.name?.first ?: "",
                middle = it.name?.middle ?: "",
                last = it.name?.last ?: "",
                full = it.name?.full ?: "",
                native = it.name?.native_ ?: "",
                alternative = it.name?.alternative?.filterNotNull()?.filterNot { it.isBlank() } ?: listOf(),
                userPreferred = it.name?.userPreferred ?: "",
            ),
            language = it.languageV2 ?: "",
            image = StaffImage(
                large = it.image?.large ?: "",
                medium = it.image?.medium ?: ""
            ),
            description = it.description ?: "",
            primaryOccupations = it.primaryOccupations?.filterNotNull()?.filterNot { it.isBlank() } ?: listOf(),
            gender = it.gender ?: "",
            dateOfBirth = if (it.dateOfBirth != null)
                FuzzyDate(year = it.dateOfBirth.year, month = it.dateOfBirth.month, day = it.dateOfBirth.day)
            else
                null,
            dateOfDeath = if (it.dateOfDeath != null)
                FuzzyDate(year = it.dateOfDeath.year, month = it.dateOfDeath.month, day = it.dateOfDeath.day)
            else
                null,
            age = it.age ?: 0,
            yearsActive = it.yearsActive?.filterNotNull() ?: listOf(),
            homeTown = it.homeTown ?: "",
            bloodType = it.bloodType ?: "",
            isFavourite = it.isFavourite,
            siteUrl = it.siteUrl ?: "",
            staffMedia = MediaConnection(
                edges = it.staffMedia?.edges?.filterNotNull()?.map {
                    MediaEdge(
                        node = Media(
                            idAniList = it.node?.id ?: 0,
                            title = MediaTitle(
                                romaji = it.node?.title?.romaji ?: "",
                                english = it.node?.title?.english ?: "",
                                native = it.node?.title?.native_ ?: "",
                                userPreferred = it.node?.title?.userPreferred ?: "",
                            ),
                            coverImage = MediaCoverImage(
                                extraLarge = it.node?.coverImage?.extraLarge ?: "",
                                large = it.node?.coverImage?.large ?: "",
                                medium = it.node?.coverImage?.medium ?: "",
                            ),
                            type = it.node?.type,
                            format = it.node?.format
                        ),
                        staffRole = it.staffRole ?: ""
                    )
                } ?: listOf(),
                pageInfo = PageInfo(
                    total = it.staffMedia?.pageInfo?.total ?: 0,
                    perPage = it.staffMedia?.pageInfo?.perPage ?: 0,
                    currentPage = it.staffMedia?.pageInfo?.currentPage ?: 0,
                    lastPage = it.staffMedia?.pageInfo?.lastPage ?: 0,
                    hasNextPage = it.staffMedia?.pageInfo?.hasNextPage ?: false
                )
            ),
            characters = CharacterConnection(
                edges = it.characters?.edges?.filterNotNull()?.map {
                    CharacterEdge(
                        node = Character(
                            id = it.node?.id ?: 0,
                            name = CharacterName(
                                first = it.node?.name?.first ?: "",
                                middle = it.node?.name?.middle ?: "",
                                last = it.node?.name?.last ?: "",
                                full = it.node?.name?.full ?: "",
                                native = it.node?.name?.native_ ?: "",
                                alternative = it.node?.name?.alternative?.filterNotNull()?.filterNot { it.isBlank() } ?: listOf(),
                                alternativeSpoiler = it.node?.name?.alternativeSpoiler?.filterNotNull()?.filterNot { it.isBlank() } ?: listOf(),
                                userPreferred = it.node?.name?.userPreferred ?: "",
                            ),
                            image = CharacterImage(
                                large = it.node?.image?.large ?: "",
                                medium = it.node?.image?.medium ?: "",
                            )
                        ),
                        role = it.role,
                        name = it.name ?: ""
                    )
                } ?: listOf(),
                pageInfo = PageInfo(
                    total = it.characters?.pageInfo?.total ?: 0,
                    perPage = it.characters?.pageInfo?.perPage ?: 0,
                    currentPage = it.characters?.pageInfo?.currentPage ?: 0,
                    lastPage = it.characters?.pageInfo?.lastPage ?: 0,
                    hasNextPage = it.characters?.pageInfo?.hasNextPage ?: false
                )
            ),
            characterMedia = MediaConnection(
                edges = it.characterMedia?.edges?.filterNotNull()?.map {
                    MediaEdge(
                        node = Media(
                            idAniList = it.node?.id ?: 0,
                            title = MediaTitle(
                                romaji = it.node?.title?.romaji ?: "",
                                english = it.node?.title?.english ?: "",
                                native = it.node?.title?.native_ ?: "",
                                userPreferred = it.node?.title?.userPreferred ?: "",
                            ),
                            coverImage = MediaCoverImage(
                                extraLarge = it.node?.coverImage?.extraLarge ?: "",
                                large = it.node?.coverImage?.large ?: "",
                                medium = it.node?.coverImage?.medium ?: "",
                            )
                        ),
                        characters = it.characters?.filterNotNull()?.map {
                            Character(
                                id = it.id,
                                name = CharacterName(
                                    first = it.name?.first ?: "",
                                    middle = it.name?.middle ?: "",
                                    last = it.name?.last ?: "",
                                    full = it.name?.full ?: "",
                                    native = it.name?.native_ ?: "",
                                    alternative = it.name?.alternative?.filterNotNull()?.filterNot { it.isBlank() } ?: listOf(),
                                    alternativeSpoiler = it.name?.alternativeSpoiler?.filterNotNull()?.filterNot { it.isBlank() } ?: listOf(),
                                    userPreferred = it.name?.userPreferred ?: ""
                                ),
                                image = CharacterImage(
                                    large = it.image?.large ?: "",
                                    medium = it.image?.medium ?: ""
                                )
                            )
                        } ?: listOf(),
                        characterRole = it.characterRole,
                        characterName = it.characterName ?: "",
                        roleNotes = it.roleNotes ?: "",
                        dubGroup = it.dubGroup ?: ""
                    )
                } ?: listOf(),
                pageInfo = PageInfo(
                    total = it.characterMedia?.pageInfo?.total ?: 0,
                    perPage = it.characterMedia?.pageInfo?.perPage ?: 0,
                    currentPage = it.characterMedia?.pageInfo?.currentPage ?: 0,
                    lastPage = it.characterMedia?.pageInfo?.lastPage ?: 0,
                    hasNextPage = it.characterMedia?.pageInfo?.hasNextPage ?: false
                )
            ),
            favourites = it.favourites ?: 0
        )
    } ?: Staff()
}