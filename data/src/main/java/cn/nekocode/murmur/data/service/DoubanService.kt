package cn.nekocode.murmur.data.service

import cn.nekocode.murmur.data.DataLayer
import cn.nekocode.murmur.data.dto.DoubanSongWrapper
import cn.nekocode.murmur.data.dto.DoubanUser
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import rx.Observable


class DoubanService {
    companion object {
        private const val API_HOST_URL = "https://www.douban.com/j/app/"
        val api: APIs

        init {
            val restAdapter = Retrofit.Builder()
                    .baseUrl(API_HOST_URL)
                    .addConverterFactory(GsonConverterFactory.create(DataLayer.gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(DataLayer.okHttpClient)
                    .build()

            api = restAdapter.create(APIs::class.java)
        }
    }

    interface APIs {
        @FormUrlEncoded
        @POST("login")
        fun login(@Field("app_name") appName: String,
                  @Field("version") version: String,
                  @Field("email") email: String,
                  @Field("password") password: String):
                Observable<DoubanUser>

        @GET("radio/people")
        fun getSongs(@Query("app_name") appName: String,
                     @Query("version") version: String,
                     @Query("user_id") userId: String,
                     @Query("token") token: String,
                     @Query("expire") expire: Long,
                     @Query("channel") channel: String,
                     @Query("type") type: String):
                Observable<DoubanSongWrapper>
    }
}
