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

    fun getVerCodeFromName(versionName: String): Long {
        val split1 = versionName.split('_')
        if (split1.size == 2) {
            val split2 = split1[0].split('.')
            if (split2.size == 3) {
                return split2[2].toLong()
            }
        }
        return -1L
    }
}