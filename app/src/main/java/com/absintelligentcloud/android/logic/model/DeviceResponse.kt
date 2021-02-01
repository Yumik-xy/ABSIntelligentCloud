package com.absintelligentcloud.android.logic.model

//设备的数据模型
data class DeviceResponse(val status: String, val devices: List<Device>) {

    data class Device(val id: String, val name: String)
}