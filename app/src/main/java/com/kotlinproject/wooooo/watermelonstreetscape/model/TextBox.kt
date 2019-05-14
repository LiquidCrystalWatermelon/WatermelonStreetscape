package com.kotlinproject.wooooo.watermelonstreetscape.model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

@kotlinx.serialization.Serializable
data class TextBox(
    val x0: Float,
    val y0: Float,
    val x1: Float,
    val y1: Float,
    val text: String,
    val degree: Float = 0f
) : Parcelable, Serializable {

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
        private const val serialVersionUID = 4380838812587594199

        override fun createFromParcel(parcel: Parcel): TextBox {
            return TextBox(parcel)
        }

        override fun newArray(size: Int): Array<TextBox?> {
            return arrayOfNulls(size)
        }
    }
}