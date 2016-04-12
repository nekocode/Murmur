package cn.nekocode.murmur.ui.main

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.nekocode.kotgo.component.rx.bus
import cn.nekocode.murmur.common.MyPresenter
import cn.nekocode.murmur.data.dto.DoubanSong
import cn.nekocode.murmur.data.dto.DoubanUser
import cn.nekocode.murmur.data.dto.Murmur
import cn.nekocode.murmur.data.exception.DoubanException
import cn.nekocode.murmur.data.model.DoubanModel
import cn.nekocode.murmur.data.model.MurmurModel
import cn.nekocode.murmur.data.model.SettingModel
import cn.nekocode.murmur.service.MusicService
import cn.nekocode.murmur.util.Util.randomPick
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread
import rx.Observable
import java.util.*


class MainPresenter(): MyPresenter(), Contract.Presenter {
    var view: Contract.View? = null

    var user: DoubanUser? = null
    val murmurs = ArrayList<Murmur>()
    val playingMurmurs = ArrayList<Murmur>()
    var playingSong: DoubanSong? = null

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        view = getParent() as Contract.View
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(savedInstanceState != null) {
            // 恢复现场
            user = savedInstanceState.getParcelable("user")
            murmurs.addAll(savedInstanceState.getParcelableArrayList("murmurs"))
            playingMurmurs.addAll(savedInstanceState.getParcelableArrayList("playingMurmurs"))
            playingSong = savedInstanceState.getParcelable("playingSong")

        } else {
            // 检查是否已经登录
            val cachedUser = DoubanModel.getCachedUserInfo()
            if(cachedUser == null) {
                view?.showLoginDialog()
            } else {
                login(cachedUser.first, cachedUser.second)
            }

        }

        // 订阅播放结束事件
        // TODO: 16/4/11 控制播放循环
        bus {
            subscribe(String::class.java) {
                if(it.equals("Finished")) {

                }
            }
        }

        // 异步获取歌曲剩余时间
        async() {
            while(view != null) {
                uiThread {
                    val time = getTimedText()
                    if(time != null)
                        view?.onTimeChanged(time)
                }

                Thread.sleep(500)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if(savedInstanceState != null) {
            // 恢复现场
            this.view?.onMurmursChanged(murmurs, playingMurmurs)
            this.view?.onSongChanged(playingSong!!)

        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /**
     * 保存现场
     */
    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putParcelable("user", user)
        outState?.putParcelableArrayList("murmurs", murmurs)
        outState?.putParcelableArrayList("playingMurmurs", playingMurmurs)
        outState?.putParcelable("playingSong", playingSong)

        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        view = null
        super.onDestroyView()
    }

    override fun stopAll() {
        // 暂停歌曲和所有白噪音
        MusicService.instance?.pauseSong()
        MusicService.instance?.stopAllMurmurs()
    }

    /**
     * 登陆豆瓣
     */
    override fun login(email: String, pwd: String) {
        DoubanModel.login(email, pwd).bind().subscribe({
            user = it
            view?.onLoginSuccess()
            fetchData()

        }, {
            when(it) {
                is DoubanException -> view?.showToast(it.err)
                else -> {
                    if(it.message != null)
                        view?.showToast(it.message!!)
                }
            }

            view?.onLoginFailed()
        })
    }

    /**
     * 获取歌曲和白噪音
     */
    private fun fetchData() {
        Observable.combineLatest(MurmurModel.getMurmurs(), DoubanModel.nextSong(user!!), {
            murmurs, song ->
            Pair(murmurs, song)

        }).bind().subscribe({
            murmurs.addAll(it.first)

            val selectedMurmurs = SettingModel.loadSelectedMurmursIDs()
            if(selectedMurmurs == null) {
                // 随机选择两个白噪音
                playingMurmurs.addAll(murmurs.randomPick(2))
                SettingModel.saveSelectedMurmursIDs(playingMurmurs.map { it.id })

            } else {
                // 读取之前保存的白噪音设置
                playingMurmurs.addAll(murmurs.filter { selectedMurmurs.contains(it.id) })
            }

            MusicService.instance?.playMurmurs(playingMurmurs)
            view?.onMurmursChanged(murmurs, playingMurmurs)

            playingSong = it.second
            MusicService.instance?.playSong(playingSong!!)
            view?.onSongChanged(playingSong!!)

        }, errorHandler)
    }

    /**
     * 切换白噪音
     */
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

        SettingModel.saveSelectedMurmursIDs(playingMurmurs.map { it.id })

        MusicService.instance?.playMurmurs(playingMurmurs)
        view?.onMurmursChanged(murmurs, playingMurmurs)
    }

    /**
     * 切换歌曲
     */
    override fun nextSong() {
        DoubanModel.nextSong(user!!).bind().subscribe({
            playingSong = it
            MusicService.instance?.playSong(playingSong!!)
            view?.onSongChanged(playingSong!!)
        }, errorHandler)
    }

    /**
     * 获取当前歌曲剩余时间
     */
    private fun getTimedText(): String? {
        var text: String? = null
        MusicService.instance?.apply {
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

    /**
     * 通用错误处理
     */
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