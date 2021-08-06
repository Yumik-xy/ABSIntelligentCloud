package com.yumik.absintelligentcloud.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build


object ApkVersionCodeUtils {

    fun getVersionCode(context: Context): Long {
        var versionCode = 0L
        try {
            versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.packageManager
                    .getPackageInfo(context.packageName, 0).longVersionCode
            } else {
                context.packageManager
                    .getPackageInfo(context.packageName, 0).versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return versionCode
    }

    fun getVerName(context: Context): String {
        var verName = ""
        try {
            verName =
                context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return verName
    }
}