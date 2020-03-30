package com.zen.alchan.helper.pojo

import com.zen.alchan.helper.enums.ListType

class ListStyle(
    var listType: ListType = ListType.LINEAR,
    var primaryColor: String? = null,
    var secondaryColor: String? = null,
    var textColor: String? = null,
    var cardColor: String? = null,
    var toolbarColor: String? = null,
    var backgroundColor: String? = null,
    var backgroundImage: String? = null
)