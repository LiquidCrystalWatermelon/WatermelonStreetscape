package com.kotlinproject.wooooo.watermelonstreetscape.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.kotlinproject.wooooo.watermelonstreetscape.R
import com.kotlinproject.wooooo.watermelonstreetscape.model.TranslateStreetScape
import kotlinx.android.synthetic.main.view_photo_item.view.*

class EditPhotoItemAdapter(
    val context: Context,
    val itemList: MutableList<TranslateStreetScape>
) : RecyclerView.Adapter<EditPhotoItemAdapter.ViewHolder>() {

    val selectedItemSet = mutableSetOf<TranslateStreetScape>()

    private var countChangedListener: ((selectedCount: Int) -> Unit)? = null

    fun setSelectedCountChangedListener(listener: ((selectedCount: Int) -> Unit)?) {
        this.countChangedListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditPhotoItemAdapter.ViewHolder {
        return LayoutInflater
            .from(context)
            .inflate(R.layout.view_photo_item, parent, false)
            .let { ViewHolder(it) }
    }

    override fun onBindViewHolder(holder: EditPhotoItemAdapter.ViewHolder, position: Int) {
        val item = itemList[position]
        with(holder) {
            // 隐藏文本框
            tvPhotoDescription.visibility = View.GONE

            // 根据选择状态指定显示元素
            fun View.v(show: Boolean) {
                visibility = if (show) View.VISIBLE else View.GONE
            }

            fun changeState(selected: Boolean) {
                viewUnselectedMask.v(selected)
                ivBoxSelected.v(selected)
                ivBoxUnselected.v(!selected)
            }

            changeState(item in selectedItemSet)

            Glide.with(context).load(item.photoFile).into(ivPhoto)

            itemView.setOnClickListener {
                // 切换选择状态
                val selected = item in selectedItemSet
                if (selected) selectedItemSet.remove(item)
                else selectedItemSet.add(item)
                changeState(!selected)
                notifyCountListener()
            }
        }
    }

    override fun getItemCount(): Int = itemList.size

    fun selectAll() {
        selectedItemSet.addAll(itemList)
        notifyDataSetChanged()
        notifyCountListener()
    }

    fun unselectAll() {
        selectedItemSet.clear()
        notifyDataSetChanged()
        notifyCountListener()
    }

    private fun notifyCountListener() {
        countChangedListener?.invoke(selectedItemSet.size)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPhotoDescription = itemView.tv_photo_description
        val ivPhoto = itemView.iv_photo
        val viewUnselectedMask = itemView.view_unselected_mask
        val ivBoxSelected = itemView.iv_edit_check_box_selected
        val ivBoxUnselected = itemView.iv_edit_check_box_unselected
    }
}