/*
 * Copyright 2017. nekocode (nekocode.cn@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.nekocode.murmur.data

import android.content.Context
import com.danikula.videocache.HttpProxyCacheServer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.paperdb.Paper
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
object DataLayer {
    const val LEANCLOUD_HOST: String = "https://api.leancloud.cn/1.1/"
    const val LEANCLOUD_APP_ID: String = "njtyqtww55i0fikg9zgzuq5cayrbi7u85uiolfjoadch2pse"
    const val LEANCLOUD_APP_KEY: String = "ld1826dyuz53gxd4vx84j60lq9mg5860ksznirff41y2sau9"

    const val DOUBAN_HOST: String = "https://api.douban.com"
    const val DOUBAN_TOKEN_HOST: String = "https://www.douban.com"
    const val DOUBAN_APP_NAME = "radio_android"
    const val DOUBAN_APP_VERSION = "642"
    const val DOUBAN_APP_KEY = "02f7751a55066bcb08e65f4eff134361"
    const val DOUBAN_APP_SECRET = "63cf04ebd7b0ff3b"
    const val DOUBAN_APP_REDIRECT_URI = "http://douban.fm"
    const val DOUBAN_APP_PUSH_DEVICE_ID = "534fa03e331b42dbb7487e8784ce50cbbf0acf13"    // len: 40
    const val DOUBAN_APP_UDID = "408428bc31c380b707d7e8961266a8d1858aee2f"
    const val DOUBAN_APP_GRANT_TYPE_PWD = "password"
    const val DOUBAN_APP_GRANT_TYPE_TOKEN = "refresh_token"

    var CLIENT: OkHttpClient? = null
    var GSON: Gson? = null
    var RETROFIT_LEANCLOUD: Retrofit? = null
    var RETROFIT_DOUBAN: Retrofit? = null
    var RETROFIT_DOUBAN_TOKEN: Retrofit? = null

    var MEDIA_PROXY: HttpProxyCacheServer? = null


    fun init(context: Context) {
        CLIENT = OkHttpClient.Builder()
                .cache(Cache(File(context.cacheDir, "okhttp"), 10 * 1024 * 1024L))
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build()

        GSON = GsonBuilder().setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'").create()

        RETROFIT_LEANCLOUD = Retrofit.Builder()
                .baseUrl(LEANCLOUD_HOST)
                .addConverterFactory(GsonConverterFactory.create(GSON))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(CLIENT)
                .build()

        RETROFIT_DOUBAN = Retrofit.Builder()
                .baseUrl(DOUBAN_HOST)
                .addConverterFactory(GsonConverterFactory.create(GSON))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(CLIENT)
                .build()

        RETROFIT_DOUBAN_TOKEN = Retrofit.Builder()
                .baseUrl(DOUBAN_TOKEN_HOST)
                .addConverterFactory(GsonConverterFactory.create(GSON))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(CLIENT)
                .build()

        MEDIA_PROXY = HttpProxyCacheServer.Builder(context)
                .maxCacheSize(512 * 1024 * 1024)
                .build()

        Paper.init(context)
    }
}