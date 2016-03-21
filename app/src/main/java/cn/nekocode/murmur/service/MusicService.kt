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
    inner class MusicServiceBinder: Binder() {
        val service = this@MusicService
    }
    override fun onBind(intent: Intent?): IBinder = MusicServiceBinder()

    val playingSong = SongPlayer(null, MediaPlayer())
    var stopSong = false
    val playingMurmurs = hashMapOf<Murmur, MediaPlayer>()
    var stopMurmurs = false

    fun playSong(song: DoubanSong) {
        stopSong = false

        if(song != playingSong.song) {
            playingSong.song = song

            val player = playingSong.player
            player.reset()
            player.setDataSource(song.url)
            player.prepareAsync()
            player.setOnPreparedListener {
                if(!stopSong)
                    it.start()
            }

            player.setOnCompletionListener {
                RxBus.send("Play finished")
            }

        } else {
            if(!playingSong.player.isPlaying) {
                playingSong.player.start()
            }
        }
    }

    fun pauseSong() {
        stopSong = true
        playingSong.player.pause()
    }

    fun playMurmurs(murmurs: List<Murmur>) {
        playingMurmurs.forEach {
            if(it.key !in murmurs) {
                it.value.stop()
            }
        }

        stopMurmurs = false
        murmurs.forEach {
            if(it !in playingMurmurs) {
                val player = MediaPlayer()
                playingMurmurs[it] = player

                player.isLooping = true
                player.setDataSource(it.file.url)
                player.prepareAsync()
                player.setOnPreparedListener {
                    if(!stopMurmurs)
                        it.start()
                }
            }
        }
    }

    fun stopAllMurmurs() {
        stopMurmurs = true
        playingMurmurs.forEach {
            it.value.stop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playingSong.player.release()

        playingMurmurs.forEach {
            it.value.release()
        }
        playingMurmurs.clear()
    }

    data class SongPlayer(var song: DoubanSong?, var player: MediaPlayer)
}