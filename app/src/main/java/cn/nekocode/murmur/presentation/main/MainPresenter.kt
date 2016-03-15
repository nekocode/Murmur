package cn.nekocode.murmur.presentation.main

import android.widget.Toast
import cn.nekocode.kotgo.component.presentation.Presenter
import cn.nekocode.kotgo.component.rx.RxLifecycle
import cn.nekocode.kotgo.component.rx.bindLifecycle
import cn.nekocode.kotgo.component.rx.onUI
import cn.nekocode.murmur.App
import cn.nekocode.murmur.data.dto.DoubanSong
import cn.nekocode.murmur.data.dto.Murmur
import cn.nekocode.murmur.data.model.DoubanSongModel
import cn.nekocode.murmur.data.model.MurmurModel
import cn.nekocode.murmur.util.Util.randomPick
import rx.Observable
import rx.Subscription


class MainPresenter(val view: MainPresenter.ViewInterface): Presenter(view) {
    interface ViewInterface: RxLifecycle.Getter {
        fun murmursChange(murmurs: List<Murmur>)
        fun songChange(song: DoubanSong)
    }

    val errorHandler: (Throwable)->Unit = {
        Toast.makeText(App.instance, it.message, Toast.LENGTH_SHORT).show()
    }

    fun init() {
        fetechMurmurs()
        nextSong()
    }

    fun fetechMurmurs() {
        val oldSubscription = MurmurModel.getMurmurs().bind(this).subscribe({
            val murmurs = it.randomPick(2)
            App.musicSerivice?.playMurmurs(murmurs)
            view.murmursChange(murmurs)
        }, errorHandler)

        oldSubscription.isUnsubscribed
    }

    var oldSubscription: Subscription? = null
    fun nextSong() {
        if(oldSubscription != null && oldSubscription!!.isUnsubscribed) {
            oldSubscription?.unsubscribe()
        }

        oldSubscription = DoubanSongModel.nextSong().bind(this).subscribe({
            App.musicSerivice?.playSong(it)
            view.songChange(it)
        }, errorHandler)
    }

    fun <T> Observable<T>.bind(lifecycler: RxLifecycle.Getter): Observable<T> {
        return this.onUI().bindLifecycle(lifecycler)
    }
}