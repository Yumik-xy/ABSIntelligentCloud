package com.yumik.absintelligentcloud.ui.device

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.yumik.absintelligentcloud.logic.Repository
import com.yumik.absintelligentcloud.logic.network.body.AddDeviceBody
import com.yumik.absintelligentcloud.logic.network.body.DeviceInfoBody

class DeviceViewModel : ViewModel() {

    private val _deviceInfoLiveDate = MutableLiveData<Pair<DeviceInfoBody, String>>()
    val deviceInfoLiveDate = Transformations.switchMap(_deviceInfoLiveDate) {
        Repository.getDeviceInfo(it.first, it.second)
    }

    fun getDeviceInfo(deviceInfoBody: DeviceInfoBody, token: String) {
        _deviceInfoLiveDate.value = Pair(deviceInfoBody, token)
    }

    private val _areaListLiveDate = MutableLiveData<String>()
    val areaListLiveDate = Transformations.switchMap(_areaListLiveDate) {
        Repository.getAreaList(it)
    }

    fun getAreaList(token: String) {
        _areaListLiveDate.value = token
    }

    private val _deleteDeviceLiveDate = MutableLiveData<Pair<String, String>>()
    val deleteDeviceLiveDate = Transformations.switchMap(_deleteDeviceLiveDate) {
        Repository.deleteDevice(it.first, it.second)
    }

    fun deleteDevice(deviceId: String, token: String) {
        _deleteDeviceLiveDate.value = Pair(deviceId, token)
    }

    private val _addDeviceLiveDate = MutableLiveData<Pair<AddDeviceBody, String>>()
    val addDeviceLiveDate = Transformations.switchMap(_addDeviceLiveDate) {
        Repository.addDevice(it.first, it.second)
    }

    fun addDevice(addDeviceBody: AddDeviceBody, token: String) {
        _addDeviceLiveDate.value = Pair(addDeviceBody, token)
    }

    private val _updateDeviceLiveDate = MutableLiveData<Pair<AddDeviceBody, String>>()
    val updateDeviceLiveDate = Transformations.switchMap(_updateDeviceLiveDate) {
        Repository.updateDevice(it.first, it.second)
    }

    fun updateDevice(addDeviceBody: AddDeviceBody, token: String) {
        _updateDeviceLiveDate.value = Pair(addDeviceBody, token)
    }

    private val _updateFaultDeviceLiveDate = MutableLiveData<Pair<String, String>>()
    val updateFaultDeviceLiveDate = Transformations.switchMap(_updateFaultDeviceLiveDate) {
        Repository.updateFaultDevice(it.first, it.second)
    }

    fun updateFaultDevice(deviceId: String, token: String) {
        _updateFaultDeviceLiveDate.value = Pair(deviceId, token)
    }
}
