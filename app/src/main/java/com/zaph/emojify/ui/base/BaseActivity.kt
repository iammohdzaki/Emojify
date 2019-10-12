package com.zaph.emojify.ui.base

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.zaph.emojify.R
import com.zaph.emojify.utils.FAILED
import com.zaph.emojify.utils.SUCCESS

/**
 * Developer : Mohammad Zaki
 * Created On : 12-10-2019
 */

open class BaseActivity : AppCompatActivity(),BaseView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun setOnClickListeners(onClickListener: View.OnClickListener, vararg views: View) {
        for (view in views)
            view.setOnClickListener(onClickListener)
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showToast(messageResId: Int) {
        showToast(getString(messageResId))
    }

    override fun showSnackbar(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
    }

    override fun showSnackbar(message: String, snackbarStatus: Int) {
        val restoreBar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val view = restoreBar.view
        when {
            SUCCESS == snackbarStatus -> view.setBackgroundColor(ContextCompat.getColor(this@BaseActivity, R.color.success))
            FAILED == snackbarStatus -> view.setBackgroundColor(ContextCompat.getColor(this@BaseActivity, R.color.red))
            else -> view.setBackgroundColor(ContextCompat.getColor(this@BaseActivity, R.color.colorAccent))
        }
        restoreBar.show()
    }

}