package com.kotlinproject.wooooo.watermelonstreetscape.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.kotlinproject.wooooo.watermelonstreetscape.R
import com.kotlinproject.wooooo.watermelonstreetscape.adapter.EditPhotoItemAdapter
import com.kotlinproject.wooooo.watermelonstreetscape.model.TranslateStreetScape
import kotlinx.android.synthetic.main.activity_main_edit.*
import kotlinx.android.synthetic.main.include_app_bar.*
import kotlin.concurrent.thread

class MainEditActivity : AppCompatActivity() {
    private val TAG = "MainEditActivity_Log"

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
        val items = intent.getParcelableArrayExtra(EXTRA_ITEMS).map { it as TranslateStreetScape }
        val selectedIndex = intent.getIntExtra(EXTRA_SELECTED_ITEM_INDEX, 0)
        val scrollY = intent.getIntExtra(EXTRA_SCROLL_Y, 0)

        // 配置列表
        adapter.itemList.addAll(items)
        adapter.selectedItemSet.add(items[selectedIndex])
        adapter.setSelectedCountChangedListener {
            setTitleCount(it)
        }
        rv_photo_item.adapter = adapter
        thread { runOnUiThread { rv_photo_item.scrollBy(0, scrollY) } }
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
