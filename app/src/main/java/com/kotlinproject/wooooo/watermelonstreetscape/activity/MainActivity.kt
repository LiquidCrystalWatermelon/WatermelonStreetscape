package com.kotlinproject.wooooo.watermelonstreetscape.activity

import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import com.kotlinproject.wooooo.watermelonstreetscape.R
import com.kotlinproject.wooooo.watermelonstreetscape.adapter.PhotoItemAdapter
import com.kotlinproject.wooooo.watermelonstreetscape.model.TextBox
import com.kotlinproject.wooooo.watermelonstreetscape.model.TranslateStreetScape
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        fab_take_photo.setOnClickListener(this::onFabClick)
//        val res = application.resources
//        // TODO 图片超糊的，不知道为什么
//        val bitmap = BitmapFactory.decodeResource(res, R.mipmap._388059)
//        val adapter = PhotoItemAdapter(this, Array(20) { TranslateStreetScape(bitmap, listOf(TextBox(0f, 0f, 10f, 10f, "abc"))) }.toMutableList())
//        rv_photo_item.adapter = adapter
    }


    private fun onFabClick(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.menu_popup_take_photo, popup.menu)
        popup.setOnMenuItemClickListener(this::onMenuItemClick)
        popup.show()
    }

    private fun onMenuItemClick(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.menu_item_select          -> Unit // TODO 选择照片
            R.id.menu_item_take_photograph -> Unit // TODO 打开相机
        }
        return false
    }
}
