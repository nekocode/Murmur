package cn.nekocode.murmur.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Created by nekocode on 3/15/16.
 */
data class DoubanSongWrapper(
        @SerializedName("r") val status: Int,
        val song: List<DoubanSong>,
        val err: String
)

data class DoubanSong(
        @SerializedName("ssid") val id: String,
        val picture: String,
        val artist: String,
        val title: String,
        val length: Long,
        val url: String
)

//{
//    "r": 0,
//    "version_max": 638,
//    "is_show_quick_start": 0,
//    "song": [
//    {
//        "album": "http://site.douban.com/oukaitou/",
//        "status": 0,
//        "picture": "https://img1.doubanio.com/view/site/large/public/cca3b49d0b6ef18.jpg",
//        "ssid": "62ba",
//        "artist": "α·Pav",
//        "url": "http://mr7.doubanio.com/1dd75c253dd3e749357c69602467f8ea/0/fm/song/p1842510_128k.mp4",
//        "title": "From Here To Somewhere",
//        "length": 487,
//        "like": 0,
//        "subtype": "S",
//        "public_time": "",
//        "sid": "1842510",
//        "singers": [
//        {
//            "related_site_id": 105149,
//            "is_site_artist": false,
//            "id": "56997",
//            "name": "α·Pav"
//        }
//        ],
//        "aid": "105149",
//        "file_ext": "mp4",
//        "sha256": "dc13b80be3027d44a832973b9a2175afa453bb1a20174464ed3b07acc971bb9d",
//        "kbps": "128",
//        "albumtitle": "α·Pav",
//        "alert_msg": ""
//    }
//    ]
//}