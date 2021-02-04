package com.absintelligentcloud.android.logic.model

import java.util.*

//设备的数据模型
data class DeviceResponse(
    val code: String,
    val data: Data,
    val success: Boolean,
    val message: String
) {

    data class Data(
        val list: List<Device>,
        val page: Int,
        val pageSize: Int,
        val totalPages: Int,
        val totalRecords: Int
    )

    data class Device(
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