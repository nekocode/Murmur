package cn.nekocode.murmur.ui

import android.app.Activity
import cn.nekocode.murmur.ui.main.MainActivity
import org.jetbrains.anko.intentFor

object Navigator {
    fun gotoMainPage(act: Activity) {
        act.apply {
            startActivity(act.intentFor<MainActivity>())
        }
    }
}


