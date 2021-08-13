package com.yumik.absintelligentcloud

import android.app.Application
import android.content.Context

class Application : Application() {

    companion object {
        lateinit var context: Context
        const val BROAD_GET_DEVICE = "com.yumik.absintelligentcloud.getdevice"
        const val BROAD_SEARCH_DEVICE = "com.yumik.absintelligentcloud.searchdevice"
        const val BROAD_ADD_DEVICE = "com.yumik.absintelligentcloud.adddevice"
        const val BROAD_LOG_OUT = "com.yumik.absintelligentcloud.logout"
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

}