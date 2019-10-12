package com.zaph.emojify.ui.base

/**
 * Developer : Mohammad Zaki
 * Created On : 12-10-2019
 */

interface BasePresenter<V : BaseView> {

    /**
     * Indicates when the view has attached ( created )
     */
    fun onAttach(baseView: V)

    /**
     * Indicates when the view has detached ( destroyed )
     */
    fun onDeAttach()


    fun getView(): V

}