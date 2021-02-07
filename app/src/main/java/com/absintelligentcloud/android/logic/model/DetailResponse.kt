package com.absintelligentcloud.android.logic.model

data class DetailResponse(
    val code: String,
    val data: Data,
    val success: Boolean,
    val message: String
) {

    data class Data(
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
}