package com.yumik.absintelligentcloud.ui.equipment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yumik.absintelligentcloud.logic.network.Network
import com.yumik.absintelligentcloud.logic.network.ServiceCreator
import com.yumik.absintelligentcloud.logic.network.body.DeviceListBody
import com.yumik.absintelligentcloud.logic.network.response.DeviceListResponse
import com.yumik.absintelligentcloud.logic.network.service.DeviceService
import kotlinx.coroutines.launch

class EquipmentViewModel : ViewModel() {
    // 获取设备列表
    val deviceList = Network.StateLiveData<DeviceListResponse>()

    fun getDeviceList(deviceListBody: DeviceListBody, token: String) {
        viewModelScope.launch {
            val res = ServiceCreator.create(DeviceService::class.java)
                .getDeviceList(deviceListBody, token)
            deviceList.value = res
        }
    }
}