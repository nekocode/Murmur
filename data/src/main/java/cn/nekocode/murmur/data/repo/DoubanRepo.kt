package cn.nekocode.murmur.data.repo

import cn.nekocode.murmur.data.DO.douban.Session
import cn.nekocode.murmur.data.DO.douban.SongS
import cn.nekocode.murmur.data.service.Api.DoubanFM
import cn.nekocode.murmur.data.service.Api.DoubanToken
import com.orhanobut.hawk.Hawk
import rx.Observable
import rx.schedulers.Schedulers
import java.util.*

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
object DoubanRepo {

    fun getCachedUserInfo(): Pair<String, String>? {
        val email: String = Hawk.get("email") ?: return null
        val pwd: String = Hawk.get("pwd") ?: return null

        return Pair(email, pwd)
    }

    fun login(email: String, pwd: String): Observable<Session> =
            DoubanToken.API.login(email, pwd)
                    .subscribeOn(Schedulers.io())
                    .map {
                        if (it.accessToken != null && !it.accessToken.isBlank()) {
                            Hawk.put("email", email)
                            Hawk.put("pwd", pwd)
                        }

                        it
                    }


    val songs = ArrayList<SongS.Song>()
    fun nextSong(session: Session): Observable<SongS.Song> =
            Observable.just(songs)
                    .subscribeOn(Schedulers.io())
                    .flatMap {
                        val auth = "Bearer ${session.accessToken}"

                        if (it.size == 0) {
                            // 从网络获取红心歌曲
                            DoubanFM.API.getRedHeartSongIds(auth)
                                    .map {
                                        val builder = StringBuilder()
                                        it.songs
                                                .filter {
                                                    // 筛选出可播放的歌曲
                                                    id -> id.playable
                                                }
                                                .forEach {
                                                    builder.append(it.sid).append("|")
                                                }

                                        builder.deleteCharAt(builder.length - 1).toString()

                                    }
                                    .flatMap {
                                        // 根据歌曲 id 列表获取详细详细
                                        DoubanFM.API.getSongs(auth, it)
                                                // 添加进内存缓存
                                                .map { songs.addAll(it); it }
                                    }
                        } else Observable.just(songs)
                    }
                    // 从歌曲列表中随机抽取下一首歌曲
                    .map {
                        // TODO 可以使用洗牌算法优化
                        it[Random().nextInt(it.size)]
                    }

}