package cn.nekocode.murmur

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import cn.nekocode.murmur.data.DataLayer
import cn.nekocode.murmur.service.MusicService
import com.squareup.leakcanary.LeakCanary
import org.jetbrains.anko.intentFor
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import kotlin.properties.Delegates

class App: Application() {
    companion object {
        var instance by Delegates.notNull<App>()
        var musicSerivice: MusicService? = null
    }

    val connection = object: ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {
            musicSerivice = null
        }

        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            musicSerivice = (p1 as MusicService.MusicServiceBinder).service
        }
    }

    fun bindService() {
        bindService(intentFor<MusicService>(), connection, Context.BIND_AUTO_CREATE)
    }

    fun unBindService() {
        unbindService(connection)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        LeakCanary.install(this)
        DataLayer.hook(this)
        bindService()

        CalligraphyConfig.initDefault(
                CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Champagne&Limousines Bold.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        )
    }

    override fun onTerminate() {
        unBindService()
        super.onTerminate()
    }
}
