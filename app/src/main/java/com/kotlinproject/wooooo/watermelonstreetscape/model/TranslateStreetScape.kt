package com.kotlinproject.wooooo.watermelonstreetscape.model

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable

data class TranslateStreetScape(
    val bitmap: Bitmap,
    val textBoxList: List<TextBox>
) : Parcelable {
    val mostImportantText by lazy {
        textBoxList.maxBy { Math.abs(it.x1 - it.x0) * Math.abs(it.y1 - it.y0) }
            ?.text ?: ""
    }

    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Bitmap::class.java.classLoader),
        parcel.createTypedArrayList(TextBox))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(bitmap, flags)
        parcel.writeTypedList(textBoxList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TranslateStreetScape> {
        override fun createFromParcel(parcel: Parcel): TranslateStreetScape {
            return TranslateStreetScape(parcel)
        }

        override fun newArray(size: Int): Array<TranslateStreetScape?> {
            return arrayOfNulls(size)
        }
    }
}