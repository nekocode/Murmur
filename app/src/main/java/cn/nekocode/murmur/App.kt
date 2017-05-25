/*
 * Copyright 2017. nekocode (nekocode.cn@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.nekocode.murmur

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import cn.nekocode.murmur.data.DataLayer
import org.jetbrains.anko.intentFor
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
class App : Application() {
    val connection = object : ServiceConnection {
        override fun onServiceDisconnected(ignored: ComponentName?) {
            PlayerService.instance = null
        }

        override fun onServiceConnected(ignored: ComponentName?, binder: IBinder?) {
            if (binder is PlayerService.Binder) {
                PlayerService.instance = binder.service
            }
        }
    }

    fun bindService() {
        bindService(intentFor<PlayerService>(), connection, Context.BIND_AUTO_CREATE)
    }

    fun unBindService() {
        unbindService(connection)
    }

    override fun onCreate() {
        super.onCreate()

        DataLayer.init(this)
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
