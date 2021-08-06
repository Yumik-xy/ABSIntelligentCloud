package com.yumik.absintelligentcloud.logic.network.response

import com.yumik.absintelligentcloud.logic.model.History

data class HistoryListResponse(
    val list: List<History>,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int,
    val totalRecords: Int
)