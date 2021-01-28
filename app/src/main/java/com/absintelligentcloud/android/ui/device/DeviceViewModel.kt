package com.absintelligentcloud.android.ui.device

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.absintelligentcloud.android.logic.Repository
import com.absintelligentcloud.android.logic.model.Device

class DeviceViewModel : ViewModel() {

    private val searchLiveData = MutableLiveData<String>()

    val deviceList = ArrayList<Device>()

    val deviceLiveData = Transformations.switchMap(searchLiveData) { query ->
        Repository.searchDevices(query)
    }

    fun getDevice(query: String) {
        searchLiveData.value = query
    }

}