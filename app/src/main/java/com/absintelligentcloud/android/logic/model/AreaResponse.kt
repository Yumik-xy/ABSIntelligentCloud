package com.absintelligentcloud.android.logic.model

data class AreaResponse(
    val code: String,
    val data: List<Data>,
    val success: Boolean,
    val message: String
) {
    data class Data(val areaId: String, val areaName: String)
}