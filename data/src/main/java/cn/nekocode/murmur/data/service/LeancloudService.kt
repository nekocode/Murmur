package cn.nekocode.murmur.data.service

import cn.nekocode.murmur.data.DataLayer
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


internal object LeancloudService {
    const val API_HOST_URL = "https://api.leancloud.cn/1.1/"
    const val APP_ID = "njtyqtww55i0fikg9zgzuq5cayrbi7u85uiolfjoadch2pse"
    const val APP_KEY = "ld1826dyuz53gxd4vx84j60lq9mg5860ksznirff41y2sau9"

    val REST_ADAPTER: Retrofit = Retrofit.Builder()
            .baseUrl(API_HOST_URL)
            .addConverterFactory(GsonConverterFactory.create(DataLayer.gson))
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .client(DataLayer.okHttpClient)
            .build()

    class ResponseWrapper<out T>(val results: List<T>)
}
