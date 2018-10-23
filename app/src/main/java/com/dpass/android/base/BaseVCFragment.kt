package com.dpass.android.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseVCFragment : BaseFragment() {

    abstract var layoutResId: Int

    abstract fun setUpViews(root: View, savedInstanceState: Bundle?)

    abstract fun workOnViewFirstCreated(savedInstanceState: Bundle?)

    abstract fun work(savedInstanceState: Bundle?)

    protected var rootView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (rootView == null){
            rootView = inflater.inflate(layoutResId, container, false)
            setUpViews(rootView!!, savedInstanceState)
            workOnViewFirstCreated(savedInstanceState)
        }
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        work(savedInstanceState)
    }

}