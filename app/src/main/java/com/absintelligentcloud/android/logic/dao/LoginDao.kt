package com.absintelligentcloud.android.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.absintelligentcloud.android.ABSIntelligentCloudApplication
import com.absintelligentcloud.android.logic.model.AreaResponse
import com.google.gson.Gson

object LoginDao {

    fun saveToken(token: String) {
        sharedPreferences().edit() {
            putString("token", token)
        }
    }

    fun getSavedToken(): String {
        val token = sharedPreferences().getString("token", "") ?: ""
        return token
    }

    fun isTokenSaved() = sharedPreferences().contains("token")

    private fun sharedPreferences() = ABSIntelligentCloudApplication.context.getSharedPreferences(
        "abs_intelligent_cloud",
        Context.MODE_PRIVATE
    )
}