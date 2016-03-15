package cn.nekocode.murmur.data.dto

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
)

//{
//    "user_id": "<user_id>",
//    "err": "ok",
//    "token": "<token_string>",
//    "expire": "<expire_time_in_millisecond>",
//    "r": 0,
//    "user_name": "<user_name>",
//    "email": "<user_account>"
//}