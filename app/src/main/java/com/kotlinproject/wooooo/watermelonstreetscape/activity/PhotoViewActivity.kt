package com.kotlinproject.wooooo.watermelonstreetscape.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.davemorrissey.labs.subscaleview.ImageSource
import com.kotlinproject.wooooo.watermelonstreetscape.R
import com.kotlinproject.wooooo.watermelonstreetscape.model.TranslateStreetScape
import kotlinx.android.synthetic.main.activity_photo_view.*

class PhotoViewActivity : AppCompatActivity() {
    lateinit var streetScape: TranslateStreetScape

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_view)
        savedInstanceState?.let { streetScape = it.get(BUNDLE_STREET_SCAPE) as TranslateStreetScape }
        siv_photo_view.setImage(ImageSource.bitmap(streetScape.bitmap))
    }

    companion object {
        const val BUNDLE_STREET_SCAPE = "street_scape"
    }
}
