package com.yumik.absintelligentcloud.logic.network.response

import com.yumik.absintelligentcloud.logic.model.Device

data class DeviceListResponse(
    val list: List<Device>,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int,
    val totalRecords: Int
)
