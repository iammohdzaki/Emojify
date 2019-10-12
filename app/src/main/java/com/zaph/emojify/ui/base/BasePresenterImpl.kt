package com.zaph.emojify.ui.base

/**
 * Developer : Mohammad Zaki
 * Created On : 12-10-2019
 */

class BasePresenterImpl<V : BaseView> : BasePresenter<V> {

    private lateinit var mBaseView: V
    private var isViewAttached: Boolean = false


    override fun onAttach(baseView: V) {
        mBaseView = baseView
        isViewAttached = true
    }

    override fun onDeAttach() {
    }

    override fun getView(): V {
        return mBaseView
    }
}