package com.yumik.absintelligentcloud.logic.model

data class FilterHistory(
    val areaFilter: String = "",
    val absType: String = "",
    val agentName: String = "",
    val contactNumber: String = "",
    val deviceId: String = "",
    val tireBrand: String = "",
    val userName: String = "",
    val statusId: String = "",
    val faultId: String = "",
    val dataFrom: Long = 0L,
    val dataTo: Long = 0L,
)