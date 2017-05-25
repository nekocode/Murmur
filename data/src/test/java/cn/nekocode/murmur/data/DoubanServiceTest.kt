package cn.nekocode.murmur.data

import cn.nekocode.murmur.data.service.DoubanService
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class DoubanServiceTest {

    @Before
    fun setUp() {
        DataLayer.init(RuntimeEnvironment.application)
    }

    @Test
    fun testLogin() {
        val session = DoubanService.login(BuildConfig.TEST_USER, BuildConfig.TEST_PWD).blockingFirst()
        print(session)
        Assert.assertTrue(session != null)
    }

    @Test
    fun testGetSongs() {
        val session = DoubanService.login(BuildConfig.TEST_USER, BuildConfig.TEST_PWD).blockingFirst()
        val songs = DoubanService.getSongs(session).blockingFirst()
        print(songs)
        Assert.assertTrue(songs.isNotEmpty())
    }
}