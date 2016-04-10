package cn.nekocode.murmur.common

import cn.nekocode.kotgo.component.rx.bindLifecycle
import cn.nekocode.kotgo.component.rx.onUI
import cn.nekocode.kotgo.component.ui.BasePresenter
import rx.Observable

/**
 * Created by nekocode on 16/3/22.
 */
abstract class MyPresenter(): BasePresenter() {
    fun <T> Observable<T>.bind(): Observable<T> {
        return this.onUI().bindLifecycle(this@MyPresenter)
    }
}