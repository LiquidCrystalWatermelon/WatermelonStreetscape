package com.kotlinproject.wooooo.watermelonstreetscape.utils

import android.graphics.*
import com.kotlinproject.wooooo.watermelonstreetscape.model.TranslateStreetScape
import kotlin.math.abs
import kotlin.math.max
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

        // 背景遮罩
        if (showDarkMask) {
            paint.color = Color.parseColor("#66000000")
            paint.style = Paint.Style.FILL_AND_STROKE
            canvas.drawRect(
                0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat(), paint)
        }

//        // 测试用，绘制文本框
//        paint.color = Color.RED
//        streetScape.textBoxList.forEach {
//            canvas.drawRect(it.x0, it.y0, it.x1, it.y1, paint)
//        }

        // 绘制文本
        paint.color = textColor
        paint.textSize = min(bitmap.width, bitmap.height) / 15f
        streetScape.textBoxList.forEach {
            // 先把字符串按行分割，每行最少1个字符
            // 每行平分文本框的高
            val lines = mutableListOf<String>()
            var text = it.text
            while (!text.isBlank()) {
                val index = paint
                    .breakText(text, true, it.x1 - it.x0, null)
                    .let { max(it, 1) }
                lines.add(text.substring(0, index))
                text = text.substring(index)
            }
            val count = lines.size
            for (i in lines.indices) {
                val line = lines[i]
                val my = it.y0 + (it.y1 - it.y0) * (i + 1) / (count + 1)
                canvas.drawText(line, it.x0, my + abs(paint.fontMetrics.top / 3), paint)
            }
        }
        return bitmap
    }
}