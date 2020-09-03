package com.zen.alchan.helper.pojo

import com.zen.alchan.helper.enums.ListType

class ListStyle(
    var listType: ListType? = ListType.LINEAR,
    var primaryColor: String? = null,
    var secondaryColor: String? = null,
    var textColor: String? = null,
    var cardColor: String? = null,
    var toolbarColor: String? = null,
    var backgroundColor: String? = null,
    var floatingButtonColor: String? = null,
    var floatingIconColor: String? = null,
    var backgroundImage: Boolean? = false,
    var longPressViewDetail: Boolean? = true,
    var hideMangaVolume: Boolean? = false,
    var hideMangaChapter: Boolean? = false,
    var hideNovelVolume: Boolean? = false,
    var hideNovelChapter: Boolean? = false,
    var showNotesIndicator: Boolean? = false,
    var showPriorityIndicator: Boolean? = false
)