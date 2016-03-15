package cn.nekocode.murmur.data.service

import cn.nekocode.murmur.data.DataLayer
import cn.nekocode.murmur.data.dto.Murmur
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import rx.Observable


class LeancloudService {
    companion object {
        private const val API_HOST_URL = "https://api.leancloud.cn/1.1/"
        private const val APP_ID = "njtyqtww55i0fikg9zgzuq5cayrbi7u85uiolfjoadch2pse"
        private const val APP_KEY = "ld1826dyuz53gxd4vx84j60lq9mg5860ksznirff41y2sau9"
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

    data class ResponseWrapper<T>(val results: List<T>)

    interface APIs {
        @Headers(*arrayOf(
                "X-LC-Id: $APP_ID",
                "X-LC-Key: $APP_KEY",
                "Content-Type: application/json"
        ))
        @GET("classes/Murmurs")
        fun getMurmurs(@Query("limit") limit: Int, @Query("order") order: String): Observable<ResponseWrapper<Murmur>>
    }
}
