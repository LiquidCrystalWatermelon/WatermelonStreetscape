package com.kotlinproject.wooooo.watermelonstreetscape.utils

import android.graphics.*
import com.kotlinproject.wooooo.watermelonstreetscape.model.TranslateStreetScape
import kotlin.math.min

object StreetScapeUtils {
    fun draw(
        streetScape: TranslateStreetScape,
        textColor: Int = Color.WHITE,
        showDarkMask: Boolean = true
    ): Bitmap {
        val bitmap = BitmapFactory
            .decodeFile(streetScape.photoPath)
            .copy(Bitmap.Config.ARGB_8888, true)
//        // 如果图片太小要放大
//        val minSideLen = 200
        val canvas = Canvas(bitmap)
        val paint = Paint()

        if (showDarkMask) {
            paint.color = Color.parseColor("#66000000")
            paint.style = Paint.Style.FILL_AND_STROKE
            canvas.drawRect(
                0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat(), paint)
        }

        paint.color = textColor
        paint.textSize = min(bitmap.width, bitmap.height) / 15f
        streetScape.textBoxList.forEach {
            canvas.drawText(it.text, it.x0, it.y0, paint)
        }
        return bitmap
    }
}