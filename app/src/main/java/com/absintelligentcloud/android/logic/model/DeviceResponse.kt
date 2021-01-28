package com.absintelligentcloud.android.logic.model

data class DeviceResponse(val status: String, val devices: List<Device>)

data class Device(val id: String, val name: String)
