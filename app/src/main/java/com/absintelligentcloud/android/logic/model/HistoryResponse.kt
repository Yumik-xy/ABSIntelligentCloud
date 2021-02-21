package com.absintelligentcloud.android.logic.model

import java.util.*

data class HistoryResponse(
    val code: String,
    val data: Data,
    val success: Boolean,
    val message: String

) {
    data class Data(
        val list: List<History>,
        val page: Int,
        val pageSize: Int,
        val totalPages: Int,
        val totalRecords: Int
    )

    data class History(
        val absType: String,
        val agentName: String,
        val contactNumber: String,
        val deviceId: String,
        val faultName: String,
        val monitorDate: String,
        val tireBrand: String,
        val userName: String
    )
}
