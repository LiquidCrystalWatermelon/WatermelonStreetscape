package com.kotlinproject.wooooo.watermelonstreetscape.utils

import android.graphics.*
import com.kotlinproject.wooooo.watermelonstreetscape.model.TranslateStreetScape
import kotlin.math.min

object StreetScapeUtils{
    fun draw(streetScape: TranslateStreetScape):Bitmap {
        val bitmap = BitmapFactory
            .decodeFile(streetScape.photoPath)
            .copy(Bitmap.Config.ARGB_8888, true)
//        // 如果图片太小要放大
//        val minSideLen = 200
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = Color.BLUE
        paint.textSize = min(bitmap.width,bitmap.height) / 15f
        streetScape.textBoxList.forEach {
            canvas.drawText(it.text,it.x0,it.y0,paint)
        }
        return bitmap
    }
}