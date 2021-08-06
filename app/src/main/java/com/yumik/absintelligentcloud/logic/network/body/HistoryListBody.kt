package com.yumik.absintelligentcloud.logic.network.body

data class HistoryListBody (
    val page: Int,
    val absType: String,
    val userName: String,
    val contactNumber: String,
    val agentName: String,
    val tireBrand: String,
    val recordDateFrom: Long,
    val recordDateTo: Long,
)