package com.yumik.absintelligentcloud.util

import android.app.Activity
import android.content.pm.PackageManager

object ApplicationNameUtil {
    fun Activity.getApplicationName(): String {
        val pm = this.packageManager
        val ai = try {
            pm.getApplicationInfo(this.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
        return if (ai == null) "(unknown)" else pm.getApplicationLabel(ai).toString()
    }
}