package com.kotlinproject.wooooo.watermelonstreetscape.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.kotlinproject.wooooo.watermelonstreetscape.utils.FileUtils
import java.io.File

data class TranslateStreetScape(
    val photoPath: String,
    val textBoxList: List<TextBox>
) : Parcelable {
    val mostImportantText by lazy {
        textBoxList.maxBy { Math.abs(it.x1 - it.x0) * Math.abs(it.y1 - it.y0) }
            ?.text ?: ""
    }

    val photoFile by lazy { File(photoPath) }

    fun getPhotoUri(context: Context) = FileUtils.fileToUri(context, photoFile)

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.createTypedArrayList(TextBox))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(photoPath)
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