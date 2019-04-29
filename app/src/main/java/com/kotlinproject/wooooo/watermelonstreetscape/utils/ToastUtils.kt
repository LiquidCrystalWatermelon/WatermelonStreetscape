package com.kotlinproject.wooooo.watermelonstreetscape.utils

import android.content.Context
import android.widget.Toast

object ToastUtils {
    fun showText(context: Context, text: CharSequence, duration: Int) =
        Toast.makeText(context, text, duration).show()

    fun showTextShort(context: Context, text: CharSequence) =
        showText(context, text, Toast.LENGTH_SHORT)

    fun showTextLong(context: Context, text: CharSequence) =
        showText(context, text, Toast.LENGTH_LONG)

    fun showText(context: Context, resId: Int, duration: Int) =
        Toast.makeText(context, resId, duration).show()

    fun showTextShort(context: Context, resId: Int) =
        ToastUtils.showText(context, resId, Toast.LENGTH_SHORT)

    fun showTextLong(context: Context, resId: Int) =
        ToastUtils.showText(context, resId, Toast.LENGTH_LONG)
}