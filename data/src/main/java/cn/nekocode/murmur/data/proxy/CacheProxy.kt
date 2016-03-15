package cn.nekocode.murmur.data.proxy

import android.util.Log
import rx.Observable
import java.io.IOException
import java.net.InetAddress
import java.net.ServerSocket
import java.net.UnknownHostException
import kotlin.properties.Delegates

/**
 * Created by nekocode on 3/15/16.
 */
class CacheProxy {
    companion object {
        const val LOG_TAG = "CacheProxy"

        private val instance by lazy {
            CacheProxy()
        }

        fun getProxyURL(url: String): String {
            return "http://127.0.0.1:%d/%s".format(instance.proxyServer.port, url)
        }
    }

    val proxyServer by lazy {
        ProxyServer()
    }

    class ProxyServer: Runnable {
        var socket: ServerSocket by Delegates.notNull()
        var port: Int = 0

        constructor() {
            try {
                socket = ServerSocket(port, 0, InetAddress.getByAddress(byteArrayOf(127, 0, 0, 1)))
                socket.soTimeout = 5000
                port = socket.localPort

                Log.d(LOG_TAG, "port $port obtained");
            } catch (e: UnknownHostException) {
                Log.e(LOG_TAG, "Error initializing server", e);
            } catch (e: IOException) {
                Log.e(LOG_TAG, "Error initializing server", e);
            }
        }

        override fun run() {

        }
    }
}