package cn.nekocode.murmur.data.dto

import java.util.*
import android.os.Parcel
import android.os.Parcelable

import com.google.gson.annotations.SerializedName

/**
 * Created by nekocode on 3/15/16.
 */
data class DoubanUser(
        @SerializedName("user_id") val id: String,
        val token: String,
        val expire: Long,
        @SerializedName("user_name") val username: String,
        val err: String
) : Parcelable {
    constructor(source: Parcel): this(source.readString(), source.readString(), source.readLong(), source.readString(), source.readString())

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(id)
        dest?.writeString(token)
        dest?.writeLong(expire)
        dest?.writeString(username)
        dest?.writeString(err)
    }

    companion object {
        @JvmField final val CREATOR: Parcelable.Creator<DoubanUser> = object : Parcelable.Creator<DoubanUser> {
            override fun createFromParcel(source: Parcel): DoubanUser {
                return DoubanUser(source)
            }

            override fun newArray(size: Int): Array<DoubanUser?> {
                return arrayOfNulls(size)
            }
        }
    }
}