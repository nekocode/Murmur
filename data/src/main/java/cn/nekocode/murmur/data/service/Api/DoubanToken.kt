package cn.nekocode.murmur.data.service.Api

import cn.nekocode.murmur.data.DO.douban.Session
import cn.nekocode.murmur.data.service.DoubanService
import retrofit2.http.*
import rx.Observable

/**
 * Created by nekocode on 16/9/14.
 */
internal interface DoubanToken {
    companion object {
        val API: DoubanToken = DoubanService.TOKEN_REST_ADAPTER.create(DoubanToken::class.java)
    }

    @FormUrlEncoded
    @POST("service/auth2/token")
    fun login(
            @Field("username") username: String,
            @Field("password") password: String,
            @Query("udid") udid: String = "408428bc31c380b707d7e8961266a8d1858aee2f",
            @Field("client_id") client_id: String = DoubanService.KEY,
            @Field("client_secret") client_secret: String = DoubanService.SECRET,
            @Field("redirect_uri") redirect_uri: String = DoubanService.REDIRECT_URI,
            @Field("grant_type") grant_type: String = "password",
            @Field("apikey") apikey: String = DoubanService.KEY

    ) : Observable<Session>
}