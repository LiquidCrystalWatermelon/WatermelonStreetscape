package com.kotlinproject.wooooo.watermelonstreetscape.utils.com.baidu.translate.demo

import com.kotlinproject.wooooo.watermelonstreetscape.model.TranslateResult
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.parse
import java.lang.Exception

@UnstableDefault
@ImplicitReflectionSerializer
fun translate(query: String, from: String = "auto", to: String = "zh") = try {
    TransApi
        .instance
        .getTransResult(query, from, to)
        .let { Json.nonstrict.parse<TranslateResult>(it) }
        .trans_result[0]
        .dst
} catch (e: Exception) {
    ""
}