package cn.nekocode.murmur.data.dto

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by nekocode on 3/15/16.
 */
data class DoubanSongWrapper(
        @SerializedName("r") val status: Int,
        val song: List<DoubanSong>,
        val err: String
)

data class DoubanSong(
        @SerializedName("ssid") val id: String,
        val picture: String,
        val artist: String,
        val title: String,
        val length: Long,
        val url: String
) : Parcelable {
    constructor(source: Parcel): this(source.readString(), source.readString(), source.readString(), source.readString(), source.readLong(), source.readString())

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(id)
        dest?.writeString(picture)
        dest?.writeString(artist)
        dest?.writeString(title)
        dest?.writeLong(length)
        dest?.writeString(url)
    }

    companion object {
        @JvmField final val CREATOR: Parcelable.Creator<DoubanSong> = object : Parcelable.Creator<DoubanSong> {
            override fun createFromParcel(source: Parcel): DoubanSong {
                return DoubanSong(source)
            }

            override fun newArray(size: Int): Array<DoubanSong?> {
                return arrayOfNulls(size)
            }
        }
    }
}