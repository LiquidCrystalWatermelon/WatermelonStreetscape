package com.kotlinproject.wooooo.watermelonstreetscape.activity

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import com.kotlinproject.wooooo.watermelonstreetscape.R
import com.kotlinproject.wooooo.watermelonstreetscape.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val permissionList = listOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.INTERNET,
        Manifest.permission.CAMERA)
    private val allPermissionRequestCode = 2625

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()
    }

    private fun checkPermission() {
        val toRequestPermissions = permissionList.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (toRequestPermissions.isEmpty()) {
            onAllPermissionGranted()
        } else {
            ActivityCompat.requestPermissions(
                this, toRequestPermissions.toTypedArray(), allPermissionRequestCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == allPermissionRequestCode) {
            val deniedPermissions = permissions
                .zip(grantResults.toTypedArray())
                .filter { it.second != PackageManager.PERMISSION_GRANTED }
                .map { it.first }
            if (deniedPermissions.isEmpty()) {
                onAllPermissionGranted()
            } else {
                finish()
            }
        }
    }

    private fun onAllPermissionGranted() {
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
        ToastUtils.showTextShort(this, menuItem.itemId)
        when (menuItem.itemId) {
            R.id.menu_item_select          -> {
                ToastUtils.showTextShort(this, "从相册中选择")
                // TODO 选择照片
            }
            R.id.menu_item_take_photograph -> {
                ToastUtils.showTextShort(this, "拍照")
                // TODO 打开相机
            }
        }
        return false
    }
}
