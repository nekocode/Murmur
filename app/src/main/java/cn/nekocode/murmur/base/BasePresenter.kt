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

package cn.nekocode.murmur.base

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trello.rxlifecycle2.components.RxFragment

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
abstract class BasePresenter<V> : RxFragment(), IContextProvider {
    private var view: V? = null


    abstract fun onViewCreated(view: V, savedInstanceState: Bundle?)

    final override fun onCreateView(
            inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = view ?: return null
        onViewCreated(view, savedInstanceState)
        return null
    }

    /**
     * Headless-fragment will not call this method
     */
    final override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        view = null
        super.onDestroyView()
    }

    override fun getContext(): Context =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) super.getContext() else activity

    fun setView(view: Any) {
        this.view = view as V
    }

    fun view(): V? = view
}