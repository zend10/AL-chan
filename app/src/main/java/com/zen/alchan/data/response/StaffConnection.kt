package com.zen.alchan.data.response

data class StaffConnection(
    val edges: List<StaffEdge> = listOf(),
    val nodes: List<Staff> = listOf()
)