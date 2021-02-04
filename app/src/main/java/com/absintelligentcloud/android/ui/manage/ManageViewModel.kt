package com.absintelligentcloud.android.ui.manage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.absintelligentcloud.android.logic.Repository
import com.absintelligentcloud.android.logic.model.AreaResponse
import com.absintelligentcloud.android.logic.model.DeviceBody
import com.absintelligentcloud.android.logic.model.DeviceResponse

class ManageViewModel : ViewModel() {

    private val getStatusLiveData = MutableLiveData<Int>()

    val deviceList = ArrayList<DeviceResponse.Device>()

    val deviceLiveData = Transformations.switchMap(getStatusLiveData) { page ->
        Repository.getStatusDevices(page)
    }


    fun getStatusDevices(page: Int) {
        getStatusLiveData.value = page
    }

}