package com.kotlinproject.wooooo.watermelonstreetscape.utils

import android.content.Context
import android.support.v4.content.FileProvider
import java.io.File

object FileUtils {
    private val packageName = "com.kotlinproject.wooooo.watermelonstreetscape.fileprovider"

    fun fileToUri(context: Context, file: File) =
        FileProvider.getUriForFile(context, packageName, file)
}