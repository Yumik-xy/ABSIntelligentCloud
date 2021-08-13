package com.yumik.absintelligentcloud.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yumik.absintelligentcloud.logic.model.Porcelain
import com.yumik.absintelligentcloud.logic.network.Network
import com.yumik.absintelligentcloud.logic.network.ServiceCreator
import com.yumik.absintelligentcloud.logic.network.body.StatusDeviceListBody
import com.yumik.absintelligentcloud.logic.network.response.DeviceListResponse
import com.yumik.absintelligentcloud.logic.network.service.DeviceService
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    // TODO: 固定值
    val porcelainList = MutableLiveData<List<Porcelain>>()

    fun setPorcelainList(list: List<Porcelain>) {
        porcelainList.value = list
    }

    // 异常设备列表
    val statusDeviceList = Network.StateLiveData<DeviceListResponse>()

    fun getStatusDeviceList(statusDeviceListBody: StatusDeviceListBody, token: String) {
        viewModelScope.launch {
            val res = Network.apiCall {
                ServiceCreator.create(DeviceService::class.java)
                    .getStatusDeviceList(statusDeviceListBody, token)
            }
            statusDeviceList.value = res
        }
    }

    // 最新异常设备列表
    val newStatusDeviceList = Network.StateLiveData<DeviceListResponse>()

    fun getNewStatusDeviceList(statusDeviceListBody: StatusDeviceListBody, token: String) {
        viewModelScope.launch {
            val res = Network.apiCall {
                ServiceCreator.create(DeviceService::class.java)
                    .getStatusDeviceList(statusDeviceListBody, token)
            }
            newStatusDeviceList.value = res
        }
    }
}