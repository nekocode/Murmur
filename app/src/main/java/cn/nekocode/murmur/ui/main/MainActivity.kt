package cn.nekocode.murmur.ui.main

import cn.nekocode.murmur.App
import cn.nekocode.murmur.common.MySingleFragmentActivity
import org.jetbrains.anko.alert

class MainActivity: MySingleFragmentActivity<MainFragment>() {
    override fun onBackPressed() {
        alert("Are you want to exit?") {
            negativeButton("No") {  }

            positiveButton("Yes") {
                super.onBackPressed()
            }
        }.show()
    }

    override fun onDestroy() {

        super.onDestroy()
    }
}
