package com.zen.alchan.helper.pojo

data class Affinity(
    val value: Double? = null,
    val count: Int = 0,
    val status: Int = 0
) {
    companion object {
        const val AFFINITY_STATUS_LOADING = 100
        const val AFFINITY_STATUS_COMPLETED = 200
        const val AFFINITY_STATUS_FAILED = 300
    }
}