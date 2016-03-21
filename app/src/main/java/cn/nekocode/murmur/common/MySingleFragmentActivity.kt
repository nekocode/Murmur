package cn.nekocode.murmur.common

import android.content.Context
import cn.nekocode.kotgo.component.presentation.BaseFragment
import cn.nekocode.kotgo.component.presentation.SingleFragmentActivity
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

/**
 * Created by nekocode on 16/3/21.
 */
abstract class MySingleFragmentActivity<T: BaseFragment>: SingleFragmentActivity<T>() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
}