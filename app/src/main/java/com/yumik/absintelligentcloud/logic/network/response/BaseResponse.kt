package com.yumik.absintelligentcloud.logic.network.response

data class BaseResponse<T>(
    val success: Boolean,
    val code: Int,
    val message: String,
    val data: T,
)