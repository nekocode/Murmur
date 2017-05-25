/*
 * Copyright 2017. nekocode (nekocode.cn@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.nekocode.murmur

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import cn.nekocode.murmur.data.DO.douban.Song
import cn.nekocode.murmur.data.DO.leancloud.Murmur

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
class PlayerService : Service() {
    companion object {
        var instance: PlayerService? = null
    }

    class MurmurPlayer(var murmur: Murmur?) : MediaPlayer()
    class SongPlayer(var song: Song?) : MediaPlayer()
    class Binder(val service: PlayerService): android.os.Binder()

    private val songPlayer = SongPlayer(null)
    private val murmurPlayers = hashMapOf<String, MurmurPlayer>()
    private var stopSong = false
    private var stopMurmurs = false

    override fun onBind(intent: Intent?): IBinder {
        return Binder(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        // 释放所有 MediaPlayer
        songPlayer.release()
        murmurPlayers.forEach { (_, palyer) ->
            palyer.release()
        }
        murmurPlayers.clear()
    }

    /**
     * （重新）播放歌曲
     */
    fun playSong(song: Song) {
        stopSong = false

        if(song != songPlayer.song) {
            songPlayer.song = song
            songPlayer.reset()
            songPlayer.isLooping = true
            songPlayer.setDataSource(song.url)
            songPlayer.prepareAsync()
            songPlayer.setOnPreparedListener {
                if(!stopSong) {
                    it.start()
                }
            }

            songPlayer.setOnCompletionListener {
            }

        } else {
            if(!songPlayer.isPlaying) {
                songPlayer.start()
            }
        }
    }

    /**
     * 暂停歌曲
     */
    fun pauseSong() {
        stopSong = true
        songPlayer.pause()
    }

    /**
     * 播放白噪音
     */
    fun playMurmurs(murmurs: List<Murmur>) {
        murmurPlayers.forEach { id, player ->
            val oldMurmur = murmurs.find { it.id == id }
            if (oldMurmur == null) {
                // 停止不在列表中的白噪音
                player.stop()
            } else {
                // 重新播放已停止的白噪音
                player.reset()
                player.isLooping = true
                player.setDataSource(oldMurmur.file.url)
                player.prepareAsync()
                player.setOnPreparedListener {
                    if(!stopMurmurs){
                        it.start()
                    }
                }
            }
        }

        // 开始播放新选中的白噪音
        stopMurmurs = false
        murmurs.filter {
            !murmurPlayers.contains(it.id)

        }.forEach {
            val player = MurmurPlayer(it)
            murmurPlayers[it.id] = player

            player.reset()
            player.isLooping = true
            player.setDataSource(it.file.url)
            player.prepareAsync()
            player.setOnPreparedListener {
                if(!stopMurmurs) {
                    it.start()
                }
            }
        }
    }

    /**
     * 停止播放所有白噪音
     */
    fun stopAllMurmurs() {
        stopMurmurs = true
        murmurPlayers.forEach { (_, palyer) ->
            palyer.stop()
        }
    }

    /**
     * 是否正在播放歌曲
     */
    fun isPlayingSong(): Boolean = songPlayer.isPlaying

    /**
     * 返回歌曲剩余播放时间（秒）
     */
    fun getRestTime(): Int = (songPlayer.duration - songPlayer.currentPosition) / 1000
}