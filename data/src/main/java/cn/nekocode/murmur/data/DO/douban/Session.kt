package cn.nekocode.murmur.data.DO.douban

import com.google.gson.annotations.SerializedName
import nz.bradcampbell.paperparcel.PaperParcel

/**
 * Created by nekocode on 16/9/15.
 */
@PaperParcel
class Session(
        @SerializedName("access_token") val accessToken: String?,
        @SerializedName("expires_in") val expiresIn: Long,
        @SerializedName("refresh_token") val refreshToken: String?,
        @SerializedName("douban_user_id") val userId: Long,
        val create: Long = 0
)