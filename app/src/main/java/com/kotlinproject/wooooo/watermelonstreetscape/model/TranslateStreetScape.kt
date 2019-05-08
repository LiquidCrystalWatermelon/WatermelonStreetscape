package com.kotlinproject.wooooo.watermelonstreetscape.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.kotlinproject.wooooo.watermelonstreetscape.utils.FileUtils
import java.io.File
import java.io.Serializable

data class TranslateStreetScape(
    val photoPath: String,
    val textBoxList: List<TextBox>,
    val timeStamp: Long = System.currentTimeMillis()
) : Parcelable, Serializable {

    val mostImportantText by lazy {
        textBoxList.maxBy { Math.abs(it.x1 - it.x0) * Math.abs(it.y1 - it.y0) }
            ?.text ?: ""
    }

    val photoFile by lazy { File(photoPath) }

    @Transient
    var scapeFile: File? = null

    fun getPhotoUri(context: Context) = FileUtils.fileToUri(context, photoFile)

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.createTypedArrayList(TextBox),
        parcel.readLong())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(photoPath)
        parcel.writeTypedList(textBoxList)
        parcel.writeLong(timeStamp)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<TranslateStreetScape> {
        private const val serialVersionUID = -363304808118067478
        override fun createFromParcel(parcel: Parcel): TranslateStreetScape {
            return TranslateStreetScape(parcel)
        }

        override fun newArray(size: Int): Array<TranslateStreetScape?> {
            return arrayOfNulls(size)
        }
    }
}