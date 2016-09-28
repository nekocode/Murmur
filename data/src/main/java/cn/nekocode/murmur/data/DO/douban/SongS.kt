package cn.nekocode.murmur.data.DO.douban

import com.google.gson.annotations.SerializedName
import nz.bradcampbell.paperparcel.PaperParcel

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
object SongS {
    internal class Ids(
            @SerializedName("songs") val songs: List<Id>
    )

    internal class Id(
            @SerializedName("playable") val playable: Boolean,
            @SerializedName("sid") val sid: String
    )

    @PaperParcel
    class Song(
            @SerializedName("sid") val id: String,
            @SerializedName("picture") val picture: String,
            @SerializedName("artist") val artist: String,
            @SerializedName("title") val title: String,
            @SerializedName("length") val length: Long,
            @SerializedName("url") val url: String
    )
}
