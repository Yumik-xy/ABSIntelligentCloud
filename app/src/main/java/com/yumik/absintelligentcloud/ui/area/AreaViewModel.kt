package com.yumik.absintelligentcloud.ui.area

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.yumik.absintelligentcloud.logic.Repository

class AreaViewModel : ViewModel() {

    private val _areaList = MutableLiveData<String>()
    val areaList = Transformations.switchMap(_areaList) {
        Repository.getAreaList(it)
    }

    fun getAreaList(token: String) {
        _areaList.value = token
    }
}