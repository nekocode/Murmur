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

package cn.nekocode.murmur.data.service

import cn.nekocode.murmur.data.DO.leancloud.Murmur
import cn.nekocode.murmur.data.DataLayer
import cn.nekocode.murmur.data.api.LeanCloudApi
import io.paperdb.Paper
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
object LeanCloudService {

    fun getMurmurs(): Observable<ArrayList<Murmur>> =
            LeanCloudApi.IMPL.getMurmurs(50)
                    .map {
                        val murmurs = it.results
                        murmurs.forEach {
                            it.file.url = DataLayer.MEDIA_PROXY!!.getProxyUrl(it.file.url)
                        }

                        Paper.book().write("murmurs", murmurs)
                        murmurs
                    }
                    .onErrorResumeNext { err: Throwable ->
                        val list: ArrayList<Murmur> = Paper.book().read("murmurs") ?: throw err

                        Observable.just(list)
                    }
}