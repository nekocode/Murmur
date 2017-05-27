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

package cn.nekocode.murmur.screen.main

import android.os.Bundle
import cn.nekocode.itempool.Item
import cn.nekocode.itempool.ItemPool
import cn.nekocode.murmur.PlayerService
import cn.nekocode.murmur.R
import cn.nekocode.murmur.base.BasePresenter
import cn.nekocode.murmur.data.DO.douban.Session
import cn.nekocode.murmur.data.DO.douban.Song
import cn.nekocode.murmur.data.service.DoubanService
import cn.nekocode.murmur.item.SongItem
import com.github.yamamotoj.pikkel.Pikkel
import com.github.yamamotoj.pikkel.PikkelDelegate
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
class MainPresenter : BasePresenter<Contract.View>(), Contract.Presenter, Pikkel by PikkelDelegate() {
    var session by state<Session?>(null)
    var songs by state<ArrayList<Song>?>(null)
    var playingPosition by state(-1)
    var itemPool = ItemPool()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        restoreInstanceState(savedInstanceState)
        itemPool.addType(SongItem::class.java)

        // 监听点击
        itemPool.onEvent(SongItem::class.java) { event ->
            when (event.action) {
                Item.EVENT_ITEM_CLICK -> {
                    // 点击歌曲
                    playSong(event.item.viewHolder.adapterPosition)
                }
            }
        }
    }

    override fun onViewCreated(view: Contract.View, savedInstanceState: Bundle?) {
        // 更新标题栏
        updateTitles(songs?.getOrNull(playingPosition))

        // 获取歌曲
        if (songs != null) {
            // 已经获取过数据
            Observable.just(songs!!)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .setSongsToView()

        } else {
            session = DoubanService.getSavedSession()

            // 检查是否需要登陆
            if (session == null) {
                view.showLoginDialog()
            } else {
                // 刷新 Token
                DoubanService.relogin(session ?: return)
                        .subscribeOn(Schedulers.io())
                        .bindUntilEvent(this@MainPresenter, FragmentEvent.DESTROY_VIEW)
                        .subscribe({
                            session = it
                            DoubanService.getSongs(it)
                                    .subscribeOn(Schedulers.io())
                                    .setSongsToView()

                        }, this::onLoginError)
            }
        }
    }

    /**
     * 登录错误时，重新展示对话框
     */
    fun onLoginError(ignored: Throwable) {
        view()?.toast(getString(R.string.toast_login_failed))
        view()?.showLoginDialog()
    }

    /**
     * 点击了登录按钮之后
     */
    override fun onLoginClicked(email: String, pwd: String) {
        // TODO progress
        DoubanService.login(email, pwd)
                .subscribeOn(Schedulers.io())
                .bindUntilEvent(this@MainPresenter, FragmentEvent.DESTROY_VIEW)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    session = it
                    DoubanService.getSongs(it)
                            .subscribeOn(Schedulers.io())
                            .setSongsToView()

                }, this::onLoginError)
    }

    /**
     * 选择菜单
     */
    override fun onMenuSelected(id: Int) {
        when (id) {
            Contract.View.MENU_ID_ABOUT -> gotoAbout(context)
        }
    }

    /**
     * 展示歌曲列表
     */
    fun Observable<ArrayList<Song>>.setSongsToView() {
        this
                .observeOn(Schedulers.io())
                .map { list ->
                    songs = list
                    list.map { SongItem.VO.fromSong(it) }
                }
                .bindUntilEvent(this@MainPresenter, FragmentEvent.DESTROY_VIEW)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    itemPool.clear()
                    itemPool.addAll(it)
                    view()?.setAdapter(itemPool.adapter)
                }, this@MainPresenter::onError)
    }

    /**
     * 播放歌曲
     */
    fun playSong(position: Int) {
        val song = songs?.getOrNull(position)
        if (song != null) {
            PlayerService.instance?.playSong(song)
        } else {
            PlayerService.instance?.pauseSong()
        }

        /*
          对 View 的操作
         */
        val view = view() ?: return
        if (song != null) {
            view.showToobar(true)
            updateTitles(song)
            view.setFABStatus(Contract.View.FAB_STATUS_PAUSE)
            playingPosition = position

        } else {
            view.showToobar(true)
            updateTitles(null)
            playingPosition = -1
        }
    }

    /**
     * 暂停歌曲
     */
    fun pauseSong() {
        PlayerService.instance?.pauseSong()

        /*
          对 View 的操作
         */
        val view = view() ?: return
        view.setFABStatus(Contract.View.FAB_STATUS_RESUME)
    }

    /**
     * 设置 Toolbar 的标题
     */
    fun updateTitles(song: Song?) {
        val view = view() ?: return
        if (song != null) {
            view.setToolbarTitle(song.title)
            view.setToolbarSubtitle(song.artist)
        } else {
            view.setToolbarTitle(getString(R.string.app_name))
            view.setToolbarSubtitle("")
        }
    }

    /**
     * 点击 Toolbar 时
     */
    override fun onToolbarClicked() {
        if (playingPosition != -1) {
            view()?.scrollToPosition(playingPosition)
        }
    }

    /**
     * 点击 FAB 时
     */
    override fun onFABClicked(status: Int) {
        when (status) {
            Contract.View.FAB_STATUS_PAUSE -> {
                pauseSong()
            }

            Contract.View.FAB_STATUS_RESUME -> {
                if (playingPosition >= 0 && playingPosition < songs?.size ?: 0) {
                    // 在列表内的话
                    playSong(playingPosition)
                } else {
                    // TODO 根据循环模式选取歌曲播放
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        saveInstanceState(outState ?: return)
    }

    override fun onBackPressed() {
        PlayerService.instance?.pauseSong() // TODO
        PlayerService.instance?.stopAllMurmurs()
    }
}