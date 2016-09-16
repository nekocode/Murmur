package cn.nekocode.murmur.ui.main

import android.app.Activity
import android.os.Bundle
import cn.nekocode.kotgo.component.rx.RxBus
import cn.nekocode.kotgo.component.rx.bindLifecycle
import cn.nekocode.kotgo.component.rx.onUI
import cn.nekocode.kotgo.component.ui.BasePresenter
import cn.nekocode.murmur.data.DO.douban.SongS
import cn.nekocode.murmur.data.DO.douban.User
import cn.nekocode.murmur.data.DO.Murmur
import cn.nekocode.murmur.data.DO.douban.SongParcel
import cn.nekocode.murmur.data.exception.DoubanException
import cn.nekocode.murmur.data.repo.DoubanRepo
import cn.nekocode.murmur.data.repo.MurmurRepo
import cn.nekocode.murmur.data.repo.SettingRepo
import cn.nekocode.murmur.service.MusicService
import cn.nekocode.murmur.util.Util.randomPick
import rx.Observable
import java.util.*


class MainPresenter(): BasePresenter(), Contract.Presenter {
    var view: Contract.View? = null

    var user: User? = null
    val murmurs = ArrayList<Murmur>()
    val playingMurmurs = ArrayList<Murmur>()
    var playingSong: SongS.Song? = null

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
            playingSong = savedInstanceState.getParcelable<SongParcel>("playingSong").data

        } else {

            // 检查是否已经登录
            val cachedUser = DoubanRepo.getCachedUserInfo()
            if(cachedUser == null) {
                view?.showLoginDialog()
            } else {
                login(cachedUser.first, cachedUser.second)
            }

        }

        // 订阅播放结束事件
        // TODO: 16/4/11 控制播放循环
        RxBus.subscribe(String::class.java) {
            if(it.equals("Finished")) {

            }
        }

        // 异步获取歌曲剩余时间
        TimedTextTask.start(view)
    }

    override fun onVewCreated(savedInstanceState: Bundle?) {
        if(savedInstanceState != null) {
            // 恢复现场
            this.view?.onMurmursChanged(murmurs, playingMurmurs)
            this.view?.onSongChanged(playingSong!!)

        }
    }

    /**
     * 保存现场
     */
    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putParcelable("user", user)
        outState?.putParcelableArrayList("murmurs", murmurs)
        outState?.putParcelableArrayList("playingMurmurs", playingMurmurs)
        outState?.putParcelable("playingSong", SongParcel(playingSong))

        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        // 终止获取歌曲剩余时间的任务
        TimedTextTask.interrupt()
        super.onDestroyView()
    }

    /**
     * 停止所有歌曲和白噪音
     */
    override fun stopAll() {
        MusicService.instance?.pauseSong()
        MusicService.instance?.stopAllMurmurs()
    }

    /**
     * 登陆豆瓣
     */
    override fun login(email: String, pwd: String) {
        DoubanRepo.login(email, pwd).onUI().bindLifecycle(this).subscribe({
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
        Observable.combineLatest(MurmurRepo.getMurmurs(), DoubanRepo.nextSong(user!!), {
            murmurs, song ->
            Pair(murmurs, song)

        }).onUI().bindLifecycle(this).subscribe({
            murmurs.addAll(it.first)

            val selectedMurmurs = SettingRepo.loadSelectedMurmursIDs()
            if(selectedMurmurs == null) {
                // 随机选择两个白噪音
                playingMurmurs.addAll(murmurs.randomPick(2))
                SettingRepo.saveSelectedMurmursIDs(playingMurmurs.map { it.id })

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

        // 保存白噪音设置
        SettingRepo.saveSelectedMurmursIDs(playingMurmurs.map { it.id })

        // 播放白噪音
        MusicService.instance?.playMurmurs(playingMurmurs)
        view?.onMurmursChanged(murmurs, playingMurmurs)
    }

    /**
     * 切换歌曲
     */
    override fun nextSong() {
        DoubanRepo.nextSong(user!!).onUI().bindLifecycle(this).subscribe({
            playingSong = it
            MusicService.instance?.playSong(playingSong!!)
            view?.onSongChanged(playingSong!!)
        }, errorHandler)
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