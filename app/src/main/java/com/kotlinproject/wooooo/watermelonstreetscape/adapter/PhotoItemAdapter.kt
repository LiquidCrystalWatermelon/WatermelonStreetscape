package com.kotlinproject.wooooo.watermelonstreetscape.adapter

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.kotlinproject.wooooo.watermelonstreetscape.R
import com.kotlinproject.wooooo.watermelonstreetscape.activity.MainActivity
import com.kotlinproject.wooooo.watermelonstreetscape.activity.MainEditActivity
import com.kotlinproject.wooooo.watermelonstreetscape.activity.PhotoViewActivity
import com.kotlinproject.wooooo.watermelonstreetscape.model.TranslateStreetScape
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_photo_item.view.*

class PhotoItemAdapter(
    val context: Context,
    val itemList: MutableList<TranslateStreetScape>
) : RecyclerView.Adapter<PhotoItemAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return LayoutInflater
            .from(context)
            .inflate(R.layout.view_photo_item, parent, false)
            .let { ViewHolder(it) }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        with(holder) {
            tvPhotoDescription.text = item.mostImportantText
            Glide.with(context).load(item.photoFile).into(ivPhoto)
            // 短按预览
            itemView.setOnClickListener {
                val intent = Intent(context, PhotoViewActivity::class.java)
                intent.putExtra(PhotoViewActivity.EXTRA_STREET_SCAPE, item as Parcelable)
                context.startActivity(intent)
            }
            // 长按编辑
            itemView.setOnLongClickListener {
                val intent = Intent(context, MainEditActivity::class.java)
                intent.putExtra(MainEditActivity.EXTRA_SELECTED_ITEM_INDEX, position)
                intent.putExtra(MainEditActivity.EXTRA_ITEMS, itemList.toTypedArray())
                intent.putExtra(MainEditActivity.EXTRA_SCROLL_Y,(context as MainActivity).rv_photo_item.computeVerticalScrollOffset())
                context.startActivity(intent)
                true
            }
        }
    }

    override fun getItemCount(): Int = itemList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPhotoDescription = itemView.tv_photo_description
        val ivPhoto = itemView.iv_photo
    }
}