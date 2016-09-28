package cn.nekocode.murmur.ui

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import cn.nekocode.kotgo.component.ui.FragmentActivity
import cn.nekocode.murmur.ui.main.MainFragment
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
class MainActivity: FragmentActivity() {
    override fun onCreatePresenter(presenterFactory: PresenterFactory) {
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(null)

        if (savedInstanceState == null)
            MainFragment.push(this)
    }
}