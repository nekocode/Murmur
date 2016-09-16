package cn.nekocode.murmur.data.DO

import com.google.gson.annotations.SerializedName
import nz.bradcampbell.paperparcel.PaperParcel

/**
 * Created by nekocode on 16/9/16.
 */
@PaperParcel
class LeanCloudFile(
        @SerializedName("objectId") val id: String,
        @SerializedName("mime_type") var mimeType: String,
        var url: String
)