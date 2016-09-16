package cn.nekocode.murmur.data

import cn.nekocode.murmur.data.DO.douban.Session
import cn.nekocode.murmur.data.DO.douban.SongS
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
import java.util.*

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

//            DoubanToken.API.login("", "")  // TODO Replace your username and password
            DoubanToken.API.login("syfyw@qq.com", "110110zxc")  // TODO Replace your username and password
                    .toBlocking().subscribe(this)

        }.checkErr().onNextEvents[0]

        Assert.assertTrue(session.accessToken != null)
        val auth = "Bearer ${session.accessToken}"


        // Test getRedHeartSongIds()
        val songIds = TestSubscriber<SongS.Ids>().apply {

            DoubanFM.API.getRedHeartSongIds(auth)
                    .toBlocking().subscribe(this)

        }.checkErr().onNextEvents[0]

        Assert.assertTrue(songIds.songs.size != 0)
        val idListStringBuilder = StringBuilder()
        songIds.songs.filter { id -> id.playable }.forEach { idListStringBuilder.append(it.sid).append("|") }
        val idListStr = idListStringBuilder.deleteCharAt(idListStringBuilder.length - 1).toString()


        // Test getSongs()
        val songs = TestSubscriber<ArrayList<SongS.Song>>().apply {

            DoubanFM.API.getSongs(auth, idListStr)
                    .toBlocking().subscribe(this)

        }.checkErr().onNextEvents[0]

        Assert.assertTrue(songs.size != 0)
    }

    fun <T> TestSubscriber<T>.checkErr(): TestSubscriber<T> {
        if (this.onErrorEvents.size != 0) {
            Assert.fail(this.onErrorEvents[0].toString())
        }

        return this
    }
}