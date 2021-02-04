package com.absintelligentcloud.android.ui.device

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.absintelligentcloud.android.logic.Repository
import com.absintelligentcloud.android.logic.model.DeviceBody
import com.absintelligentcloud.android.logic.model.DeviceResponse

class DeviceViewModel : ViewModel() {

    private val searchLiveData = MutableLiveData<DeviceBody>()

    val deviceList = ArrayList<DeviceResponse.Device>()

    val deviceLiveData = Transformations.switchMap(searchLiveData) { deviceBody ->
        Repository.searchDevices(deviceBody)
    }


    fun searchDevices(deviceBody: DeviceBody) {
        searchLiveData.value = deviceBody
    }

}