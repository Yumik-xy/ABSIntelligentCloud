package com.yumik.absintelligentcloud.ui.area

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yumik.absintelligentcloud.logic.model.Area
import com.yumik.absintelligentcloud.logic.network.Network
import com.yumik.absintelligentcloud.logic.network.ServiceCreator
import com.yumik.absintelligentcloud.logic.network.service.AreaService
import kotlinx.coroutines.launch

class AreaViewModel : ViewModel() {

    val areaListLiveData = Network.StateLiveData<List<Area>>()

    fun getAreaList(token: String) {
        viewModelScope.launch {
            val res = Network.apiCall {
                ServiceCreator.create(AreaService::class.java).getAreaList(token)
            }
            areaListLiveData.value = res
        }
    }
}