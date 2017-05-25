/*
 * Copyright 2017. nekocode (nekocode.cn@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.nekocode.murmur.util

import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
object Drawables {

    /**
     * 可点击的背景
     */
    fun clickableBackground(pressedColor: Int): Drawable {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Ripple 效果
            val stateList = ColorStateList(
                    arrayOf(intArrayOf(android.R.attr.state_pressed), intArrayOf()),
                    intArrayOf(pressedColor, pressedColor)
            )
            return RippleDrawable(stateList, ColorDrawable(0), ColorDrawable(0xFFFFFFFF.toInt()))

        } else {
            // 正常效果
            val drawable = StateListDrawable()
            drawable.addState(intArrayOf(android.R.attr.state_pressed), ColorDrawable(pressedColor))
            return drawable
        }
    }
}