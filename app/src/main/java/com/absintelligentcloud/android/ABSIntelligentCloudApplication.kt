package com.absintelligentcloud.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

//全局获取Context
class ABSIntelligentCloudApplication : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        // 全局常量定义
        var token: String = ""

        //            "AITechLab eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJyb2xlIjo1LCJleHAiOjE2NDM2MDEzNjYsInVzZXJJZCI6ImFkbWluIiwiaWF0IjoxNjEyMDY1MzY2fQ.R3Y6ziHXpUti0-T49MInzZugPoxcTLy9qu7N3eHcbfjTZQMryuPgXsIOtOAg06YMojkHjRZzgUUNhAB9v8-iPc8W14ObN60tEkL4Tm68EK0ohEV1mgtxnlEXzojLgF1n1FsMRMn4Z0LBZ4ihhtQtM7MyrACivDli_zNagU9cKm8mK18D--KDkKnfnVbI-w8Y2crWBtkD0PNI6wwJgudxty2Wt7G02sK7eSpsPWEo5acV7_mRXxm0rxRZ6OdPjkQ_GgRaqKj_4x64St4bGS6stxzKDHBz0aZ3GfzDnbAd2AiFufc6ZaDm8HGph0jxTdPrdS5SRToKrzH8iPK-s_-FSg"
        var role: Int = -1
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

}