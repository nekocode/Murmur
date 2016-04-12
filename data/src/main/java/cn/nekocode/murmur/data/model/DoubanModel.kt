package cn.nekocode.murmur.data.model

import cn.nekocode.murmur.data.dto.DoubanUser
import cn.nekocode.murmur.data.exception.DoubanException
import cn.nekocode.murmur.data.service.DoubanService
import com.orhanobut.hawk.Hawk
import rx.Observable
import rx.schedulers.Schedulers

/**
 * Created by nekocode on 3/13/16.
 */
object DoubanModel {

    fun getCachedUserInfo(): Pair<String, String>? {
        val email: String = Hawk.get("email") ?: return null
        val pwd: String = Hawk.get("pwd") ?: return null

        return Pair(email, pwd)
    }

    fun login(email: String, pwd: String) =
            DoubanService.api.login("radio_android", "100", email, pwd)
                    .subscribeOn(Schedulers.io())
                    .map {
                        if (!it.err.equals("ok")) {
                            throw DoubanException(it.err)
                        }

                        Hawk.put("email", email)
                        Hawk.put("pwd", pwd)
                        
                        it
                    }

    fun nextSong(user: DoubanUser) =
            DoubanService.api.getSongs("radio_android", "100",
                    user.id, user.token, user.expire, "0", "n")

                    .subscribeOn(Schedulers.io())
                    .flatMap {
                        if (it.status != 0) {
                            throw DoubanException(it.err)
                        }

                        Observable.just(it.song[0])
                    }
}