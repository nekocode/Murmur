package cn.nekocode.murmur.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Created by nekocode on 16/9/15.
 */
class DoubanSession(
        @SerializedName("access_token") val accessToken: String?,
        @SerializedName("expires_in") val expiresIn: Long,
        @SerializedName("refresh_token") val refreshToken: String?,
        @SerializedName("douban_user_id") val userId: Long,
        val create: Long = 0
)