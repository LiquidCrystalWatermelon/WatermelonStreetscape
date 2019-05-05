package com.kotlinproject.wooooo.watermelonstreetscape.http

import android.graphics.Bitmap
import com.kotlinproject.wooooo.watermelonstreetscape.model.TextBox
import com.kotlinproject.wooooo.watermelonstreetscape.model.TranslateStreetScape
import java.io.IOException
import kotlin.random.Random

typealias HttpClient = FakeHttpClient

interface HttpCallback<T> {
    fun onResponse(item: T)
    fun onFailure(e: IOException?)
}

interface HttpClientInterface {
    fun <T> uploadImage(bitmap: Bitmap, callback: HttpCallback<T>)
}

object FakeHttpClient : HttpClientInterface {
    override fun <T> uploadImage(bitmap: Bitmap, callback: HttpCallback<T>) {
        // 随机生成一些框返回
        val width = bitmap.width
        val height = bitmap.height
        val textBoxList = (1..Random.nextInt(10)).map {
            val x = Random.nextInt(width).toFloat()
            val y = Random.nextInt(height).toFloat()
            val w = Random.nextInt(100).toFloat()
            val h = Random.nextInt(100).toFloat()
            val s = (1..Random.nextInt(100))
                .map { (Random.nextInt(26) - 'a'.toInt()).toChar() }
                .joinToString("")
            TextBox(x, y, x + w, y + h, s)
        }
        val result = TranslateStreetScape(bitmap, textBoxList)
        callback.onResponse(result as T)
    }
}