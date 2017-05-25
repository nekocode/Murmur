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

import cn.nekocode.murmur.data.DO.leancloud.Murmur
import cn.nekocode.murmur.data.DO.leancloud.Response
import cn.nekocode.murmur.data.DataLayer
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
internal interface LeanCloudApi {
    companion object {
        val IMPL = DataLayer.RETROFIT_LEANCLOUD!!.create(LeanCloudApi::class.java)!!
    }

    @Headers(*arrayOf(
            "X-LC-Id: ${DataLayer.LEANCLOUD_APP_ID}",
            "X-LC-Key: ${DataLayer.LEANCLOUD_APP_KEY}",
            "Content-Type: application/json"
    ))
    @GET("classes/Murmurs")
    fun getMurmurs(
            @Query("limit") limit: Int,
            @Query("order") order: String = "-updatedAt"
    ): Observable<Response<Murmur>>
}