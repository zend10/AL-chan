package com.zen.alchan.helper.pojo

import com.zen.alchan.helper.enums.CountryCode
import com.zen.alchan.helper.enums.MediaListSort
import type.MediaFormat
import type.MediaSeason
import type.MediaSource
import type.MediaStatus

class MediaFilteredData(
    var selectedSort: MediaListSort? = null,
    var selectedFormat: MediaFormat? = null,
    var selectedYear: Int? = null,
    var selectedSeason: MediaSeason? = null,
    var selectedCountry: CountryCode? = null,
    var selectedStatus: MediaStatus? = null,
    var selectedSource: MediaSource? = null,
    var selectedGenreList: ArrayList<String?>? = null,
    var selectedTagList: ArrayList<String?>? = null
)