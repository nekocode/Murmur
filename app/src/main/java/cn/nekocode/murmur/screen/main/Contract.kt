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

package cn.nekocode.murmur.screen.main

import android.support.v7.widget.RecyclerView
import cn.nekocode.itempool.ItemPool
import cn.nekocode.murmur.base.IPresenter
import cn.nekocode.murmur.base.IView

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
interface Contract {

    /**
     * View 的所有接口都是对 View 的操作
     */
    interface View : IView {
        companion object {
            const val FAB_STATUS_PAUSE = 0
            const val FAB_STATUS_RESUME = 1

            const val MENU_ID_ABOUT = 0
        }

        fun setAdapter(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>)
        fun setToolbarTitle(title: String)
        fun setToolbarSubtitle(subtitle: String)
        fun showToobar(show: Boolean)
        fun scrollToPosition(position: Int)
        fun setFABStatus(status: Int)
        fun showLoginDialog()
        fun hideLoginDialog()
    }

    /**
     * View 从来不应该主动让 Presenter 任何事，而是告诉 Presenter 发生了什么
     * 所以 Presenter 所有接口都是仅面向 View 事件的，都必须以 onXXX() 的命名
     */
    interface Presenter: IPresenter {
        fun onBackPressed()
        fun onToolbarClicked()
        fun onFABClicked(status: Int)
        fun onLoginClicked(email: String, pwd: String)
        fun onMenuSelected(id: Int)
    }
}