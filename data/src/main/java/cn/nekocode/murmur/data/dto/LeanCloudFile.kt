package cn.nekocode.murmur.data.dto

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by nekocode on 16/9/16.
 */
data class LeanCloudFile(
        @SerializedName("objectId") val id: String,
        @SerializedName("mime_type") var mimeType: String,
        var url: String
) : Parcelable {
    constructor(source: Parcel): this(source.readString(), source.readString(), source.readString())

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(id)
        dest?.writeString(mimeType)
        dest?.writeString(url)
    }

    companion object {
        @JvmField final val CREATOR: Parcelable.Creator<LeanCloudFile> = object : Parcelable.Creator<LeanCloudFile> {
            override fun createFromParcel(source: Parcel): LeanCloudFile {
                return LeanCloudFile(source)
            }

            override fun newArray(size: Int): Array<LeanCloudFile?> {
                return arrayOfNulls(size)
            }
        }
    }
}