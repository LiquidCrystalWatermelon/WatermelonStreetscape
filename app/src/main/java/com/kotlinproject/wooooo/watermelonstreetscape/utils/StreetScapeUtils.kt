package com.kotlinproject.wooooo.watermelonstreetscape.utils

import android.graphics.*
import com.kotlinproject.wooooo.watermelonstreetscape.model.TranslateStreetScape
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object StreetScapeUtils {
    private val TAG = "StreetScapeUtils_Log"
    fun draw(
        streetScape: TranslateStreetScape,
        textColor: Int = Color.WHITE,
        highlightCrowdingText: Boolean = true,
        crowdingTextColor: Int = Color.CYAN,
        showDarkMask: Boolean = true,
        darkMaskColor: Int = Color.parseColor("#66000000"),
        showTextBorder: Boolean = false,
        textBorderColor: Int = Color.parseColor("#66000000"),
        textBorderStyle: Paint.Style = Paint.Style.FILL
    ): Bitmap {
        val bitmap = BitmapFactory
            .decodeFile(streetScape.photoPath)
            .copy(Bitmap.Config.ARGB_8888, true)

        val canvas = Canvas(bitmap)
        val paint = Paint()

        // 背景遮罩
        if (showDarkMask) {
            paint.color = darkMaskColor
            paint.style = Paint.Style.FILL_AND_STROKE
            canvas.drawRect(
                0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat(), paint)
        }

//        // 测试用，绘制文本框
//        paint.color = Color.RED
//        streetScape.textBoxList.forEach {
//            canvas.drawRect(it.x0, it.y0, it.x1, it.y1, paint)
//        }
//
        // 绘制倾斜文本框
        if (showTextBorder) {
            paint.color = textBorderColor
            paint.style = textBorderStyle
            streetScape.textBoxList.forEach {
                val cx = (it.x0 + it.x1) / 2
                val cy = (it.y0 + it.y1) / 2
                canvas.rotate(it.degree, cx, cy)
                canvas.drawRect(it.x0, it.y0, it.x1, it.y1, paint)
                canvas.rotate(-it.degree, cx, cy)
            }
        }

        val crowdingTextColor =
            if (highlightCrowdingText) crowdingTextColor else textColor

        // 绘制文本
        streetScape.textBoxList.forEach { box ->
            val maxTextSize = min(bitmap.width, bitmap.height) / 4f
            val minTextSize = min(bitmap.width, bitmap.height) / 30f
            val textSizeStep = min(bitmap.width, bitmap.height) / 100f
            // 先把字符串按行分割，每行最少1个字符
            // 每行平分文本框的高
            val lines = mutableListOf<String>()
            paint.color = crowdingTextColor
            paint.textSize = maxTextSize + textSizeStep
            while (paint.textSize > minTextSize) {
                paint.textSize -= textSizeStep
                var text = box.text
                lines.clear()
                val textHeight = abs(paint.fontMetrics.let { it.top + it.bottom })
                // 切行
                while (!text.isBlank()) {
                    val index = paint
                        .breakText(text, true, box.x1 - box.x0, null)
                        .let { max(it, 1) }
                    lines.add(text.substring(0, index))
                    text = text.substring(index)
                }
                if (textHeight < (box.y1 - box.y0) / (lines.size.let { if (it > 1) it + 1 else 1 })) {
                    paint.color = textColor
                    break
                }
            }

            // 临时附加：标红文本
            if (box.tag == "red") paint.color = Color.RED

            // 旋转画布
            val cx = (box.x0 + box.x1) / 2
            val cy = (box.y0 + box.y1) / 2
            canvas.rotate(box.degree, cx, cy)

            // 绘制文字
            val count = lines.size
            for (i in lines.indices) {
                val line = lines[i]
                val my = box.y0 + (box.y1 - box.y0) * (i + 1) / (count + 1)
                canvas.drawText(line, box.x0, my + abs(paint.fontMetrics.top / 3), paint)
            }

            // 画布转回来
            canvas.rotate(-box.degree, cx, cy)
        }
        return bitmap
    }
}