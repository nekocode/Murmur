package cn.nekocode.murmur.ui.main

import android.os.AsyncTask
import cn.nekocode.murmur.service.MusicService
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
class TimingTextTask : AsyncTask<Unit, String, Unit> {
    var view: Contract.View? = null

    constructor(view: Contract.View?) : super() {
        this.view = view
    }

    override fun onCancelled() {
        view = null
        super.onCancelled()
    }

    override fun doInBackground(vararg params: Unit): Unit {
        while(view != null) {
            val time = MusicService.instance?.let {

                if(!it.isSongPlaying())
                    return@let null

                val rest = it.getRestTime()
                val m = rest / 60
                val s = rest % 60

                if(m != 0 && s != 0) "$m:$s" else null
            }

            if (time != null)
                publishProgress(time)

            Thread.sleep(500)
        }
    }

    override fun onProgressUpdate(vararg values: String?) {
        val time = values[0]!!
        view?.onTimeChanged(time)
    }

}
