package com.yumik.absintelligentcloud.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlin.reflect.KProperty

class SPUtil<T>(private val context: Context, private val name: String, private val default: T) {
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(
            context.packageName + "_preferences",
            Context.MODE_PRIVATE
        )
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val data = getSharePreferences(name, default)
        Log.i("info", "get\"$name\": $data")
        return data
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        Log.i("info", "set\"$name\": $value")
        putSharePreferences(name, value)
    }

    @SuppressLint("CommitPrefEdits")
    private fun putSharePreferences(name: String, value: T) = with(prefs.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> throw IllegalArgumentException("This type of data cannot be saved!")
        }.apply()
        commit()
    }

    @Suppress("UNCHECKED_CAST")
    private fun getSharePreferences(name: String, default: T): T = with(prefs) {
        val res: Any =
            when (default) {
                is Long -> getLong(name, default)
                is Int -> getInt(name, default)
                is Boolean -> getBoolean(name, default)
                is Float -> getFloat(name, default)
                is String -> getString(name, default)
                else -> throw IllegalArgumentException("This type of data cannot be saved!")
            }!!
        return res as T
    }
}