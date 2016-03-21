package cn.nekocode.murmur.presentation

import android.content.Context
import cn.nekocode.murmur.presentation.main.MainActivity
import org.jetbrains.anko.intentFor

fun Context.gotoMainPage() {
    startActivity(intentFor<MainActivity>())
}


