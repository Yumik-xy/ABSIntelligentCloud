package com.yumik.absintelligentcloud.ui.mine

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.yumik.absintelligentcloud.Application
import com.yumik.absintelligentcloud.logic.Repository
import com.yumik.absintelligentcloud.logic.network.body.UpdatePasswordBody
import com.yumik.absintelligentcloud.util.ApkVersionCodeUtils

class MineViewModel : ViewModel() {

    // 更新密码
    private val _updatePasswordLiveData = MutableLiveData<Pair<UpdatePasswordBody, String>>()
    val updatePasswordLiveData = Transformations.switchMap(_updatePasswordLiveData) {
        Repository.updatePassword(it.first, it.second)
    }

    fun updatePassword(password: String, token: String) {
        _updatePasswordLiveData.value = Pair(UpdatePasswordBody(password), token)
    }

    // 获取版本号
    private val _versionName = MutableLiveData<String>().apply {
        value = ApkVersionCodeUtils.getVerName(Application.context)
    }
    val versionName: LiveData<String> = _versionName

    // 获取更新信息
    // checkUpdate()
    private var _checkUpdateLiveData = MutableLiveData<Boolean>()
    val checkUpdateLiveData = Transformations.switchMap(_checkUpdateLiveData) {
        Repository.checkUpdate()
    }

    fun checkUpdate() {
        _checkUpdateLiveData.value = true
    }


}