package cn.nekocode.murmur.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Created by nekocode on 3/13/16.
 */
data class LeanCloudFile(
        @SerializedName("objectId") val id: String,
        @SerializedName("mime_type") var mimeType: String,
        var url: String
)

data class Murmur(
        @SerializedName("objectId") val id: String,
        var name: String,
        var file: LeanCloudFile
)