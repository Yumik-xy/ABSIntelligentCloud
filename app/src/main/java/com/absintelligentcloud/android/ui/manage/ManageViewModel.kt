package com.absintelligentcloud.android.ui.manage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.absintelligentcloud.android.logic.Repository
import com.absintelligentcloud.android.logic.model.AreaResponse
import com.absintelligentcloud.android.logic.model.DeviceBody
import com.absintelligentcloud.android.logic.model.DeviceResponse
import com.absintelligentcloud.android.logic.model.StatusDeviceBody

class ManageViewModel : ViewModel() {

    private val getStatusLiveData = MutableLiveData<StatusDeviceBody>()

    val deviceList = ArrayList<DeviceResponse.Device>()

    val deviceLiveData = Transformations.switchMap(getStatusLiveData) { result ->
        Repository.getStatusDevices(result)
    }


    fun getStatusDevices(statusDeviceBody: StatusDeviceBody) {
        getStatusLiveData.value = statusDeviceBody
    }

}