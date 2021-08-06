package com.yumik.absintelligentcloud.logic.model

data class Porcelain(
    val id: Int,
    val imageResource: Int,
    val title: String,
    val clickListener: () -> Unit,
)
