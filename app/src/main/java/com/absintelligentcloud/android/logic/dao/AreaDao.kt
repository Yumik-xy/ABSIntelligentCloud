package com.absintelligentcloud.android.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.absintelligentcloud.android.ABSIntelligentCloudApplication
import com.absintelligentcloud.android.logic.model.AreaResponse
import com.google.gson.Gson

object AreaDao {

    fun saveArea(area: AreaResponse.Data) {
        sharedPreferences().edit() {
            putString("area", Gson().toJson(area))
        }
    }

    fun getSavedArea(): AreaResponse.Data {
        val areaJson = sharedPreferences().getString("area", "")
        return Gson().fromJson(areaJson, AreaResponse.Data::class.java)
    }

    fun isAreaSaved() = sharedPreferences().contains("area")

    private fun sharedPreferences() = ABSIntelligentCloudApplication.context.getSharedPreferences(
        "abs_intelligent)cloud",
        Context.MODE_PRIVATE
    )
}