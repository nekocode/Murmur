package cn.nekocode.murmur.data.service.Api

import cn.nekocode.murmur.data.DO.douban.ClientId
import cn.nekocode.murmur.data.service.DoubanService
import retrofit2.http.*
import rx.Observable

/**
 * Created by nekocode on 16/9/14.
 */
internal interface DoubanPush {
    companion object {
        val API: DoubanPush = DoubanService.ARTERY_REST_ADAPTER.create(DoubanPush::class.java)
    }

    @FormUrlEncoded
    @POST("api/register_android_device")
    fun registerDevice(
            @Field("device_id") device_id: String = DoubanService.PUSH_DEVICE_ID,
            @Field("ck") ck: String = DoubanService.getCk(DoubanService.PUSH_DEVICE_ID),
            @Field("ver") ver: String = DoubanService.VER.toString(),
            @Field("sdk_ver") sdk_ver: String = DoubanService.SDK_VER.toString(),
            @Field("sdk_pkg") sdk_pkg: String = DoubanService.SDK_PKG,
            @Field("device_info") device_info: String = DoubanService.getDeviceInfo(),
            @Field("apikey") apikey: String = DoubanService.PUSH_API_KEY

    ) : Observable<ClientId>
}