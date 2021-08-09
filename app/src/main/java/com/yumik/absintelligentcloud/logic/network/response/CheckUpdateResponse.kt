package com.yumik.absintelligentcloud.logic.network.response

data class CheckUpdateResponse(
    val url: String,
    val description: String,
    val versionName: String,
    val versionCode: Long,
    val md5: String
)