package cn.nekocode.murmur.common

import cn.nekocode.kotgo.component.presentation.Presenter
import cn.nekocode.kotgo.component.rx.bindLifecycle
import cn.nekocode.kotgo.component.rx.onUI
import rx.Observable

/**
 * Created by nekocode on 16/3/22.
 */
abstract class MyPresenter(open val view: BaseViewInterface): Presenter(view) {
    fun <T> Observable<T>.bind(): Observable<T> {
        return this.onUI().bindLifecycle(view)
    }
}