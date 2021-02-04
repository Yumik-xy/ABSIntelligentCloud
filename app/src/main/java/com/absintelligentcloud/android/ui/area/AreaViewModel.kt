package com.absintelligentcloud.android.ui.area

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.absintelligentcloud.android.logic.Repository
import com.absintelligentcloud.android.logic.dao.AreaDao
import com.absintelligentcloud.android.logic.model.AreaResponse

class AreaViewModel : ViewModel() {

    private val getLiveData = MutableLiveData<Boolean>()

    val areaList = ArrayList<AreaResponse.Data>()

    val areaLiveData = Transformations.switchMap(getLiveData) {
        Repository.getAreaList()
    }

    fun getAreaList(query: Boolean) {
        getLiveData.value = query
    }

    fun saveArea(area: AreaResponse.Data) = Repository.saveArea(area)

    fun getSavedArea() = Repository.getSavedArea()

    fun isAreaSaved() = Repository.isAreaSaved()

}