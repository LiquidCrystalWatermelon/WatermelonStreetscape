package com.kotlinproject.wooooo.watermelonstreetscape.model

import android.os.Parcel
import android.os.Parcelable

data class TextBox(
    val x0: Float,
    val y0: Float,
    val x1: Float,
    val y1: Float,
    val text: String,
    val degree: Float = 0f
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readString(),
        parcel.readFloat())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(x0)
        parcel.writeFloat(y0)
        parcel.writeFloat(x1)
        parcel.writeFloat(y1)
        parcel.writeString(text)
        parcel.writeFloat(degree)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<TextBox> {
        override fun createFromParcel(parcel: Parcel): TextBox {
            return TextBox(parcel)
        }

        override fun newArray(size: Int): Array<TextBox?> {
            return arrayOfNulls(size)
        }
    }
}