package cn.nekocode.murmur.ui.main

import cn.nekocode.murmur.data.DO.douban.SongS
import cn.nekocode.murmur.data.DO.Murmur

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
interface Contract {
    interface View {
        fun showLoginDialog()
        fun onLoginSuccess()
        fun onLoginFailed()

        fun onMurmursChanged(all: List<Murmur>, playing: List<Murmur>)
        fun onSongChanged(song: SongS.Song)
        fun onTimeChanged(timedText: String)

        fun showToast(msg: String)
    }

    interface Presenter {
        fun login(email: String, pwd: String)

        fun changeMurmur(murmur: Murmur, play: Boolean)
        fun nextSong()
        fun stopAll()
    }

}