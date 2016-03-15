package cn.nekocode.murmur.presentation.main

import android.app.AlertDialog
import android.content.Context
import cn.nekocode.murmur.App
import cn.nekocode.murmur.presentation.MySingleFragmentActivity
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class MainActivity: MySingleFragmentActivity() {
    override val toolbarLayoutId = null
    override val fragmentClass = MainFragment::class.java

    override fun afterCreate() {
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this).setMessage("Are you want to exit?")
        .setNegativeButton("No", {
            dialog, which ->

        }).setPositiveButton("Yes", {
            dialog, which ->
            App.musicSerivice?.pauseSong()
            App.musicSerivice?.stopAllMurmurs()
            super.onBackPressed()
        }).show()
    }
}
