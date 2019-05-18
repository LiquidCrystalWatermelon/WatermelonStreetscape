package com.kotlinproject.wooooo.watermelonstreetscape.utils

import android.content.Context
import android.widget.Toast

fun Context.toast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, text, duration).show()

fun Context.toast(resId: Int, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, resId, duration).show()

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