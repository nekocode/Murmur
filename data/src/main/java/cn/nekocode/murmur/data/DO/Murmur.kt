package cn.nekocode.murmur.data.DO

import com.google.gson.annotations.SerializedName
import nz.bradcampbell.paperparcel.PaperParcel

/**
 * Created by nekocode on 3/13/16.
 */
@PaperParcel
class Murmur(
        @SerializedName("objectId") val id: String,
        var name: String,
        var file: LeanCloudFile
)