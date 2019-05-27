package com.kotlinproject.wooooo.watermelonstreetscape.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.kotlinproject.wooooo.watermelonstreetscape.R
import kotlinx.android.synthetic.main.include_app_bar.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        setSupportActionBar(toolbar_app)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "关于西瓜街景"
        }
        toolbar_app.setNavigationOnClickListener { finish() }
    }
}
