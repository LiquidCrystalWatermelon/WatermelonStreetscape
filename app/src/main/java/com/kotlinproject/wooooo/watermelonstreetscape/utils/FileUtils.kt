package com.kotlinproject.wooooo.watermelonstreetscape.utils

import android.content.Context
import android.os.Environment
import android.support.v4.content.FileProvider
import java.io.File

object FileUtils {
    private val packageName = "com.kotlinproject.wooooo.watermelonstreetscape.fileprovider"

    val appDataFilePath = Environment
        .getExternalStorageDirectory().path + "/WatermelonStreetScape"

    val imageDictPath = "$appDataFilePath/image"
    val scapeDictPath = "$appDataFilePath/scape"

    fun fileToUri(context: Context, file: File) =
        FileProvider.getUriForFile(context, packageName, file)

    fun imageFilePath(imgName: Any) = "$imageDictPath/$imgName.jpg"

    fun scapeFilePath(scapeName: Any) = "$scapeDictPath/$scapeName.sca"
}