package com.kotlinproject.wooooo.watermelonstreetscape.utils

import android.app.Activity
import android.content.Context

var Context.spServiceIp: String?
    get() {
        return getSharedPreferences("service_ip", Activity.MODE_PRIVATE)
            .getString("service_ip", null) ?: "192.168.0.16:5000"
    }
    set(value) {
        val spe = getSharedPreferences("service_ip", Activity.MODE_PRIVATE).edit()
        spe.putString("service_ip", value)
        spe.apply()
    }