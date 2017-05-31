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

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import cn.nekocode.murmur.R
import cn.nekocode.murmur.base.BaseActivity
import com.github.yamamotoj.pikkel.Pikkel
import com.github.yamamotoj.pikkel.PikkelDelegate
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.onClick

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
class MainActivity : BaseActivity(), Contract.View, LoginFragment.Callback, Pikkel by PikkelDelegate() {
    companion object {
        const val TAG_LOGIN_FRG = "login"
    }

    var fabStatus by state(Contract.View.FAB_STATUS_RESUME)
    var loginFrg: LoginFragment? = null
    var loadingDialog: ProgressDialog? = null

    var presenter: Contract.Presenter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        restoreInstanceState(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        toolbar.onClick { presenter?.onToolbarClicked() }
        setFABStatus(fabStatus)
        resumeOrPauseButton.onClick { presenter?.onFABClicked(fabStatus) }
        recyclerView.layoutManager = LinearLayoutManager(this)

        loginFrg = fragmentManager.findFragmentByTag(TAG_LOGIN_FRG) as LoginFragment?
        loadingDialog = ProgressDialog(this).apply {
            setCancelable(false)
        }
    }

    override fun onCreatePresenter(presenterFactory: PresenterFactory) {
        presenter = presenterFactory.createOrGet(MainPresenter::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val item = menu.add(Menu.NONE, Contract.View.MENU_ID_ABOUT, Menu.NONE, R.string.menu)
        item.setIcon(R.mipmap.ic_launcher)
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        presenter?.onMenuSelected(item.itemId)
        return super.onOptionsItemSelected(item)
    }

    override fun setAdapter(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {
        recyclerView.adapter = adapter
    }

    override fun setToolbarTitle(title: String) {
        toolbar.title = title
    }

    override fun setToolbarSubtitle(subtitle: String) {
        toolbar.subtitle = subtitle
    }

    override fun showToobar(show: Boolean) {
        appbar.setExpanded(show, true)
    }

    override fun scrollToPosition(position: Int) {
        recyclerView.smoothScrollToPosition(position) // FIXME
    }

    override fun setFABStatus(status: Int) {
        fabStatus = status

        when (status) {
            Contract.View.FAB_STATUS_PAUSE -> {
                val d = ContextCompat.getDrawable(this, R.drawable.pause)
                resumeOrPauseButton.setImageDrawable(d)
            }

            Contract.View.FAB_STATUS_RESUME -> {
                val d = ContextCompat.getDrawable(this, R.drawable.resume)
                resumeOrPauseButton.setImageDrawable(d)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        saveInstanceState(outState ?: return)
    }

    override fun onBackPressed() {
        alert(R.string.ensure_exit) {
            negativeButton(R.string.no) {
            }

            positiveButton(R.string.yes) {
                presenter?.onBackPressed()
                super.onBackPressed()
            }
        }.show()
    }

    override fun showLoginDialog() {
        val dialog = loginFrg ?: LoginFragment()
        loginFrg = dialog
        if (!dialog.isAdded) {
            loginFrg?.show(fragmentManager, TAG_LOGIN_FRG)
        }
    }

    override fun hideLoginDialog() {
        loginFrg?.dismiss()
    }

    override fun onLoginClicked(email: String, pwd: String) {
        presenter?.onLoginClicked(email, pwd)
    }

    override fun showLoadingDialog(message: String) {
        val dialog = loadingDialog ?: return
        dialog.setMessage(message)
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    override fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }
}
