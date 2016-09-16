package cn.nekocode.murmur.data.dto

import com.google.gson.annotations.SerializedName
import nz.bradcampbell.paperparcel.PaperParcel

/**
 * Created by nekocode on 3/15/16.
 */
object DoubanSong {
    class Ids(
            @SerializedName("songs") val songs: List<Id>
    )

    class Id(
            @SerializedName("playable") val playable: Boolean,
            @SerializedName("sid") val sid: String
    )

    @PaperParcel
    class Song(
            @SerializedName("sid") val id: String,
            @SerializedName("album") val picture: String,
            @SerializedName("artist") val artist: String,
            @SerializedName("title") val title: String,
            @SerializedName("length") val length: Long,
            @SerializedName("url") val url: String
    )
}
