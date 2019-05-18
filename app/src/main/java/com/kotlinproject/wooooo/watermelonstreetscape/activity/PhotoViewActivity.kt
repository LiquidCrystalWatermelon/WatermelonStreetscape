package com.kotlinproject.wooooo.watermelonstreetscape.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewAnimationUtils
import com.davemorrissey.labs.subscaleview.ImageSource
import com.kotlinproject.wooooo.watermelonstreetscape.R
import com.kotlinproject.wooooo.watermelonstreetscape.model.TranslateStreetScape
import com.kotlinproject.wooooo.watermelonstreetscape.utils.StreetScapeUtils
import com.kotlinproject.wooooo.watermelonstreetscape.utils.ToastUtils
import com.kotlinproject.wooooo.watermelonstreetscape.utils.toast
import kotlinx.android.synthetic.main.activity_photo_view.*
import kotlinx.android.synthetic.main.include_app_bar.*
import kotlin.concurrent.thread
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class PhotoViewActivity : AppCompatActivity() {
    private val TAG = "PhotoViewActivity_Log"
    private lateinit var streetScape: TranslateStreetScape
    private lateinit var textBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_view)

        streetScape = intent.getParcelableExtra(EXTRA_STREET_SCAPE)

        // toolbar
        setSupportActionBar(toolbar_app)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = streetScape.mostImportantText
        }
        toolbar_app.setNavigationOnClickListener { finish() }

        if (!streetScape.photoFile.exists()) {
            toast("找不到图片 ${streetScape.mostImportantText}")
            finish()
            return
        }

        // 获取写上文字的图片
        textBitmap = StreetScapeUtils.draw(
            streetScape, showDarkMask = false, showTextBorder = true
        )

        siv_photo_view_with_text.setMinimumDpi(40)
        siv_photo_view_no_text.setMinimumDpi(40)
        siv_photo_view_with_text.setImage(ImageSource.bitmap(textBitmap))
        siv_photo_view_no_text.setImage(ImageSource.uri(streetScape.getPhotoUri(this)))

        siv_photo_view_with_text.setOnClickListener { showNoText() }
        siv_photo_view_no_text.setOnClickListener { showWithText() }

        // 用于文本弹出卡片的动画，获取中点和半径
        fun viewCenterR(view: View): Triple<Int, Int, Float> {
            val w = view.width
            val h = view.height
            val x = w / 2
            val y = h / 2
            val r = sqrt((w * w + h * h).toFloat())
            return Triple(x, y, r)
        }

        view_card_mask.setOnClickListener {
            val (x, y, r) = viewCenterR(card_info)
            ViewAnimationUtils
                .createCircularReveal(card_info, x, y, r, 0f)
                .apply {
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(p0: Animator?) {
                            rl_card.visibility = View.GONE
                        }
                    })
                }
                .setDuration(500)
                .start()
        }

        // 长按手势
        val gesture = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent?) {
                if (e == null) return
                val (x, y) = siv_photo_view_with_text
                    .viewToSourceCoord(e.x, e.y)!!
                    .let { it.x to it.y }
                streetScape
                    .textBoxList
                    .filter {
                        val cx = (it.x0 + it.x1) / 2
                        val cy = (it.y0 + it.y1) / 2
                        val angle = -PI * it.degree / 180
                        val mapX = (x - cx) * cos(angle) - (y - cy) * sin(angle) + cx
                        val mapY = (x - cx) * sin(angle) + (y - cy) * cos(angle) + cy
                        mapX in it.x0..it.x1 && mapY in it.y0..it.y1
                    }
                    .joinToString("\n\n") { it.text }
                    .let {
                        if (it.isNotBlank()) {
                            rl_card.visibility = View.VISIBLE
                            tv_card.text = it
                            val (cx, cy, r) = viewCenterR(card_info)
                            Log.i(TAG, ":card $cx $cy $r")
                            ViewAnimationUtils
                                .createCircularReveal(card_info, cx, cy, 0f, r)
                                .setDuration(500)
                                .start()
                        }
                    }
            }
        })

        gesture.setIsLongpressEnabled(true)

        siv_photo_view_with_text.setOnTouchListener { view, motionEvent ->
            gesture.onTouchEvent(motionEvent)
        }

        showNoText()
        thread {
            Thread.sleep(300)
            runOnUiThread { showWithText() }
        }
    }

    // 显示文字
    private fun showWithText() {
        val (scale, center) = siv_photo_view_no_text.let { it.scale to it.center }
        siv_photo_view_with_text.setScaleAndCenter(scale, center)
        siv_photo_view_with_text.visibility = View.VISIBLE
        siv_photo_view_no_text.visibility = View.GONE
    }

    // 不显示文字
    private fun showNoText() {
        val (scale, center) = siv_photo_view_with_text.let { it.scale to it.center }
        siv_photo_view_no_text.setScaleAndCenter(scale, center)
        siv_photo_view_with_text.visibility = View.GONE
        siv_photo_view_no_text.visibility = View.VISIBLE
    }

    companion object {
        const val EXTRA_STREET_SCAPE = "street_scape"
    }
}
