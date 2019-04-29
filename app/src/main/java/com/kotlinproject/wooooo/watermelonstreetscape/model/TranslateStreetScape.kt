package com.kotlinproject.wooooo.watermelonstreetscape.model

import android.graphics.Bitmap

data class TranslateStreetScape(
    val bitmap: Bitmap,
    val textBoxList: List<TextBox>
) {
    val mostImportantText by lazy {
        textBoxList.maxBy { Math.abs(it.x1 - it.x0) * Math.abs(it.y1 - it.y0) }
            ?.text ?: ""
    }
}