package cn.nekocode.murmur.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import cn.nekocode.kotgo.component.rx.RxBus
import cn.nekocode.murmur.data.dto.DoubanSong
import cn.nekocode.murmur.data.dto.Murmur

/**
 * Created by nekocode on 3/15/16.
 */
class MusicService: Service() {
    companion object {
        var instance: MusicService? = null
    }

    inner class MusicServiceBinder: Binder() {
        val service = this@MusicService
    }
    override fun onBind(intent: Intent?): IBinder {
        return MusicServiceBinder()
    }

    private val songPlayer = SongPlayer(null, MediaPlayer())
    private val murmurPlayers = hashMapOf<Murmur, MediaPlayer>()
    private var stopSong = false
    private var stopMurmurs = false

    fun playSong(song: DoubanSong) {
        stopSong = false

        if(song != songPlayer.song) {
            songPlayer.song = song

            val player = songPlayer.player
            player.reset()
            player.isLooping = true
            player.setDataSource(song.url)
            player.prepareAsync()
            player.setOnPreparedListener {
                if(!stopSong) {
                    it.start()
                    RxBus.send("Prepared")
                }
            }

            player.setOnCompletionListener {
                RxBus.send("Finished")
            }

        } else {
            if(!songPlayer.player.isPlaying) {
                songPlayer.player.start()
            }
        }
    }

    fun pauseSong() {
        stopSong = true
        songPlayer.player.pause()
    }

    fun playMurmurs(murmurs: List<Murmur>) {
        // 停止被去掉的白噪音
        murmurPlayers.filter {
            !murmurs.contains(it.key)

        }.forEach {
            it.value.stop()
        }

        // 开始播放新选中的白噪音
        stopMurmurs = false
        murmurs.filter {
            !murmurPlayers.containsKey(it)

        }.forEach {
            val player = murmurPlayers[it] ?: MediaPlayer()
            murmurPlayers[it] = player

            player.reset()
            player.isLooping = true
            player.setDataSource(it.file.url)
            player.prepareAsync()
            player.setOnPreparedListener {
                if(!stopMurmurs)
                    it.start()
            }
        }
    }

    fun stopAllMurmurs() {
        stopMurmurs = true
        murmurPlayers.forEach {
            it.value.stop()
        }
    }

    fun isSongPlaying(): Boolean = songPlayer.song != null && songPlayer.player.isPlaying

    fun getRestTime(): Int = (songPlayer.player.duration - songPlayer.player.currentPosition) / 1000

    override fun onDestroy() {
        super.onDestroy()
        songPlayer.player.release()

        murmurPlayers.forEach {
            it.value.release()
        }
        murmurPlayers.clear()
    }

    data class SongPlayer(var song: DoubanSong?, var player: MediaPlayer)
}