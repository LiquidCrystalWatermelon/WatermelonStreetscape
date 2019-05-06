package com.kotlinproject.wooooo.watermelonstreetscape.activity

import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.davemorrissey.labs.subscaleview.ImageSource
import com.kotlinproject.wooooo.watermelonstreetscape.R
import com.kotlinproject.wooooo.watermelonstreetscape.model.TranslateStreetScape
import com.kotlinproject.wooooo.watermelonstreetscape.utils.StreetScapeUtils
import kotlinx.android.synthetic.main.activity_photo_view.*
import kotlin.concurrent.thread

class PhotoViewActivity : AppCompatActivity() {
    private val TAG = "PhotoViewActivity_Log"
    private lateinit var streetScape: TranslateStreetScape
    private lateinit var textBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_view)
        streetScape = intent.getParcelableExtra(BUNDLE_STREET_SCAPE)

        setSupportActionBar(toolbar_photo_view)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = streetScape.mostImportantText
        }
        toolbar_photo_view.setNavigationOnClickListener { finish() }

        textBitmap = StreetScapeUtils.draw(streetScape)

        siv_photo_view_with_text.setMinimumDpi(40)
        siv_photo_view_no_text.setMinimumDpi(40)
        siv_photo_view_with_text.setImage(ImageSource.bitmap(textBitmap))
        siv_photo_view_no_text.setImage(ImageSource.uri(streetScape.getPhotoUri(this)))

        siv_photo_view_with_text.setOnClickListener { showNoText() }
        siv_photo_view_no_text.setOnClickListener { showWithText() }

        val gesture = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent?) {
                if (e == null) return
                val pointf = siv_photo_view_with_text.viewToSourceCoord(e.x, e.y)!!
                Log.i(TAG, ":position ${pointf.x} ${pointf.y}")
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

    private fun showWithText() {
        val (scale, center) = siv_photo_view_no_text.let { it.scale to it.center }
        siv_photo_view_with_text.setScaleAndCenter(scale, center)
        siv_photo_view_with_text.visibility = View.VISIBLE
        siv_photo_view_no_text.visibility = View.GONE
    }

    private fun showNoText() {
        val (scale, center) = siv_photo_view_with_text.let { it.scale to it.center }
        siv_photo_view_no_text.setScaleAndCenter(scale, center)
        siv_photo_view_with_text.visibility = View.GONE
        siv_photo_view_no_text.visibility = View.VISIBLE
    }

    companion object {
        const val BUNDLE_STREET_SCAPE = "street_scape"
    }
}
