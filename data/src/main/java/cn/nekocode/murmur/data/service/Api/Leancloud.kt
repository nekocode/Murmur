package cn.nekocode.murmur.data.service.Api

import cn.nekocode.murmur.data.DO.Murmur
import cn.nekocode.murmur.data.service.LeancloudService
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import rx.Observable

/**
 * Created by nekocode on 16/9/14.
 */
internal interface Leancloud {
    companion object {
        val API: Leancloud = LeancloudService.REST_ADAPTER.create(Leancloud::class.java)
    }

    @Headers(*arrayOf(
            "X-LC-Id: ${LeancloudService.APP_ID}",
            "X-LC-Key: ${LeancloudService.APP_KEY}",
            "Content-Type: application/json"
    ))
    @GET("classes/Murmurs")
    fun getMurmurs(
            @Query("limit") limit: Int,
            @Query("order") order: String
    ): Observable<LeancloudService.ResponseWrapper<Murmur>>
}