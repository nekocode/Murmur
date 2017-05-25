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

package cn.nekocode.murmur.screen

import android.content.Context
import cn.nekocode.meepo.annotation.TargetClass
import cn.nekocode.meepo.Meepo
import cn.nekocode.murmur.screen.about.AboutActivity

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
interface UIRouter {

    companion object {
        val IMPL = Meepo.Builder().build().create(UIRouter::class.java)!!
    }

    @TargetClass(AboutActivity::class)
    fun gotoAbout(context: Context?) {
        IMPL.gotoAbout(context)
    }
}