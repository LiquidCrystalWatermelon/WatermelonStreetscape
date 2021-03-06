package com.kotlinproject.wooooo.watermelonstreetscape.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.serialization.Optional
import java.io.Serializable

@kotlinx.serialization.Serializable
data class TextBox(
    var x0: Float,
    var y0: Float,
    var x1: Float,
    var y1: Float,
    var text: String,
    @Optional var degree: Float = 0f,
    @Optional var tag: String? = null
) : Parcelable, Serializable {

    constructor(parcel: Parcel) : this(
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readString(),
        parcel.readFloat(),
        parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(x0)
        parcel.writeFloat(y0)
        parcel.writeFloat(x1)
        parcel.writeFloat(y1)
        parcel.writeString(text)
        parcel.writeFloat(degree)
        parcel.writeString(tag)
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