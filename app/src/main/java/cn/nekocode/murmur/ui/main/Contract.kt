package cn.nekocode.murmur.ui.main

import cn.nekocode.murmur.data.dto.DoubanSong
import cn.nekocode.murmur.data.dto.Murmur

/**
 * Created by nekocode on 16/4/9.
 */
interface Contract {
    interface View {
        fun showLoginDialog()
        fun onLoginSuccess()
        fun onLoginFailed()

        fun onMurmursChanged(all: List<Murmur>, playing: List<Murmur>)
        fun onSongChanged(song: DoubanSong.Song)
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