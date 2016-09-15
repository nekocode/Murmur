package cn.nekocode.murmur.data

import cn.nekocode.murmur.data.dto.DoubanSession
import cn.nekocode.murmur.data.dto.DoubanSong
import cn.nekocode.murmur.data.service.Api.DoubanFM
import cn.nekocode.murmur.data.service.Api.DoubanToken
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

import rx.observers.TestSubscriber

/**
 * Created by nekocode on 16/9/14.
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class DoubanServiceTest {

    @Before
    fun setUp() {
        DataLayer.hook(RuntimeEnvironment.application)
    }

//    private fun getMqttPingPi(): PendingIntent {
//        val intent = Intent("com.douban.artery.action.MQTT_PING")
//        intent.setClass(this, PushService::class.java)
//        return PendingIntent.getService(this, 0, intent, 134217728)
//    }
//
//    fun testMQtt() {
//        if (this.mMqtt == null) {
//            val pingSender = AndroidPingSender(this, getMqttPingPi())
//            val callback = MqttConnectionCallback(this)
//            this.mMqtt = MqttClient(this,
//                    MqttConfig(
//                            "ssl://push.douban.com:4392",
//                            290,
//                            ServiceUtils.getCurrentDeviceInfo(this, getMyDeviceId()),   // username
//                            pingSender,
//                            callback
//                    )
//            )
//
//        }
//        return this.mMqtt
//    }

    @Test
    fun testDoubanApis() {
        // Test login()
        val session = TestSubscriber<DoubanSession>().apply {

            DoubanToken.API.login("syfyw@qq.com", "110110zxc")
                    .toBlocking().subscribe(this)

        }.onNextEvents[0]

        Assert.assertTrue(session.accessToken != null)
        val auth = "Bearer ${session.accessToken}"

        // Test getRedHeartSongs()
        val songIds = TestSubscriber<DoubanSong.Ids>().apply {

            DoubanFM.API.getRedHeartSongs(auth)
                    .toBlocking().subscribe(this)

        }.onNextEvents[0]

        Assert.assertTrue(songIds.songs.size != 0)
        val vaildSongIds = songIds.songs.filter { id -> id.playable }


        // FIXME
        // Test getSong()
        val song = TestSubscriber<DoubanSong.Song>().apply {

            DoubanFM.API.getSong(auth, vaildSongIds[0].sid)
                    .toBlocking().subscribe(this)

            val t = this
            this.onErrorEvents

        }.onNextEvents[0]

        Assert.assertTrue(!song.id.isNullOrBlank())

    }
}