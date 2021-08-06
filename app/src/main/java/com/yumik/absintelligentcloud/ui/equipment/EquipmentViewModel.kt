package com.yumik.absintelligentcloud.ui.equipment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.yumik.absintelligentcloud.logic.Repository
import com.yumik.absintelligentcloud.logic.network.body.DeviceListBody
import com.yumik.absintelligentcloud.logic.network.body.StatusDeviceListBody

class EquipmentViewModel : ViewModel() {
    // 获取设备列表
    private val _deviceList = MutableLiveData<Pair<DeviceListBody, String>>()
    val deviceList = Transformations.switchMap(_deviceList) {
        Repository.getDeviceList(it.first, it.second)
    }

    fun getDeviceList(deviceListBody: DeviceListBody, token: String) {
        _deviceList.value = Pair(deviceListBody, token)
    }
}