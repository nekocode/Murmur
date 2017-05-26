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
        println(session)
        Assert.assertTrue(session != null)
    }

    @Test
    fun testRelogin() {
        val session = DoubanService.login(BuildConfig.TEST_USER, BuildConfig.TEST_PWD).blockingFirst()
        println(session)
        val newSeesion = DoubanService.relogin(session).blockingFirst()
        println(newSeesion)
        Assert.assertTrue(newSeesion != null)
    }

    @Test
    fun testGetSongs() {
        val session = DoubanService.login(BuildConfig.TEST_USER, BuildConfig.TEST_PWD).blockingFirst()
        val songs = DoubanService.getSongs(session).blockingFirst()
        println(songs)
        Assert.assertTrue(songs.isNotEmpty())
    }
}