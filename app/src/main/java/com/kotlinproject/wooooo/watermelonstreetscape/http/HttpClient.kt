package com.kotlinproject.wooooo.watermelonstreetscape.http

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.kotlinproject.wooooo.watermelonstreetscape.model.TextBox
import com.kotlinproject.wooooo.watermelonstreetscape.model.TranslateStreetScape
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.parse
import kotlinx.serialization.parseList
import okhttp3.*
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.lang.NullPointerException
import kotlin.concurrent.thread
import kotlin.math.min
import kotlin.random.Random

typealias HttpClient = FakeHttpClient

interface HttpCallback<T> {
    fun onResponse(item: T)
    fun onFailure(e: Exception?)
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
                .map { (Random.nextInt(10) + '0'.toInt()).toChar() }
//                .map { Random.nextInt(0x4e00, 0x9fa5).toChar() }
                .joinToString("")
            TextBox(x, y, xe, ye, s, degree)
        }
        val result = TranslateStreetScape(bitmapPath, textBoxList)
        callback.onResponse(result)
    }

    @ImplicitReflectionSerializer
    fun uploadImage(activity: Activity, bitmapPath: String, callback: (TranslateStreetScape?) -> Unit) {
        thread {
            val file = File(bitmapPath)
            val requestBody = MultipartBody
                .Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("bitmap", file.name, RequestBody.create(MediaType.parse("image/jpg"), file))
                .build()
            val request = Request
                .Builder()
                .url("http://127.0.0.1:5000/upload_img")
                .post(requestBody)
                .build()
            val response = OkHttpClient()
                .newCall(request)
                .execute()

            val result = try {
                response.body()?.string()?.let { Json.parseList<TextBox>(it) }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }?.let { TranslateStreetScape(bitmapPath, it) }

            activity.runOnUiThread {
                callback(result)
            }
        }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun uploadImage(activity: Activity, bitmapPath: String, callback: HttpCallback<TranslateStreetScape>) {
        thread {
            val file = File(bitmapPath)
            val requestBody = MultipartBody
                .Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("bitmap", file.name, RequestBody.create(MediaType.parse("image/jpg"), file))
                .build()
            val request = Request
                .Builder()
                .url("http://192.168.0.106:5000/upload_img")
                .post(requestBody)
                .build()
            val response = try {
                OkHttpClient()
                    .newCall(request)
                    .execute()
            } catch (e: Exception) {
                e.printStackTrace()
                activity.runOnUiThread { callback.onFailure(e) }
                return@thread
            }

            activity.runOnUiThread {
                try {
                    val result = response.body()?.string()?.let { Json.nonstrict.parseList<TextBox>(it) }
                    if (result == null) {
                        callback.onFailure(NullPointerException("Empty response body!"))
                    } else {
                        callback.onResponse(TranslateStreetScape(bitmapPath, result))
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    callback.onFailure(e)
                }
            }
        }
    }

    fun translateScape(scape: TranslateStreetScape) {
    }
}