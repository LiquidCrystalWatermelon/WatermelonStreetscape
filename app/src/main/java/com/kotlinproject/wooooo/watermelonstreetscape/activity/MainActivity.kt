package com.kotlinproject.wooooo.watermelonstreetscape.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
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
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.EditText
import com.kotlinproject.wooooo.watermelonstreetscape.utils.FileUtils
import com.kotlinproject.wooooo.watermelonstreetscape.utils.spServiceIp
import kotlinx.serialization.ImplicitReflectionSerializer
import java.io.*

/*
 * 相机和相册接收或返回的格式均是 uri
 * Glide 支持 uri file bitmap
 * OkHttp 上传仅支持 file
 * 总之一定要有一个 uri 转 file 的过程
 */


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
        val file = File(imageFilePath("temp"))
        file.parentFile.let {
            if (!it.exists()) it.mkdirs()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            FileUtils.fileToUri(this, file)
        } else {
            Uri.fromFile(file)
        }
    }


    /** 适配器 */
    private val adapter by lazy { PhotoItemAdapter(this, mutableListOf()) }

    private val appDataFilePath = Environment
        .getExternalStorageDirectory().path + "/WatermelonStreetScape"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()
    }

    /** 检查权限 */
    private fun checkPermission() {
        val toRequestPermissions = permissionList.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (toRequestPermissions.isEmpty()) {
            onAllPermissionGranted()
        } else {
            ActivityCompat.requestPermissions(
                this, toRequestPermissions.toTypedArray(), REQUEST_ALL_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_ALL_PERMISSION) {
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

    /** 获得所有所需权限 */
    private fun onAllPermissionGranted() {
        init()
    }

    private fun init() {
        title = getString(R.string.app_name_cn)
        fab_take_photo.isLongClickable = true
        fab_take_photo.setOnClickListener(::onFabClick)
        fab_take_photo.setOnLongClickListener(::onFabLongClick)
        rv_photo_item.adapter = adapter
        rv_photo_item.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val threshold = 50
                if (dy > threshold) fabHide()
                if (dy < 0) fabShow()
            }
        })
        // 从本地读取街景文件
        File("$appDataFilePath/scape/")
            .takeIf { it.exists() }
            ?.listFiles { _, str -> str.endsWith(".sca") }
            ?.map {
                ObjectInputStream(FileInputStream(it))
                    .use { it.readObject() as TranslateStreetScape }
                    .apply { scapeFile = it }
            }
            ?.sortedByDescending { it.timeStamp }
            ?.let { adapter.itemList.addAll(it) }
    }

    private fun onFabLongClick(view: View): Boolean {
        val et = EditText(this)
        et.setText(spServiceIp)
        AlertDialog
            .Builder(this)
            .setTitle("修改服务器ip地址：")
            .setView(et)
            .setNegativeButton("取消", null)
            .setPositiveButton("确定") { _, _ -> spServiceIp = et.text.toString() }
            .show()
        return true
    }

    private fun onFabClick(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.menu_popup_take_photo, popup.menu)
        popup.setOnMenuItemClickListener(this::onMenuItemClick)
        popup.setOnDismissListener { fabShow() }
        popup.show()
        fabHidePopup()
    }

    // 浮动按钮动画
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
                startActivityForResult(intent, REQUEST_ALBUM)
            }
            R.id.menu_item_take_photograph -> {
                // 打开相机
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, tempPhotoUri)
                startActivityForResult(intent, REQUEST_CAMERA)
            }
        }
        return false
    }

    @ImplicitReflectionSerializer
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) return
        when (requestCode) {
            REQUEST_ALBUM, REQUEST_CAMERA ->
                onSelectPhotoResult(requestCode, resultCode, data)
            REQUEST_MULTI_DELETE          ->
                onMultiDeleteResult(requestCode, resultCode, data)
        }
    }

    @ImplicitReflectionSerializer
    private fun onSelectPhotoResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // 更新在首页列表中
        pb_uploading.visibility = View.VISIBLE
        val uri = if (requestCode == REQUEST_ALBUM) data?.data else tempPhotoUri
        val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))

        val filePath = imageFilePath(System.currentTimeMillis())
        File(filePath).parentFile.let {
            if (!it.exists()) it.mkdirs()
        }
        FileOutputStream(filePath).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            it.flush()
        }

        HttpClient.uploadImage(this, filePath, object : HttpCallback<TranslateStreetScape> {
            override fun onFailure(e: Exception?) {
                pb_uploading.visibility = View.GONE
                ToastUtils.showTextShort(this@MainActivity, "图像上传失败")
            }

            override fun onResponse(item: TranslateStreetScape) {
                pb_uploading.visibility = View.GONE
                // 写入本地
                val objFile = File(scapeFilePath(item.timeStamp))
                objFile.parentFile.let {
                    if (!it.exists()) it.mkdirs()
                }
                ObjectOutputStream(FileOutputStream(objFile)).use {
                    it.writeObject(item)
                }
                item.scapeFile = objFile

                // 加入列表
                adapter.itemList.add(0, item)
                adapter.notifyItemInserted(0)
                adapter.notifyItemRangeChanged(0, adapter.itemCount)
                rv_photo_item.scrollToPosition(0)
//                adapter.notifyDataSetChanged()
                Log.i(TAG, ": TranslateStreetScape ${item.timeStamp}")
            }
        })
    }

    private fun onMultiDeleteResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null) return

        // 滚动同步
        val scrollY = data.getIntExtra(EXTRA_SCROLL_Y, -1)
        Log.d(TAG, ": scrollY: $scrollY")
        if (scrollY >= 0) {
            val curY = rv_photo_item.computeVerticalScrollOffset()
            rv_photo_item.scrollBy(0, scrollY - curY)
        }
        // 移除元素
        val indexes = data.getIntArrayExtra(EXTRA_DELETE_ITEMS_INDEX)
        indexes
            ?.takeIf { it.isNotEmpty() }
            ?.map {
                adapter.notifyItemRemoved(it)
                Log.d(TAG, ": remove index :$it")
                adapter.itemList[it]
            }
            ?.apply { adapter.itemList.removeAll(this) }
            ?.forEach {
                it.photoFile.apply { if (exists()) delete() }
                it.scapeFile?.apply { if (exists()) delete() }
            }
            ?.let { adapter.notifyItemRangeChanged(indexes.min()!!, indexes.size) }
    }

    private fun imageFilePath(imgName: Any) = "$appDataFilePath/image/$imgName.jpg"

    private fun scapeFilePath(scapeName: Any) = "$appDataFilePath/scape/$scapeName.sca"

    companion object {
        // requestCode
        const val REQUEST_ALL_PERMISSION = 2625
        const val REQUEST_ALBUM = 2626
        const val REQUEST_CAMERA = 2627
        const val REQUEST_MULTI_DELETE = 2628

        // extra
        const val EXTRA_SCROLL_Y = "scroll_y"
        const val EXTRA_DELETE_ITEMS_INDEX = "delete_items_index"
    }
}
