package cn.nekocode.murmur.data

import cn.nekocode.murmur.data.service.LeanCloudService
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class LeanCloudServiceTest {

    @Before
    fun setUp() {
        DataLayer.init(RuntimeEnvironment.application)
    }

    @Test
    fun testGetMurmurs() {
        val murmurs = LeanCloudService.getMurmurs().blockingFirst()
        print(murmurs)
        Assert.assertTrue(murmurs.isNotEmpty())
    }
}