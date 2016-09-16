package cn.nekocode.murmur.data.service

import cn.nekocode.murmur.data.DataLayer
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


internal object DoubanService {
    const val API_HOST_URL = "https://api.douban.com"
    const val TOKEN_HOST_URL = "https://www.douban.com"

    const val APP_NAME = "radio_android"
    const val VERSION = "642"
    const val KEY = "02f7751a55066bcb08e65f4eff134361"
    const val SECRET = "63cf04ebd7b0ff3b"
    const val REDIRECT_URI = "http://douban.fm"
    const val PUSH_DEVICE_ID = "534fa03e331b42dbb7487e8784ce50cbbf0acf13"   // len: 40

    val API_REST_ADAPTER: Retrofit = Retrofit.Builder()
            .baseUrl(API_HOST_URL)
            .addConverterFactory(GsonConverterFactory.create(DataLayer.gson))
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .client(DataLayer.okHttpClient)
            .build()

    val TOKEN_REST_ADAPTER: Retrofit = Retrofit.Builder()
            .baseUrl(TOKEN_HOST_URL)
            .addConverterFactory(GsonConverterFactory.create(DataLayer.gson))
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .client(DataLayer.okHttpClient)
            .build()

}
