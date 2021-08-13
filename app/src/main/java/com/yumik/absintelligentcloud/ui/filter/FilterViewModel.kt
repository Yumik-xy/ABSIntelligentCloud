package com.yumik.absintelligentcloud.ui.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yumik.absintelligentcloud.logic.model.Area
import com.yumik.absintelligentcloud.logic.network.Repository
import com.yumik.absintelligentcloud.logic.network.ServiceCreator
import com.yumik.absintelligentcloud.logic.network.service.AreaService
import kotlinx.coroutines.launch

class FilterViewModel : ViewModel() {

    val areaListLiveData = Repository.StateLiveData<List<Area>>()

    fun getAreaList(token: String) {
        viewModelScope.launch {
            val res = Repository.apiCall {
                ServiceCreator.create(AreaService::class.java)
                    .getAreaList(token)
            }
            areaListLiveData.value = res
        }
    }
}