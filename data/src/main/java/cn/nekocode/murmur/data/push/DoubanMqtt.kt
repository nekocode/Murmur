package cn.nekocode.murmur.data.push

import android.content.Context
import android.util.Log
import cn.nekocode.murmur.data.service.Api.DoubanPush
import cn.nekocode.murmur.data.service.DoubanService
import com.orhanobut.hawk.Hawk
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import rx.Observable
import rx.schedulers.Schedulers

/**
 * Created by nekocode on 16/9/16.
 */
class DoubanMqtt : MqttCallback {
    companion object {
        const val LOG_TAG = "DoubanMqtt"
    }

    val ctx: Context
    var client: MqttAndroidClient? = null

    constructor(context: Context) {
        ctx = context.applicationContext
    }

    // FIXME
    fun connect() {
        if (client == null) {
            // 尝试从缓存中取 client_id
            Observable.fromCallable { Hawk.get<String>("client_id") }
                    .flatMap {
                        if (it != null) {
                            Observable.just(it)
                        } else {
                            DoubanPush.API.registerDevice().map {
                                Hawk.put("client_id", it)
                                it.id!!
                            }
                        }
                    }
                    .subscribeOn(Schedulers.io())
                    .onErrorReturn {
                        Log.e(LOG_TAG, it.toString())
                        "aNdQJubLL94LO"     // 出错返回默认 client_id
                    }
                    .subscribe {
                        client = MqttAndroidClient(ctx, DoubanService.PUSH_SERVER_URI, it)
                        client?.setCallback(this)

                        // 创建 Client 后重连
                        connect()
                    }

        } else {
            val options = MqttConnectOptions().apply {
                isAutomaticReconnect = true
                isCleanSession = true
                keepAliveInterval = DoubanService.MQTT_KEEP_ALIVE_IN_SECONDS
                userName = DoubanService.getDeviceInfo()
            }

            client?.connect(options, null, object: IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.e("MQTT", asyncActionToken?.toString() ?: "")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.e("MQTT", asyncActionToken?.toString() ?: "")
                }

            })
        }
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        Log.e(LOG_TAG, message?.payload?.toString() ?: "")
    }

    override fun connectionLost(cause: Throwable?) {
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {
    }
}