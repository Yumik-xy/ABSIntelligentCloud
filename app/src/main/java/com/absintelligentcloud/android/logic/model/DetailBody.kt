package com.absintelligentcloud.android.logic.model

data class DetailBody(
    val absType: String,
    val deviceId: String,
    val areaId: String,
    val userName: String,
    val productionDate: Long,
    val contactNumber: String,
    val agentName: String,
    val tireBrand: String
)
