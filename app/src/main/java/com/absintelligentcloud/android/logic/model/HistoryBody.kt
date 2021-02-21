package com.absintelligentcloud.android.logic.model

data class HistoryBody(
    val absType: String,
    val userName: String,
    val contactNumber: String,
    val agentName: String,
    val tireBrand: String,
    val recordDateFrom: Long,
    val recordDateTo: Long,
    val page: Int
)