package com.yumik.absintelligentcloud.ui.device

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yumik.absintelligentcloud.logic.model.Area
import com.yumik.absintelligentcloud.logic.network.Repository
import com.yumik.absintelligentcloud.logic.network.ServiceCreator
import com.yumik.absintelligentcloud.logic.network.body.AddDeviceBody
import com.yumik.absintelligentcloud.logic.network.response.DeviceInfoResponse
import com.yumik.absintelligentcloud.logic.network.service.AreaService
import com.yumik.absintelligentcloud.logic.network.service.DeviceService
import kotlinx.coroutines.launch

class DeviceViewModel : ViewModel() {

    val deviceInfoLiveData = Repository.StateLiveData<DeviceInfoResponse>()

    fun getDeviceInfo(deviceId: String, token: String) {
        viewModelScope.launch {
            val res = Repository.apiCall {
                ServiceCreator.create(DeviceService::class.java)
                    .getDeviceInfo(deviceId, token)
            }
            deviceInfoLiveData.value = res
        }
    }

    val areaListLiveData = Repository.StateLiveData<List<Area>>()

    fun getAreaList(token: String) {
        viewModelScope.launch {
            val res = Repository.apiCall {
                ServiceCreator.create(AreaService::class.java).getAreaList(token)
            }
            areaListLiveData.value = res
        }
    }

    val deleteDeviceLiveData = Repository.StateLiveData<Nothing>()

    fun deleteDevice(deviceId: String, token: String) {
        viewModelScope.launch {
            val res = Repository.apiCall {
                ServiceCreator.create(DeviceService::class.java)
                    .deleteDevice(deviceId, token)
            }
            deleteDeviceLiveData.value = res
        }
    }

    val addDeviceLiveData = Repository.StateLiveData<Nothing>()

    fun addDevice(addDeviceBody: AddDeviceBody, token: String) {
        viewModelScope.launch {
            val res = Repository.apiCall {
                ServiceCreator.create(DeviceService::class.java)
                    .addDevice(addDeviceBody, token)
            }
            addDeviceLiveData.value = res
        }
    }

    val updateDeviceLiveData = Repository.StateLiveData<Nothing>()

    fun updateDevice(updateDeviceBody: AddDeviceBody, token: String) {
        viewModelScope.launch {
            val res = Repository.apiCall {
                ServiceCreator.create(DeviceService::class.java)
                    .updateDevice(updateDeviceBody, token)
            }
            updateDeviceLiveData.value = res
        }
    }

    val updateFaultDeviceLiveData = Repository.StateLiveData<Nothing>()

    fun updateFaultDevice(deviceId: String, token: String) {
        viewModelScope.launch {
            val res = Repository.apiCall {
                ServiceCreator.create(DeviceService::class.java)
                    .updateFaultDevice(deviceId, token)
            }
            updateFaultDeviceLiveData.value = res
        }
    }
}
