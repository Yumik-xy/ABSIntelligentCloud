package com.yumik.absintelligentcloud.logic.network.response

data class DeviceInfoResponse(
    val absType: String,
    val agentName: String,
    val areaId: String,
    val contactNumber: String,
    val deviceId: String,
    val productionDate: Long,
    val status: Int,
    val tireBrand: String,
    val userName: String,
)
