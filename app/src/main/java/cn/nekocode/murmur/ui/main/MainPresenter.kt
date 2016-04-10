package cn.nekocode.murmur.ui.main

import android.os.Bundle
import cn.nekocode.kotgo.component.rx.bus
import cn.nekocode.murmur.App
import cn.nekocode.murmur.common.MyPresenter
import cn.nekocode.murmur.data.dto.DoubanUser
import cn.nekocode.murmur.data.dto.Murmur
import cn.nekocode.murmur.data.exception.DoubanException
import cn.nekocode.murmur.data.model.DoubanModel
import cn.nekocode.murmur.data.model.MurmurModel
import cn.nekocode.murmur.data.model.SettingModel
import cn.nekocode.murmur.util.Util.randomPick
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread
import rx.Observable
import java.util.*
import kotlin.properties.Delegates


class MainPresenter(): MyPresenter(), Contract.Presenter {
    var view: Contract.View? = null

    var user: DoubanUser by Delegates.notNull<DoubanUser>()
    val murmurs = ArrayList<Murmur>()
    val playingMurmurs = ArrayList<Murmur>()
    var isDestoried = false

    override fun onCreate(savedState: Bundle?) {
        val cachedUser = DoubanModel.getCachedUserInfo()

        if(cachedUser == null) {
            view?.showLoginDialog()
        } else {
            login(cachedUser.first, cachedUser.second)
        }

        async() {
            while(!isDestoried) {
                uiThread {
                    val time = getTimedText()
                    if(time != null)
                        view?.timeChanged(time)
                }

                Thread.sleep(500)
            }
        }

        bus {
            subscribe(String::class.java) {
                if(it.equals("Finished")) {

                }
            }
        }
    }

    override fun onDestroyView() {
        isDestoried = true
        super.onDestroyView()
    }

    override fun login(email: String, pwd: String) {
        DoubanModel.login(email, pwd).bind().subscribe({
            user = it
            view?.loginSuccess()
            fetchData()

        }, {
            when(it) {
                is DoubanException -> view?.showToast(it.err)
                else -> {
                    if(it.message != null)
                        view?.showToast(it.message!!)
                }
            }

            view?.loginFailed()
        })
    }

    private fun fetchData() {
        Observable.combineLatest(MurmurModel.getMurmurs(), DoubanModel.nextSong(user), {
            murmurs, song ->
            Pair(murmurs, song)

        }).bind().subscribe({
            murmurs.addAll(it.first)

            val selectedMurmurs = SettingModel.loadSelectedMurmurs()
            if(selectedMurmurs == null) {
                playingMurmurs.addAll(murmurs.randomPick(2))
                SettingModel.saveSelectedMurmurs(playingMurmurs)
            } else {
                playingMurmurs.addAll(selectedMurmurs)
            }

            App.musicSerivice?.playMurmurs(playingMurmurs)
            view?.murmursChanged(murmurs, playingMurmurs)

            val song = it.second
            App.musicSerivice?.playSong(song)
            view?.songChanged(song)

        }, errorHandler)
    }

    override fun changeMurmur(murmur: Murmur, play: Boolean) {
        if(murmur in playingMurmurs) {
            if(!play) {
                playingMurmurs.remove(murmur)
            }
        } else {
            if(play) {
                playingMurmurs.add(murmur)
            }
        }

        SettingModel.saveSelectedMurmurs(playingMurmurs)

        App.musicSerivice?.playMurmurs(playingMurmurs)
        view?.murmursChanged(murmurs, playingMurmurs)
    }

    override fun nextSong() {
        DoubanModel.nextSong(user).bind().subscribe({
            App.musicSerivice?.playSong(it)
            view?.songChanged(it)
        }, errorHandler)
    }

    private fun getTimedText(): String? {
        var text: String? = null
        App.musicSerivice?.apply {
            if(playingSong.song == null || !playingSong.player.isPlaying)
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
                    view?.showToast("You token has been invalid.\nYou must login again.")
                    view?.showLoginDialog()

                } else {
                    view?.showToast(it.err)
                }
            }

            else -> {
                if(it.message != null)
                    view?.showToast(it.message!!)
            }
        }

    }
}