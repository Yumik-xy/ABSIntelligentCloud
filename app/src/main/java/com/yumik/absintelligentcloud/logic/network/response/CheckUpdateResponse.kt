package com.yumik.absintelligentcloud.logic.network.response

data class CheckUpdateResponse(
    val downLoadUrl: String,
    val size: Long,
    val name: String,
    val description: String,
    val version: String,
    val versionCode: Long,
    val md5: String
)