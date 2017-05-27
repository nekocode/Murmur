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

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.KeyEvent
import android.widget.EditText
import cn.nekocode.murmur.BuildConfig
import cn.nekocode.murmur.R
import com.github.yamamotoj.pikkel.Pikkel
import com.github.yamamotoj.pikkel.PikkelDelegate
import org.jetbrains.anko.*

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
class LoginFragment : DialogFragment(), Pikkel by PikkelDelegate() {
    var emailText by state<String?>(null)
    var pwdText by state<String?>(null)
    var emailEdit: EditText? = null
    var pwdEdit: EditText? = null
    var callback: Callback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is Callback) {
            callback = context
        }
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (activity is Callback) {
                callback = activity
            }
        }
    }

    override fun onDetach() {
        callback = null
        super.onDetach()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        restoreInstanceState(savedInstanceState)
        isCancelable = false

        val dialog = AlertDialog.Builder(activity).apply {
            setView(activity.UI {
                verticalLayout {
                    padding = dip(30)

                    val emailEdit = editText {
                        hintResource = R.string.email
                        textSize = 14f
                        freezesText = true
                    }
                    this@LoginFragment.emailEdit = emailEdit

                    val pwdEdit = editText {
                        hintResource = R.string.password
                        textSize = 14f
                        freezesText = true
                    }
                    this@LoginFragment.pwdEdit = pwdEdit

                    emailText?.let {
                        emailEdit.setText(it)
                        emailEdit.setSelection(it.length)
                    }
                    pwdText?.let {
                        if (it.isNotBlank()) {
                            pwdEdit.setText(it)
                            pwdEdit.setSelection(it.length)
                            pwdEdit.requestFocus()
                        }
                    }

                    if (BuildConfig.DEBUG) {
                        if (emailText.isNullOrBlank() && pwdText.isNullOrBlank()) {
                            // 在测试模式下，自动填入测试的账号密码
                            emailEdit.setText(cn.nekocode.murmur.data.BuildConfig.TEST_USER)
                            pwdEdit.setText(cn.nekocode.murmur.data.BuildConfig.TEST_PWD)
                            emailEdit.setSelection(emailEdit.length())
                            pwdEdit.setSelection(pwdEdit.length())
                            pwdEdit.requestFocus()
                        }
                    }
                }
            }.view)

            setPositiveButton(R.string.login) { _, _ -> }

            setOnKeyListener { _, keyCode, keyEvent ->
                if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_DOWN) {
                    callback?.onBackPressed()
                    true
                } else {
                    false
                }
            }
        }.create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val email = emailEdit?.text.toString().trim()
                val pwd = pwdEdit?.text.toString()

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    toast(R.string.email_invaild)

                } else if (TextUtils.isEmpty(pwd)) {
                    toast(R.string.password_invaild)

                } else {
                    callback?.onLoginClicked(email, pwd)
                    dialog.dismiss()
                }
            }
        }

        return dialog
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState ?: return

        emailText = emailEdit?.text?.toString() ?: ""
        pwdText = pwdEdit?.text?.toString() ?: ""
        saveInstanceState(outState)
    }

    interface Callback {
        fun onLoginClicked(email: String, pwd: String)
        fun onBackPressed()
    }
}