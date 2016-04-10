package cn.nekocode.murmur.ui.main

import cn.nekocode.murmur.data.dto.DoubanSong
import cn.nekocode.murmur.data.dto.Murmur

/**
 * Created by nekocode on 16/4/9.
 */
interface Contract {
    interface View {
        fun showLoginDialog()
        fun loginSuccess()
        fun loginFailed()
        fun murmursChanged(all: List<Murmur>, playing: List<Murmur>)
        fun songChanged(song: DoubanSong)
        fun timeChanged(timedText: String)

        fun showToast(msg: String)
    }

    interface Presenter {
        fun changeMurmur(murmur: Murmur, play: Boolean)
        fun nextSong()
        fun login(email: String, pwd: String)
    }

}