package com.absintelligentcloud.android.ui.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.absintelligentcloud.android.logic.Repository
import com.absintelligentcloud.android.logic.model.DeviceBody
import com.absintelligentcloud.android.logic.model.DeviceResponse
import com.absintelligentcloud.android.logic.model.HistoryBody
import com.absintelligentcloud.android.logic.model.HistoryResponse

class HistoryViewModel : ViewModel() {

    private val getLiveData = MutableLiveData<HistoryBody>()

    val historyList = ArrayList<HistoryResponse.History>()

    val historyLiveData = Transformations.switchMap(getLiveData) { historyBody ->
        Repository.getHistory(historyBody)
    }

    fun getHistory(historyBody: HistoryBody) {
        getLiveData.value = historyBody
    }

}