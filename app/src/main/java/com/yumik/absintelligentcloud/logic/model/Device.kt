package com.yumik.absintelligentcloud.logic.model

data class Device(
    val id: Int,
    val absType: String,
    val agentName: String,
    val areaId: String,
    val contactNumber: String,
    val deviceId: String,
    val productionDate: Long,
    val updateAt: Long,
    val status: Int,
    val tireBrand: String,
    val userName: String,
)
