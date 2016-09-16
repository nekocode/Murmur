package cn.nekocode.murmur.ui

import android.content.Context
import android.os.Bundle
import cn.nekocode.kotgo.component.ui.FragmentActivity
import cn.nekocode.murmur.ui.main.MainFragment
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class MainActivity: FragmentActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun afterOnCreate(savedInstanceState: Bundle?) {
        super.afterOnCreate(savedInstanceState)

        window.setBackgroundDrawable(null)

        if (savedInstanceState == null)
            MainFragment.push(this)
    }
}