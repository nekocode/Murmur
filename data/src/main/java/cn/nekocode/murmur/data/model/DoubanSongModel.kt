package cn.nekocode.murmur.data.model

import cn.nekocode.murmur.data.dto.DoubanSong
import cn.nekocode.murmur.data.dto.DoubanUser
import cn.nekocode.murmur.data.exception.DoubanException
import cn.nekocode.murmur.data.service.DoubanService
import rx.Observable
import rx.schedulers.Schedulers

/**
 * Created by nekocode on 3/13/16.
 */
object DoubanSongModel {
    var user: DoubanUser? = null

    fun nextSong(): Observable<DoubanSong> {
        if(user == null) {
            return DoubanService.api.login("radio_android", "100", "syfyw@qq.com", "110110zxc")
                    .subscribeOn(Schedulers.io())
                    .flatMap {
                        if (!it.err.equals("ok")) {
                            user = null
                            throw DoubanException(it.err)
                        }

                        user = it
                        nextSong(user!!)
                    }
        }

        return nextSong(user!!)
    }

    private fun nextSong(user: DoubanUser) =
            DoubanService.api.getSongs("radio_android", "100",
                    user.id, user.token, user.expire, "0", "n")

                    .subscribeOn(Schedulers.io())
                    .flatMap {
                        if(it.status != 0) {
                            if(it.err.equals("invalid_token")) {
                                DoubanSongModel.user = null
                                return@flatMap nextSong()

                            } else {
                                throw DoubanException(it.err)
                            }
                        }

                        Observable.just(it.song[0])
                    }
}