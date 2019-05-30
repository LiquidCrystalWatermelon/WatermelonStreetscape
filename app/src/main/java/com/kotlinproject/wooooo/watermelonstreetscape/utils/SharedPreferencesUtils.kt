package com.kotlinproject.wooooo.watermelonstreetscape.utils

import android.app.Activity
import android.content.Context
import android.preference.PreferenceManager

val Context.spServiceIp
    get() = PreferenceManager
        .getDefaultSharedPreferences(this)
        .getString("service_ip", "192.168.0.16:5000")
//
//var Context.spServiceIp: String?
//    get() {
//        return getSharedPreferences("service_ip", Activity.MODE_PRIVATE)
//            .getString("service_ip", null) ?: "192.168.0.16:5000"
//    }
//    set(value) {
//        val spe = getSharedPreferences("service_ip", Activity.MODE_PRIVATE).edit()
//        spe.putString("service_ip", value)
//        spe.apply()
//    }


private fun Context.spBoolean(s: String, default: Boolean) = PreferenceManager
    .getDefaultSharedPreferences(this)
    .getBoolean(s, default)

val Context.spIsBackgroundDark get() = spBoolean("is_background_dark", true)

val Context.spShowTextBorder get() = spBoolean("show_text_border", false)

val Context.spHighLightCrowdingText get() = spBoolean("highlight_crowding_text", true)

val Context.spIsTextTilt get() = spBoolean("is_text_tilt",false)

var Context.spIsTestMode
    get() = spBoolean("is_test_mode", false)
    set(value) {
        val spe = PreferenceManager.getDefaultSharedPreferences(this).edit()
        spe.putBoolean("is_test_mode", value)
        spe.apply()
    }

