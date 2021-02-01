package com.absintelligentcloud.android.ui.manage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.absintelligentcloud.android.logic.Repository
import com.absintelligentcloud.android.logic.model.AreaResponse
import com.absintelligentcloud.android.logic.model.DeviceResponse

class ManageViewModel : ViewModel() {

    private val getLiveData = MutableLiveData<AreaResponse.Data>()

    var areaList = ArrayList<AreaResponse.Data>()

    val areaLiveData = Transformations.switchMap(getLiveData) {
        Repository.getAreaList()
    }

    fun getAreaList(query: AreaResponse.Data) {
        getLiveData.value = query
    }

}