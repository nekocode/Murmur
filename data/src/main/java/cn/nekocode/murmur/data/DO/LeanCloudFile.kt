package cn.nekocode.murmur.data.DO

import com.google.gson.annotations.SerializedName
import nz.bradcampbell.paperparcel.PaperParcel

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
@PaperParcel
class LeanCloudFile(
        @SerializedName("objectId") val id: String,
        @SerializedName("mime_type") var mimeType: String,
        var url: String
)