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

package cn.nekocode.murmur.data.api

import cn.nekocode.murmur.data.DO.douban.Song
import cn.nekocode.murmur.data.DO.douban.SongIDs
import cn.nekocode.murmur.data.DataLayer
import cn.nekocode.murmur.data.service.DoubanService
import io.reactivex.Observable
import retrofit2.http.*

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
internal interface DoubanApi {
    companion object {
        val IMPL = DataLayer.RETROFIT_DOUBAN!!.create(DoubanApi::class.java)!!
    }

    @GET("v2/fm/redheart/basic")
    fun getRedHeartSongIds(
            @Header("Authorization") auth: String,
            @Query("app_name") app_name: String = DataLayer.DOUBAN_APP_NAME,
            @Query("version") version: String = DataLayer.DOUBAN_APP_VERSION,
            @Query("push_device_id") push_device_id: String = DataLayer.DOUBAN_APP_PUSH_DEVICE_ID

    ): Observable<SongIDs>

    @FormUrlEncoded
    @POST("v2/fm/songs")
    fun getSongs(
            @Header("Authorization") auth: String,
            @Field("sids") sids: String,
            @Field("kbps") kbps: String = "128",
            @Field("app_name") app_name: String = DataLayer.DOUBAN_APP_NAME,
            @Field("version") version: String = DataLayer.DOUBAN_APP_VERSION,
            @Field("push_device_id") push_device_id: String = DataLayer.DOUBAN_APP_PUSH_DEVICE_ID,
            @Field("apikey") apikey: String = DataLayer.DOUBAN_APP_KEY

    ): Observable<ArrayList<Song>>
}