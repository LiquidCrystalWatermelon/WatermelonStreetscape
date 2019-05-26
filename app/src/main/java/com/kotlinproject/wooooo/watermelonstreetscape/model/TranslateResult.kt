package com.kotlinproject.wooooo.watermelonstreetscape.model

import kotlinx.serialization.Serializable

@Serializable
data class TranslateResult(
    val from: String,
    val to: String,
    val trans_result: List<TransResult>
)

@Serializable
data class TransResult(
    val src: String,
    val dst: String
)