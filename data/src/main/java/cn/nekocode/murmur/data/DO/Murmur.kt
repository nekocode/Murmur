package cn.nekocode.murmur.data.DO

import com.google.gson.annotations.SerializedName
import nz.bradcampbell.paperparcel.PaperParcel

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
@PaperParcel
class Murmur(
        @SerializedName("objectId") val id: String,
        var name: String,
        var file: LeanCloudFile
)