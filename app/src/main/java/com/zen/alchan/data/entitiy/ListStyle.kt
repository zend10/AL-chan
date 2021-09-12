package com.zen.alchan.data.entitiy

data class ListStyle(
    val hideScoreWhenNotScored: Boolean = false,
    var primaryColor: String? = null,
    var secondaryColor: String? = null,
    var negativeColor: String? = null,
    var textColor: String? = null,
    var cardColor: String? = null,
    var toolbarColor: String? = null,
    var backgroundColor: String? = null,
    var floatingButtonColor: String? = null,
    var floatingIconColor: String? = null
)