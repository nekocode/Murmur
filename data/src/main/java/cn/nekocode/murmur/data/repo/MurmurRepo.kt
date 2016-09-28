package cn.nekocode.murmur.data.repo

import cn.nekocode.murmur.data.DataLayer
import cn.nekocode.murmur.data.DO.Murmur
import cn.nekocode.murmur.data.service.Api.Leancloud
import com.orhanobut.hawk.Hawk
import rx.Observable
import rx.schedulers.Schedulers

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
object MurmurRepo {

    fun getMurmurs(): Observable<List<Murmur>> =
            Leancloud.API.getMurmurs(50, "-updatedAt")
                    .subscribeOn(Schedulers.io())
                    .map {
                        val murmurs = it.results
                        murmurs.map {
                            it.file.url = DataLayer.mediaProxy.getProxyUrl(it.file.url)
                            it
                        }

                        Hawk.put("murmurs", murmurs)
                        murmurs
                    }
                    .onErrorResumeNext {
                        val list: List<Murmur>? = Hawk.get("murmurs")
                        list ?: throw Exception("Get murmurs error.")

                        Observable.just(list)
                    }

}