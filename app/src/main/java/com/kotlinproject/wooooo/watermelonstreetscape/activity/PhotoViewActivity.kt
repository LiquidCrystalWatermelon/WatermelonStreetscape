package com.kotlinproject.wooooo.watermelonstreetscape.activity

import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.davemorrissey.labs.subscaleview.ImageSource
import com.kotlinproject.wooooo.watermelonstreetscape.R
import com.kotlinproject.wooooo.watermelonstreetscape.model.TranslateStreetScape
import com.kotlinproject.wooooo.watermelonstreetscape.utils.StreetScapeUtils
import kotlinx.android.synthetic.main.activity_photo_view.*

class PhotoViewActivity : AppCompatActivity() {
    private lateinit var streetScape: TranslateStreetScape
    private lateinit var textBitmap: Bitmap
    private var isShowText = true

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

        siv_photo_view.setMinimumDpi(40)
        showWithText()
        siv_photo_view.setOnClickListener {
            isShowText = !isShowText
            if (isShowText) showWithText() else showNoText()
        }
    }

    private fun showWithText() {
        siv_photo_view.setImage(ImageSource.bitmap(textBitmap))
    }

    private fun showNoText() {
        siv_photo_view.setImage(ImageSource.uri(streetScape.getPhotoUri(this)))
    }

    companion object {
        const val BUNDLE_STREET_SCAPE = "street_scape"
    }
}
