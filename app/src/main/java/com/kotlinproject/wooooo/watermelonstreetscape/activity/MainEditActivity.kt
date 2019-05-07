package com.kotlinproject.wooooo.watermelonstreetscape.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.kotlinproject.wooooo.watermelonstreetscape.R
import com.kotlinproject.wooooo.watermelonstreetscape.adapter.EditPhotoItemAdapter
import kotlinx.android.synthetic.main.activity_main_edit.*
import kotlinx.android.synthetic.main.include_app_bar.*

class MainEditActivity : AppCompatActivity() {

    private val adapter by lazy { EditPhotoItemAdapter(this, mutableListOf()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_edit)

        // toolbar
        setSupportActionBar(toolbar_app)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitleCount(1)
        toolbar_app.setNavigationOnClickListener { finish() }

        // 取出数据
//        val
    }

    private fun setTitleCount(count: Int) {
        supportActionBar?.title = "已选择 $count 项"
    }

    companion object {
        const val EXTRA_SELECTED_ITEM_INDEX = "selected_item_index"
        const val EXTRA_ITEMS = "items"
        const val EXTRA_SCROLL_Y = "scroll_y"
    }
}
