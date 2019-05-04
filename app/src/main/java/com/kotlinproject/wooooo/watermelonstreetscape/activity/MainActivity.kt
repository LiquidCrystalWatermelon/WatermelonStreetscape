package com.kotlinproject.wooooo.watermelonstreetscape.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import com.kotlinproject.wooooo.watermelonstreetscape.R
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity_Log"
    private val permissionList = listOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.INTERNET,
        Manifest.permission.CAMERA)
    private val tempPhotoFile by lazy {
        File(Environment.getExternalStorageDirectory().path +
            "/WatermelonStreetScape/" + System.currentTimeMillis() + ".jpg")
    }
    private val allPermissionRequestCode = 2625
    private val albumRequestCode = 2626
    private val cameraRequestCode = 2627

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
        when (menuItem.itemId) {
            R.id.menu_item_select          -> {
                // 选择照片
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, albumRequestCode)
            }
            R.id.menu_item_take_photograph -> {
                // 打开相机
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    Log.i(TAG, ": " + tempPhotoFile.path)
                    FileProvider.getUriForFile(
                        this, "com.kotlinproject.wooooo.watermelonstreetscape.fileprovider", tempPhotoFile)
                } else {
                    Uri.fromFile(tempPhotoFile)
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                startActivityForResult(intent, cameraRequestCode)
            }
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // TODO 处理拿来的照片
        Log.i(TAG, """: $requestCode $resultCode ${data?.data}""")
    }
}
