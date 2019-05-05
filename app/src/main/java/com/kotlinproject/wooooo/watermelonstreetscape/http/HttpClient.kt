package com.kotlinproject.wooooo.watermelonstreetscape.http

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.kotlinproject.wooooo.watermelonstreetscape.model.TextBox
import com.kotlinproject.wooooo.watermelonstreetscape.model.TranslateStreetScape
import java.io.File
import java.io.IOException
import kotlin.random.Random

typealias HttpClient = FakeHttpClient

interface HttpCallback<T> {
    fun onResponse(item: T)
    fun onFailure(e: IOException?)
}

interface HttpClientInterface {
    fun <T> uploadImage(bitmapPath: String, callback: HttpCallback<T>)
}

object FakeHttpClient : HttpClientInterface {
    override fun <T> uploadImage(bitmapPath: String, callback: HttpCallback<T>) {
        val bitmap = BitmapFactory.decodeFile(bitmapPath)
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
        val result = TranslateStreetScape(bitmapPath, textBoxList)
        callback.onResponse(result as T)
    }
}