package cn.nekocode.murmur.data.service.Api

import cn.nekocode.murmur.data.DO.douban.SongS
import cn.nekocode.murmur.data.service.DoubanService
import retrofit2.http.*
import rx.Observable
import java.util.*

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
internal interface DoubanFM {
    companion object {
        val API: DoubanFM = DoubanService.API_REST_ADAPTER.create(DoubanFM::class.java)
    }

    @GET("v2/fm/redheart/basic")
    fun getRedHeartSongIds(
            @Header("Authorization") auth: String,
            @Query("app_name") app_name: String = DoubanService.APP_NAME,
            @Query("version") version: String = DoubanService.VERSION,
            @Query("push_device_id") push_device_id: String = DoubanService.PUSH_DEVICE_ID

    ): Observable<SongS.Ids>

    @FormUrlEncoded
    @POST("v2/fm/songs")
    fun getSongs(
            @Header("Authorization") auth: String,
            @Field("sids") sids: String,
            @Field("kbps") kbps: String = "128",
            @Field("app_name") app_name: String = DoubanService.APP_NAME,
            @Field("version") version: String = DoubanService.VERSION,
            @Field("push_device_id") push_device_id: String = DoubanService.PUSH_DEVICE_ID,
            @Field("apikey") apikey: String = DoubanService.KEY

    ): Observable<ArrayList<SongS.Song>>
}