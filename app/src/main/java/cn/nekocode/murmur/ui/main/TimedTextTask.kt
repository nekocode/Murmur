package cn.nekocode.murmur.ui.main

import cn.nekocode.murmur.service.MusicService
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by nekocode on 16/4/12.
 */
object TimedTextTask {
    var view: Contract.View? = null

    fun start(_view: Contract.View?) {
        view = _view

        // 异步获取歌曲剩余时间
        doAsync {
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

    fun interrupt() {
        view = null
    }

    /**
     * 获取当前歌曲剩余时间
     */
    private fun getTimedText(): String? {
        var text: String? = null
        MusicService.instance?.apply {
            if(!isSongPlaying())
                return@apply

            val rest = getRestTime()

            val m = rest / 60
            val s = rest % 60

            if(m != 0 && s != 0)
                text = "$m:$s"
        }

        return text
    }
}