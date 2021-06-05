package com.zen.alchan.helper.pojo

data class ListStyle(
    val hideScoreWhenNotScored: Boolean = false,

) {
    companion object {
        val EMPTY_LIST_STYLE = ListStyle()
    }
}