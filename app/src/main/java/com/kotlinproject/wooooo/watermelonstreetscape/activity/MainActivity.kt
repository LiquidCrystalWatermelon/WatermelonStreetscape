package com.kotlinproject.wooooo.watermelonstreetscape.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import com.kotlinproject.wooooo.watermelonstreetscape.R
import com.kotlinproject.wooooo.watermelonstreetscape.adapter.PhotoItemAdapter
import com.kotlinproject.wooooo.watermelonstreetscape.http.HttpCallback
import com.kotlinproject.wooooo.watermelonstreetscape.http.HttpClient
import com.kotlinproject.wooooo.watermelonstreetscape.model.TranslateStreetScape
import com.kotlinproject.wooooo.watermelonstreetscape.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import android.support.v7.widget.RecyclerView


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity_Log"

    /** 权限列表 */
    private val permissionList = listOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.INTERNET,
        Manifest.permission.CAMERA)

    /** 拍照临时图片存放 uri */
    private val tempPhotoUri by lazy {
        val file = File(Environment.getExternalStorageDirectory().path +
            "/WatermelonStreetScape/image/temp.jpg")
        file.parentFile.let {
            if (!it.exists()) it.mkdirs()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            FileProvider.getUriForFile(
                this, "com.kotlinproject.wooooo.watermelonstreetscape.fileprovider", file)
        } else {
            Uri.fromFile(file)
        }
    }

    // requestCode
    private val allPermissionRequestCode = 2625
    private val albumRequestCode = 2626
    private val cameraRequestCode = 2627

    /** 适配器 */
    private val adapter by lazy { PhotoItemAdapter(this, mutableListOf()) }

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
        title="西瓜街景"
        fab_take_photo.setOnClickListener(this::onFabClick)
        rv_photo_item.adapter = adapter
        rv_photo_item.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val threshold = 50
                if (dy > threshold) fabHide()
                if (dy < 0) fabShow()
            }
        })
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
        popup.setOnDismissListener { fabShow() }
        popup.show()
        fabHidePopup()
    }

    private fun fabMoveY(y: Float) = fab_take_photo.animate().translationY(y).start()
    private fun fabHide() = fabMoveY(250f)
    private fun fabHidePopup() = fabMoveY(-200f)
    private fun fabShow() = fabMoveY(0f)

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
                intent.putExtra(MediaStore.EXTRA_OUTPUT, tempPhotoUri)
                startActivityForResult(intent, cameraRequestCode)
            }
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // 更新在首页列表中

        val uri = if (requestCode == albumRequestCode) data?.data else tempPhotoUri
        val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))

        HttpClient.uploadImage(bitmap, object : HttpCallback<TranslateStreetScape> {
            override fun onFailure(e: IOException?) {
                ToastUtils.showTextShort(this@MainActivity, "图像上传失败")
            }

            override fun onResponse(item: TranslateStreetScape) {
                // 加入列表
                adapter.itemList.add(item)
                adapter.notifyDataSetChanged()
            }
        })
    }
}
