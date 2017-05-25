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

import cn.nekocode.murmur.data.DO.douban.Session
import cn.nekocode.murmur.data.DO.douban.Song
import cn.nekocode.murmur.data.api.DoubanApi
import cn.nekocode.murmur.data.api.DoubanTokenApi
import cn.nekocode.murmur.data.exception.DoubanException
import io.paperdb.Paper
import io.reactivex.Observable

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
object DoubanService {

    /**
     * 获取保存的 Session
     */
    fun getSavedSession(): Session? = Paper.book().read<Session?>("seesion")

    /**
     * 刷新 Token
     */
    fun relogin(session: Session): Observable<Session> =
            DoubanTokenApi.IMPL.relogin(session.refreshToken ?: "")
                    .checkAndSaveSeesion()

    /**
     * 登录
     */
    fun login(email: String, pwd: String): Observable<Session> =
            DoubanTokenApi.IMPL.login(email, pwd)
                    .checkAndSaveSeesion()

    private fun Observable<Session>.checkAndSaveSeesion(): Observable<Session> =
            this
                    .map {
                        if (!it.accessToken.isNullOrBlank()) {
                            Paper.book().write("seesion", it)
                        } else {
                            throw DoubanException("Seesion is not vaild.")
                        }

                        it
                    }

    /**
     * 获取所有红心歌曲
     */
    fun getSongs(session: Session): Observable<ArrayList<Song>> {
        val auth = "Bearer ${session.accessToken}"

        return DoubanApi.IMPL.getRedHeartSongIds(auth)
                .map { songIDs ->
                    val builder = StringBuilder()
                    songIDs.songs
                            .filter {
                                // 筛选出可播放的歌曲
                                id ->
                                id.playable
                            }
                            .forEach {
                                builder.append(it.id).append("|")
                            }

                    if (builder.isNotEmpty()) {
                        builder.deleteCharAt(builder.length - 1).toString()
                    } else {
                        builder.toString()
                    }
                }
                .flatMap {
                    // 根据歌曲 id 列表获取详细信息
                    DoubanApi.IMPL.getSongs(auth, it)
                }
                .map {
                    Paper.book().write("songs", it)
                    it
                }
                .onErrorResumeNext { err: Throwable ->
                    val list: ArrayList<Song> = Paper.book().read("songs") ?: throw err

                    Observable.just(list)
                }
    }
}