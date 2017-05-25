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

import cn.nekocode.murmur.data.DO.douban.Session
import cn.nekocode.murmur.data.DataLayer
import io.reactivex.Observable
import retrofit2.http.*

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
internal interface DoubanTokenApi {
    companion object {
        val IMPL = DataLayer.RETROFIT_DOUBAN_TOKEN!!.create(DoubanTokenApi::class.java)!!
    }

    @FormUrlEncoded
    @POST("service/auth2/token")
    fun login(
            @Field("username") username: String,
            @Field("password") password: String,
            @Field("udid") udid: String = DataLayer.DOUBAN_APP_UDID,
            @Field("client_id") client_id: String = DataLayer.DOUBAN_APP_KEY,
            @Field("client_secret") client_secret: String = DataLayer.DOUBAN_APP_SECRET,
            @Field("redirect_uri") redirect_uri: String = DataLayer.DOUBAN_APP_REDIRECT_URI,
            @Field("grant_type") grant_type: String = DataLayer.DOUBAN_APP_GRANT_TYPE_PWD,
            @Field("apikey") apikey: String = DataLayer.DOUBAN_APP_KEY

    ) : Observable<Session>

    @FormUrlEncoded
    @POST("service/auth2/token")
    fun relogin(
            @Field("refresh_token") refreshToken: String,
            @Field("udid") udid: String = DataLayer.DOUBAN_APP_UDID,
            @Field("client_id") client_id: String = DataLayer.DOUBAN_APP_KEY,
            @Field("client_secret") client_secret: String = DataLayer.DOUBAN_APP_SECRET,
            @Field("redirect_uri") redirect_uri: String = DataLayer.DOUBAN_APP_REDIRECT_URI,
            @Field("grant_type") grant_type: String = DataLayer.DOUBAN_APP_GRANT_TYPE_TOKEN,
            @Field("apikey") apikey: String = DataLayer.DOUBAN_APP_KEY

    ) : Observable<Session>
}