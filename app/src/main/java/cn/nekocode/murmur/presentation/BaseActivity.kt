package cn.nekocode.murmur.presentation

import android.content.Context
import cn.nekocode.kotgo.component.presentation.BaseActivity
import cn.nekocode.kotgo.component.presentation.SingleFragmentActivity
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

/**
 * Created by nekocode on 3/13/16.
 */
abstract class MyBaseActivity: BaseActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
}

abstract class MySingleFragmentActivity: SingleFragmentActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
}