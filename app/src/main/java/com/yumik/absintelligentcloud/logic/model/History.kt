package com.yumik.absintelligentcloud.logic.model

data class History(
    val id: Int,
    val absType: String,
    val agentName: String,
    val contactNumber: String,
    val deviceId: String,
    val faultName: String,
    val monitorDate: String,
    val tireBrand: String,
    val userName: String
)
