package cn.nekocode.murmur.presentation.main

import android.os.Bundle
import cn.nekocode.murmur.App
import cn.nekocode.murmur.common.MyPresenter
import cn.nekocode.murmur.data.dto.DoubanSong
import cn.nekocode.murmur.data.dto.DoubanUser
import cn.nekocode.murmur.data.dto.Murmur
import cn.nekocode.murmur.data.exception.DoubanException
import cn.nekocode.murmur.data.model.DoubanModel
import cn.nekocode.murmur.data.model.MurmurModel
import cn.nekocode.murmur.data.model.SettingModel
import cn.nekocode.murmur.util.Util.randomPick
import rx.Observable
import java.util.*
import kotlin.properties.Delegates


class MainPresenter(override val view: ViewInterface): MyPresenter(view) {
    interface ViewInterface: BaseViewInterface {
        fun showLoginDialog()
        fun loginSuccess()
        fun loginFailed()
        fun murmursChange(murmurs: List<Murmur>)
        fun songChange(song: DoubanSong)
        fun changeTimedText(text: String)

        fun showToast(msg: String)
    }

    var user: DoubanUser by Delegates.notNull<DoubanUser>()
    val murmurs = ArrayList<Murmur>()
    val playingMurmurs = ArrayList<Murmur>()

    override fun onCreate(savedState: Bundle?) {
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

        }).bind().subscribe({
            murmurs.addAll(it.first)

            val selectedMurmurs = SettingModel.loadSelectedMurmurs()
            if(selectedMurmurs == null) {
                playingMurmurs.addAll(it.first.randomPick(2))
                SettingModel.saveSelectedMurmurs(playingMurmurs)
            } else {
                playingMurmurs.addAll(selectedMurmurs)
            }

            App.musicSerivice?.playMurmurs(playingMurmurs)
            view.murmursChange(playingMurmurs)

            val song = it.second
            App.musicSerivice?.playSong(song)
            view.songChange(song)

        }, errorHandler)
    }

    fun login(email: String, pwd: String) {
        DoubanModel.login(email, pwd).bind().subscribe({
            user = it
            view.loginSuccess()
            fetchData()

        }, {
            when(it) {
                is DoubanException -> view.showToast(it.err)
                else -> {
                    if(it.message != null)
                        view.showToast(it.message!!)
                }
            }

            view.loginFailed()
        })
    }

    fun nextSong() {
        DoubanModel.nextSong(user).bind().subscribe({
            App.musicSerivice?.playSong(it)
            view.songChange(it)
        }, errorHandler)
    }

    fun timedText(): String {
        var text = "loading"
        App.musicSerivice?.apply {
            if(playingSong.song == null)
                return@apply

            val rest = (playingSong.player.duration - playingSong.player.currentPosition) / 1000

            val m = rest / 60
            val s = rest % 60

            if(m != 0 && s != 0)
                text = "$m:$s"
        }

        return text
    }

    val errorHandler: (Throwable)->Unit = {
        when(it) {
            is DoubanException -> {
                if (it.err.equals("invalid_token")) {
                    view.showToast("You token has been invalid.\nYou must login again.")
                    view.showLoginDialog()

                } else {
                    view.showToast(it.err)
                }
            }

            else -> {
                if(it.message != null)
                    view.showToast(it.message!!)
            }
        }

    }
}