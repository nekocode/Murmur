package cn.nekocode.murmur.data.DO

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by nekocode on 3/13/16.
 */
data class Murmur(
        @SerializedName("objectId") val id: String,
        var name: String,
        var file: LeanCloudFile
) : Parcelable {
    constructor(source: Parcel): this(source.readString(), source.readString(), source.readParcelable<LeanCloudFile>(LeanCloudFile::class.java.classLoader))

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(id)
        dest?.writeString(name)
        dest?.writeParcelable(file, 0)
    }

    companion object {
        @JvmField final val CREATOR: Parcelable.Creator<Murmur> = object : Parcelable.Creator<Murmur> {
            override fun createFromParcel(source: Parcel): Murmur {
                return Murmur(source)
            }

            override fun newArray(size: Int): Array<Murmur?> {
                return arrayOfNulls(size)
            }
        }
    }
}