package cn.nekocode.murmur.data.push

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import cn.nekocode.murmur.data.service.DoubanService
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by nekocode on 16/9/16.
 */
class DoubanMqtt : MqttCallback {
    companion object {
        const val SERVER_URI = "ssl://push.douban.com:4392"
        const val KEEP_ALIVE_INTERVAL = 290

        val CLIENT_ID: String
            get() {
                try {
                    return JSONObject().apply {

                        put("device_id", DoubanService.PUSH_DEVICE_ID)
                        put("ver", 2)
                        put("sdk_ver", 212)
                        put("sdk_pkg", "com.douban.radio")
                        put("os_api", Build.VERSION.SDK_INT)
                        put("os_ver", Build.VERSION.RELEASE)
                        put("os_rom", "android")
                        put("vendor", Build.MANUFACTURER)
                        put("model", Build.MODEL)
                        put("net", "WIFI")
                        put("apps", JSONArray().apply { put("com.douban.radio") })

                    }.toString()

                } catch (th: Throwable) {
                    return ""
                }
            }
    }

    val client: MqttAndroidClient

    constructor(context: Context) {
        client = MqttAndroidClient(context.applicationContext, SERVER_URI, CLIENT_ID)
        client.setCallback(this)
    }

    fun connect() {
        val options = MqttConnectOptions().apply {
            isAutomaticReconnect = true
            isCleanSession = true
            keepAliveInterval = KEEP_ALIVE_INTERVAL
        }

        client.connect(options, null, object: IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Log.e("MQTT", asyncActionToken?.toString() ?: "")
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                // 无效客户机标志
                Log.e("MQTT", asyncActionToken?.toString() ?: "")
            }

        })
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
    }

    override fun connectionLost(cause: Throwable?) {
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {
    }
}