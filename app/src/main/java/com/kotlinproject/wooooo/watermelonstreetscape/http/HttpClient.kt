package com.kotlinproject.wooooo.watermelonstreetscape.http

import android.app.Activity
import android.graphics.BitmapFactory
import com.kotlinproject.wooooo.watermelonstreetscape.model.TextBox
import com.kotlinproject.wooooo.watermelonstreetscape.model.TranslateResult
import com.kotlinproject.wooooo.watermelonstreetscape.model.TranslateStreetScape
import com.kotlinproject.wooooo.watermelonstreetscape.utils.com.baidu.translate.demo.TransApi
import com.kotlinproject.wooooo.watermelonstreetscape.utils.com.baidu.translate.demo.translate
import com.kotlinproject.wooooo.watermelonstreetscape.utils.spServiceIp
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.parse
import kotlinx.serialization.parseList
import okhttp3.*
import java.io.File
import java.lang.Exception
import java.lang.NullPointerException
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.math.min
import kotlin.random.Random

typealias HttpClient = RealHttpClient

interface HttpCallback<T> {
    fun onResponse(item: T)
    fun onFailure(e: Exception?)
}

class HttpCallbackBuilder<T> {
    internal var mOnResponse: ((T) -> Unit)? = null
    internal var mOnFailure: ((Exception?) -> Unit)? = null
    fun onResponse(action: ((item: T) -> Unit)?) {
        mOnResponse = action
    }

    fun onFailure(action: ((e: Exception?) -> Unit)?) {
        mOnFailure = action
    }

    fun build() = object : HttpCallback<T> {
        override fun onFailure(e: Exception?) {
            mOnFailure?.invoke(e)
        }

        override fun onResponse(item: T) {
            mOnResponse?.invoke(item)
        }
    }
}

object RealHttpClient {

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun uploadImage(
        activity: Activity,
        bitmapPath: String,
        callback: (HttpCallbackBuilder<TranslateStreetScape>.() -> Unit)
    ) {
        val mCallback = HttpCallbackBuilder<TranslateStreetScape>()
            .also(callback)
            .build()
        uploadImage(activity, bitmapPath, mCallback)
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
                .url("http://${activity.spServiceIp}/upload_img")
                .post(requestBody)
                .build()
            val boxes = try {
                OkHttpClient()
                    .newBuilder()
                    .connectTimeout(60L, TimeUnit.SECONDS)
                    .callTimeout(60L, TimeUnit.SECONDS)
                    .build()
                    .newCall(request)
                    .execute()
                    .body()
                    ?.string()
                    ?.let { Json.nonstrict.parseList<TextBox>(it) }
                    ?.onEach { it.text = translate(it.text) }
            } catch (e: Exception) {
                e.printStackTrace()
                activity.runOnUiThread { callback.onFailure(e) }
                return@thread
            }

            activity.runOnUiThread {
                if (boxes == null) {
                    callback.onFailure(NullPointerException("Empty response body!"))
                } else {
                    callback.onResponse(TranslateStreetScape(bitmapPath, boxes))
                }
            }
        }
    }
}


object FakeHttpClient {

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun uploadImage(activity: Activity, bitmapPath: String, callback: HttpCallback<TranslateStreetScape>) {
        thread {
            val bitmap = BitmapFactory.decodeFile(bitmapPath)
            val trans = TransApi()
            // 随机生成一些框返回
            val width = bitmap.width
            val height = bitmap.height
            val textBoxList = try {
                (1..Random.nextInt(10)).map {
                    val x = Random.nextInt(width * 9 / 10).toFloat()
                    val y = Random.nextInt(height).toFloat()
                    val xe = Random.nextInt(x.toInt(), min(x + width / 2, width.toFloat()).toInt()).toFloat()
                    val ye = min(y + height / 10, height.toFloat()).toInt().toFloat()
                    val w = Random.nextInt(width / 2).toFloat()
                    val h = Random.nextInt(height / 10).toFloat()
                    val degree = Random.nextInt(360).toFloat()
//            val s = (1..Random.nextInt(50))
////                .map { (Random.nextInt(26) + 'a'.toInt()).toChar() }
//                .map { (Random.nextInt(10) + '0'.toInt()).toChar() }
////                .map { Random.nextInt(0x4e00, 0x9fa5).toChar() }
//                .joinToString("")
                    val s = "apple ".repeat(Random.nextInt(10) + 1)
                    val result = translate(s)
                    TextBox(x, y, xe, ye, result, degree)
                }
            } catch (e: Exception) {
                activity.runOnUiThread {
                    e.printStackTrace()
                    callback.onFailure(e)
                }
                return@thread
            }
            activity.runOnUiThread {
                val result = TranslateStreetScape(bitmapPath, textBoxList)
                callback.onResponse(result)
            }
        }
    }
}