package cn.nekocode.murmur.data

import cn.nekocode.murmur.data.DO.douban.ClientId
import cn.nekocode.murmur.data.DO.douban.Session
import cn.nekocode.murmur.data.DO.douban.SongS
import cn.nekocode.murmur.data.service.Api.DoubanFM
import cn.nekocode.murmur.data.service.Api.DoubanPush
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

    @Test
    fun testDoubanApis() {
        // Test login()
        val session = TestSubscriber<Session>().apply {

            DoubanToken.API.login("syfyw@qq.com", "110110zxc")
                    .toBlocking().subscribe(this)

        }.checkErr().onNextEvents[0]

        Assert.assertTrue(session.accessToken != null)
        val auth = "Bearer ${session.accessToken}"


        // Test getRedHeartSongs()
        val songIds = TestSubscriber<SongS.Ids>().apply {

            DoubanFM.API.getRedHeartSongs(auth)
                    .toBlocking().subscribe(this)

        }.checkErr().onNextEvents[0]

        Assert.assertTrue(songIds.songs.size != 0)
        val vaildSongIds = songIds.songs.filter { id -> id.playable }


        // Test registerDevice()
        val clientId = TestSubscriber<ClientId>().apply {

            DoubanPush.API.registerDevice()
                    .toBlocking().subscribe(this)

        }.checkErr().onNextEvents[0]

        Assert.assertTrue(!clientId.id.isNullOrBlank())


        // FIXME
        // Test getSong()
//        val song = TestSubscriber<SongS.Song>().apply {
//
//            DoubanFM.API.getSong(auth, vaildSongIds[0].sid)
//                    .toBlocking().subscribe(this)
//
//        }.checkErr().onNextEvents[0]
//
//        Assert.assertTrue(!song.id.isNullOrBlank())
    }

    fun <T> TestSubscriber<T>.checkErr(): TestSubscriber<T> {
        if (this.onErrorEvents.size != 0) {
            Assert.fail(this.onErrorEvents[0].toString())
        }

        return this
    }
}