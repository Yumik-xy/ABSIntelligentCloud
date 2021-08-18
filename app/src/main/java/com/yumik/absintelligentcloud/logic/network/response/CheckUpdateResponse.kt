package com.yumik.absintelligentcloud.logic.network.response

import com.google.gson.annotations.SerializedName

data class CheckUpdateResponse(
    // val url: String,
    // val description: String,
    // val versionName: String,
    // val versionCode: Long,
    // val md5: String
    @SerializedName("tag_name") val versionCode: String,
    @SerializedName("name") val versionName: String,
    @SerializedName("published_at") val time: String,
    @SerializedName("assets") val assets: List<Assets>,
    @SerializedName("body") val description: String
)

data class Assets(
    @SerializedName("name") val apkName: String,
    @SerializedName("size") val apkSize: Long,
    @SerializedName("browser_download_url") val url: String,
)