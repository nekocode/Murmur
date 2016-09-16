package cn.nekocode.murmur.data.service.Api

import cn.nekocode.murmur.data.DO.douban.SongS
import cn.nekocode.murmur.data.service.DoubanService
import retrofit2.http.*
import rx.Observable

/**
 * Created by nekocode on 16/9/14.
 */
internal interface DoubanFM {
    companion object {
        val API: DoubanFM = DoubanService.API_REST_ADAPTER.create(DoubanFM::class.java)
    }

    @GET("v2/fm/redheart/basic")
    fun getRedHeartSongs(
            @Header("Authorization") auth: String,
            @Query("app_name") app_name: String = DoubanService.APP_NAME,
            @Query("version") version: String = DoubanService.VERSION,
            @Query("push_device_id") push_device_id: String = DoubanService.PUSH_DEVICE_ID

    ): Observable<SongS.Ids>

    @GET("v2/fm/song/{sid}")
    fun getSong(
            @Header("Authorization") auth: String,
            @Path("sid") songId: String,
            @Query("app_name") app_name: String = DoubanService.APP_NAME,
            @Query("version") version: String = DoubanService.VERSION,
            @Query("push_device_id") push_device_id: String = DoubanService.PUSH_DEVICE_ID

    ): Observable<SongS.Song>
}