package com.kotlinproject.wooooo.watermelonstreetscape.http

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.kotlinproject.wooooo.watermelonstreetscape.model.TextBox
import com.kotlinproject.wooooo.watermelonstreetscape.model.TranslateStreetScape
import java.io.File
import java.io.IOException
import kotlin.math.min
import kotlin.random.Random

typealias HttpClient = FakeHttpClient

interface HttpCallback<T> {
    fun onResponse(item: T)
    fun onFailure(e: IOException?)
}

interface HttpClientInterface {
    fun <T> uploadImage(bitmapPath: String, callback: HttpCallback<T>)
}

object FakeHttpClient {
    fun uploadImage(bitmapPath: String, callback: HttpCallback<TranslateStreetScape>) {
        val bitmap = BitmapFactory.decodeFile(bitmapPath)
        // 随机生成一些框返回
        val width = bitmap.width
        val height = bitmap.height
        val textBoxList = (1..Random.nextInt(10)).map {
            val x = Random.nextInt(width * 9 / 10).toFloat()
            val y = Random.nextInt(height).toFloat()
            val xe = Random.nextInt(x.toInt(), min(x + width / 2, width.toFloat()).toInt()).toFloat()
            val ye = min(y + height / 10, height.toFloat()).toInt().toFloat()
            val w = Random.nextInt(width / 2).toFloat()
            val h = Random.nextInt(height / 10).toFloat()
            val degree = Random.nextInt(360).toFloat()
            val s = (1..Random.nextInt(50))
//                .map { (Random.nextInt(26) + 'a'.toInt()).toChar() }
                .map { (Random.nextInt(10) + '0'.toInt()).toChar()}
//                .map { Random.nextInt(0x4e00, 0x9fa5).toChar() }
                .joinToString("")
            TextBox(x, y, xe, ye, s, degree)
        }
        val result = TranslateStreetScape(bitmapPath, textBoxList)
        callback.onResponse(result)
    }

    fun translateScape(scape: TranslateStreetScape) {
    }
}