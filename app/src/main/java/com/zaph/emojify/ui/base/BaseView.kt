package com.zaph.emojify.ui.base

import android.view.View
import androidx.annotation.StringRes

/**
 * Developer : Mohammad Zaki
 * Created On : 12-10-2019
 */

interface BaseView {

    fun showToast(@StringRes messageResId: Int)

    fun showToast(message: String)

    fun setOnClickListeners(onClickListener: View.OnClickListener, vararg views: View)

    fun showSnackbar(message: String)

    fun showSnackbar(message: String,snackbarStatus:Int)
}