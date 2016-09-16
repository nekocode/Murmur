package cn.nekocode.murmur.data.service

import android.os.Build
import cn.nekocode.murmur.data.DataLayer
import cn.nekocode.murmur.data.util.CryptoUtils.HASH
import cn.nekocode.murmur.data.util.CryptoUtils.PBE
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


internal object DoubanService {
    const val API_HOST_URL = "https://api.douban.com"
    const val TOKEN_HOST_URL = "https://www.douban.com"
    const val ARTERY_HOST_URL = "https://artery.douban.com"

    const val APP_NAME = "radio_android"
    const val VERSION = "642"
    const val KEY = "02f7751a55066bcb08e65f4eff134361"
    const val SECRET = "63cf04ebd7b0ff3b"
    const val REDIRECT_URI = "http://douban.fm"

    const val PUSH_SERVER_URI = "ssl://push.douban.com:4392"
    const val PUSH_API_KEY = "00647c939cd71c97012d75bad68555c5"
    const val PUSH_API_SECRET = "e276b9dde6479dae"
    const val PUSH_DEVICE_ID = "534fa03e331b42dbb7487e8784ce50cbbf0acf13"   // len: 40
    const val PUSH_CK_SALT = "c3b7de3770d8866d717ca71c07007a1e2b245ac7ef61991b6e6e7d1217f0ad5d"
    const val RANDOM_TEXT = "lLVijIStu9q;VSLW DcSKnuGvy^QHjyVKZfoiZmz"
    const val MQTT_KEEP_ALIVE_IN_SECONDS = 290

    const val VER = 2
    const val SDK_VER = 212
    const val SDK_PKG = "com.douban.radio"

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

    val ARTERY_REST_ADAPTER: Retrofit = Retrofit.Builder()
            .baseUrl(ARTERY_HOST_URL)
            .addConverterFactory(GsonConverterFactory.create(DataLayer.gson))
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .client(DataLayer.okHttpClient)
            .build()

    fun getDeviceInfo() = JSONObject().apply {
        put("device_id", DoubanService.PUSH_DEVICE_ID)
        put("ver", VER)
        put("sdk_ver", SDK_VER)
        put("sdk_pkg", SDK_PKG)
        put("os_api", Build.VERSION.SDK_INT)
        put("os_ver", Build.VERSION.RELEASE)
        put("os_rom", "android")
        put("vendor", Build.MANUFACTURER)
        put("model", Build.MODEL)
        put("net", "WIFI")
        put("apps", JSONArray().apply { put(SDK_PKG) })

    }.toString()

    fun getSalt(): ByteArray {
        val salt = ByteArray(8)
        System.arraycopy(HASH.md5Bytes(RANDOM_TEXT.toByteArray()), 7, salt, 0, 8)
        return salt
    }

    fun getPassword(): String {
        return HASH.sha1(PUSH_API_SECRET) + RANDOM_TEXT
    }

    fun encrypt(data: String): String {
        return PBE.encrypt(data, getPassword(), getSalt())
    }

    fun decrypt(data: String): String {
        return PBE.decrypt(data, getPassword(), getSalt())
    }

    fun getCk(data: String) = HASH.sha1(data + decrypt(PUSH_CK_SALT));
}
