package cn.nekocode.murmur.presentation.main

import cn.nekocode.kotgo.component.presentation.Presenter
import cn.nekocode.kotgo.component.rx.RxLifecycle
import cn.nekocode.kotgo.component.rx.bindLifecycle
import cn.nekocode.kotgo.component.rx.onUI
import cn.nekocode.murmur.App
import cn.nekocode.murmur.data.dto.DoubanSong
import cn.nekocode.murmur.data.dto.DoubanUser
import cn.nekocode.murmur.data.dto.Murmur
import cn.nekocode.murmur.data.exception.DoubanException
import cn.nekocode.murmur.data.model.DoubanModel
import cn.nekocode.murmur.data.model.MurmurModel
import cn.nekocode.murmur.util.Util.randomPick
import rx.Observable
import rx.Subscription
import kotlin.properties.Delegates


class MainPresenter(val view: MainPresenter.ViewInterface): Presenter(view) {
    interface ViewInterface: RxLifecycle.Getter {
        fun showLoginDialog()
        fun loginSuccess()
        fun loginFailed()
        fun murmursChange(murmurs: List<Murmur>)
        fun songChange(song: DoubanSong)

        fun toast(msg: String)
    }

    var user: DoubanUser by Delegates.notNull<DoubanUser>()

    val errorHandler: (Throwable)->Unit = {
        when(it) {
            is DoubanException -> {
                if (it.err.equals("invalid_token")) {
                    view.toast("You token has been invalid.\nYou must login again.")
                    view.showLoginDialog()

                } else {
                    view.toast(it.err)
                }
            }

            else -> {
                if(it.message != null)
                    view.toast(it.message!!)
            }
        }

    }

    fun init() {
        val cachedUser = DoubanModel.getCachedUserInfo()

        if(cachedUser == null) {
            view.showLoginDialog()

        } else {
            login(cachedUser.first, cachedUser.second)
        }
    }

    private fun fetchData() {
        Observable.combineLatest(MurmurModel.getMurmurs(), DoubanModel.nextSong(user), {
            murmurs, song ->
            Pair(murmurs, song)

        }).bind(this).subscribe({
            val murmurs = it.first.randomPick(2)
            val song = it.second

            App.musicSerivice?.playMurmurs(murmurs)
            App.musicSerivice?.playSong(song)
            view.murmursChange(murmurs)
            view.songChange(song)

        }, errorHandler)
    }

    fun login(email: String, pwd: String) {
        DoubanModel.login(email, pwd).bind(view).subscribe({
            user = it
            view.loginSuccess()
            fetchData()

        }, {
            when(it) {
                is DoubanException -> view.toast(it.err)
                else -> {
                    if(it.message != null)
                        view.toast(it.message!!)
                }
            }

            view.loginFailed()
        })
    }

    var oldSubscription: Subscription? = null

    fun nextSong() {
        if(oldSubscription != null && oldSubscription!!.isUnsubscribed) {
            oldSubscription?.unsubscribe()
        }

        oldSubscription = DoubanModel.nextSong(user).bind(this).subscribe({
            App.musicSerivice?.playSong(it)
            view.songChange(it)
        }, errorHandler)
    }

    fun <T> Observable<T>.bind(lifecycler: RxLifecycle.Getter): Observable<T> {
        return this.onUI().bindLifecycle(lifecycler)
    }
}