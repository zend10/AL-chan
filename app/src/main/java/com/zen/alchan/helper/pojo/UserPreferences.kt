package com.zen.alchan.helper.pojo

import com.zen.alchan.helper.enums.ListType
import type.ActivityType
import type.MediaSort
import type.StaffLanguage

class UserPreferences(
    var seasonalListType: ListType? = ListType.LINEAR,
    var socialActivityType: ArrayList<ActivityType>? = null,
    var globalActivityType: ArrayList<ActivityType>? = null,
    var voiceActorLanguage: StaffLanguage? = StaffLanguage.JAPANESE,
    var sortCharacterMedia: MediaSort? = null,
    var orderCharacterMediaIsDescending: Boolean? = true,
    var sortStaffAnime: MediaSort? = MediaSort.POPULARITY_DESC,
    var sortStaffManga: MediaSort? = MediaSort.POPULARITY_DESC,
    var sortStaffCharacter: MediaSort? = null
)