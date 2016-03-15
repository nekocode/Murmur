package cn.nekocode.murmur.data

import android.app.Application
import com.danikula.videocache.HttpProxyCacheServer
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.orhanobut.hawk.Hawk
import com.orhanobut.hawk.HawkBuilder
import com.orhanobut.hawk.LogLevel
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

object DataLayer {
    // OkHttp Config
    const val RESPONSE_CACHE_FILE: String = "reponse_cache"
    const val RESPONSE_CACHE_SIZE: Int = 10 * 1024 * 1024
    const val HTTP_CONNECT_TIMEOUT: Int = 10
    const val HTTP_READ_TIMEOUT: Int = 30
    const val HTTP_WRITE_TIMEOUT: Int = 10

    var app: Application by Delegates.notNull()
    var mediaProxy by Delegates.notNull<HttpProxyCacheServer>()
    var okHttpClient: OkHttpClient by Delegates.notNull()
    val gson: Gson = GsonBuilder().setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'").create()

    fun hook(app: Application) {
        DataLayer.app = app

        Hawk.init(app)
                .setEncryptionMethod(HawkBuilder.EncryptionMethod.MEDIUM)
                .setStorage(HawkBuilder.newSqliteStorage(app))
                .setLogLevel(LogLevel.FULL)
                .build()

        mediaProxy = HttpProxyCacheServer.Builder(app)
                .maxCacheSize(512 * 1024 * 1024)
                .build()

        val cacheDir = File(app.cacheDir, RESPONSE_CACHE_FILE)
        okHttpClient = OkHttpClient.Builder()
                .cache(Cache(cacheDir, RESPONSE_CACHE_SIZE.toLong()))
                .connectTimeout(HTTP_CONNECT_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .writeTimeout(HTTP_WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .readTimeout(HTTP_READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .addNetworkInterceptor(StethoInterceptor())
                .build()

        Stetho.initializeWithDefaults(app)
    }
}